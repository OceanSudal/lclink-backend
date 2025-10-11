package com.sudal.lclink.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sudal.lclink.client.GeminiClient;
import com.sudal.lclink.dto.CargoItemDto;
import com.sudal.lclink.entity.CargoItem;
import com.sudal.lclink.repository.CargoItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final CargoItemRepository cargoItemRepository;
    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public List<CargoItemDto> getAiRecommendations(String forwarderUserId) {
        // 1. 비교 대상 데이터 준비: 모든 '화주(SHIPPER)'의 화물 목록을 가져옵니다.
        List<CargoItem> shipperCargos = cargoItemRepository.findAllByUser_Company_CompanyType("SHIPPER");

        List<CargoItemDto> shipperCargosDto = shipperCargos.stream()
                .map(CargoItemDto::from)
                .collect(Collectors.toList());

        if (shipperCargosDto.isEmpty()) {
            return List.of();
        }

        // 2. 프롬프트 생성
        String prompt = createRecommendationPrompt(shipperCargosDto);

        // 3. Gemini API 호출
        String llmResponseJson = geminiClient.getRecommendation(prompt);

        if (llmResponseJson == null) {
            return List.of();
        }

        try {
            // 4. Gemini의 JSON 응답 파싱
            llmResponseJson = llmResponseJson.replace("```json", "").replace("```", "").trim();
            Map<String, List<Map<String, Object>>> responseMap = objectMapper.readValue(llmResponseJson, new TypeReference<>() {});
            List<Map<String, Object>> recommendations = responseMap.get("recommendations");

            List<Integer> recommendedIds = recommendations.stream()
                    .map(rec -> (Integer) rec.get("itemId"))
                    .collect(Collectors.toList());

            // 5. 추천된 ID로 실제 화물 정보를 DB에서 조회하여 반환
            return cargoItemRepository.findAllByItemIdIn(recommendedIds).stream()
                    .map(CargoItemDto::from)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Gemini 응답 파싱 중 에러: " + e.getMessage());
            return List.of();
        }
    }

    private String createRecommendationPrompt(List<CargoItemDto> shipperCargos) {
        String shipperCargosJson;
        try {
            shipperCargosJson = objectMapper.writeValueAsString(shipperCargos);
        } catch (Exception e) {
            shipperCargosJson = "[]";
        }

        return String.format(
                "You are an expert LCL freight contract matching specialist for a forwarder." +
                        "From the 'List of Shipper Contracts' below, select the 3 most attractive contracts." +
                        "The criteria for attractiveness are: 1) CBM and weight are not too small, 2) origin and destination are clear, and 3) the departure date (etd) is not too far in the future." +
                        "For each recommendation, summarize the reason in one sentence." +
                        "You MUST respond ONLY in the following JSON format. Do not add any other explanations." +
                        "### List of Shipper Contracts:\n%s\n\n" +
                        "### Output Format:\n" +
                        "```json\n" +
                        "{\"recommendations\": [{\"itemId\": <recommended_itemId>, \"reason\": \"<reason_for_recommendation>\"}]}\n" +
                        "```",
                shipperCargosJson
        );
    }
}