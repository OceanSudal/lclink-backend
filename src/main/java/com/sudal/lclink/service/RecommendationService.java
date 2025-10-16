package com.sudal.lclink.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudal.lclink.client.GeminiClient;
import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.dto.CargoRequestDto;
import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.entity.CargoRequest;
import com.sudal.lclink.repository.CargoItemRepository;
import com.sudal.lclink.repository.CargoRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CargoRequestRepository cargoRequestRepository; // 👈 변경
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public List<CargoRequestDto> getAiRecommendations(String forwarderUserId) {
        // 1. OPEN 상태의 cargo-request만 가져오기 (아직 견적을 받지 않은 요청)
        List<CargoRequest> openRequests = cargoRequestRepository
                .findAllByRequestStatus("OPEN"); // 👈 변경

        System.out.println("OPEN status cargo-request size: " + openRequests.size());

        if (openRequests.isEmpty()) {
            return List.of();
        }

        // 2. DTO 변환
        List<CargoRequestDto> requestsDto = openRequests.stream()
                .map(CargoRequestDto::from)
                .collect(Collectors.toList());

        // 3. Gemini API 호출
        String prompt = createRecommendationPrompt(requestsDto);
        String llmResponseJson = geminiClient.getRecommendation(prompt);

        System.out.println("DEBUG: LLM response origin: " + llmResponseJson); // 👈 디버깅 로그 추가!

        if (llmResponseJson == null) {
            return List.of();
        }

        try {
            llmResponseJson = llmResponseJson.replace("```json", "").replace("```", "").trim();
            Map<String, List<Map<String, Object>>> responseMap = objectMapper.readValue(llmResponseJson, new TypeReference<>() {});
            List<Map<String, Object>> recommendations = responseMap.get("recommendations");

            List<Integer> recommendedIds = recommendations.stream()
                    .map(rec -> (Integer) rec.get("requestId")) // 👈 itemId → requestId
                    .collect(Collectors.toList());

            // 4. 추천된 cargo-request 반환
            return cargoRequestRepository.findAllByRequestIdIn(recommendedIds).stream()
                    .map(CargoRequestDto::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Gemini response parsing error: " + e.getMessage());
            return List.of();
        }
    }

    private String createRecommendationPrompt(List<CargoRequestDto> requests) {
        String requestsJson;
        try {
            requestsJson = objectMapper.writeValueAsString(requests);
        } catch (Exception e) {
            requestsJson = "[]";
        }

        return String.format(
                "You are an expert LCL freight contract matching specialist for a forwarder." +
                        "From the 'List of Shipper Contracts' below, select the 3 most attractive contracts." +
                        "The criteria for attractiveness are: 1) CBM and weight are not too small, 2) origin and destination are clear, and 3) the departure date (etd) is not too far in the future." +
                        "For each recommendation, summarize the reason in one sentence." +
                        "You MUST respond ONLY in the following JSON format. Do not add any other explanations." +
                        "You must recommend at least one contract." +
                        "### List of Shipper Contracts:\n%s\n\n" +
                        "### Output Format:\n" +
                        "```json\n" +
                        "{\"recommendations\": [{\"itemId\": <recommended_itemId>, \"reason\": \"<reason_for_recommendation>\"}]}\n" +
                        "```",
                requestsJson
        );
    }
}