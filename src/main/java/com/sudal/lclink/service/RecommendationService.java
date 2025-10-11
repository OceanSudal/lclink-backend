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
        System.out.println("===========================================");
        System.out.println("🎯 getAiRecommendations 시작");
        System.out.println("===========================================");

        // 1. 비교 대상 데이터 준비
        List<CargoItem> shipperCargos = cargoItemRepository.findAllByUser_Company_CompanyType("SHIPPER");
        System.out.println("📦 DB에서 조회한 SHIPPER 화물 개수: " + shipperCargos.size());

        List<CargoItemDto> shipperCargosDto = shipperCargos.stream()
                .map(CargoItemDto::from)
                .collect(Collectors.toList());

        if (shipperCargosDto.isEmpty()) {
            System.err.println("⚠️ SHIPPER 화물 데이터가 없습니다!");
            return List.of();
        }

        // 데이터가 너무 많으면 제한 (토큰 제한 방지)
        int maxItems = 20;
        if (shipperCargosDto.size() > maxItems) {
            System.out.println("⚠️ 데이터가 " + shipperCargosDto.size() + "개로 너무 많아 " + maxItems + "개로 제한합니다.");
            shipperCargosDto = shipperCargosDto.stream()
                    .limit(maxItems)
                    .collect(Collectors.toList());
        }

        // 2. 프롬프트 생성
        String prompt = createRecommendationPrompt(shipperCargosDto);
        System.out.println("📝 프롬프트 길이: " + prompt.length() + " 글자");
        System.out.println("📝 프롬프트 미리보기: " + prompt.substring(0, Math.min(200, prompt.length())) + "...");

        // 3. Gemini API 호출
        System.out.println("🚀 Gemini API 호출 시작...");
        String llmResponseJson = geminiClient.getRecommendation(prompt);
        System.out.println("📥 Gemini 응답 받음: " + (llmResponseJson != null ? "성공" : "null"));

        if (llmResponseJson == null) {
            System.err.println("❌ Gemini 응답이 null입니다!");
            return List.of();
        }

        System.out.println("📄 원본 응답: " + llmResponseJson);

        try {
            // 4. Gemini의 JSON 응답 파싱
            llmResponseJson = llmResponseJson.replace("```json", "").replace("```", "").trim();
            System.out.println("🔧 정제된 응답: " + llmResponseJson);

            Map<String, List<Map<String, Object>>> responseMap = objectMapper.readValue(llmResponseJson, new TypeReference<>() {});
            List<Map<String, Object>> recommendations = responseMap.get("recommendations");

            if (recommendations == null || recommendations.isEmpty()) {
                System.err.println("⚠️ recommendations가 비어있습니다!");
                return List.of();
            }

            System.out.println("✅ 추천 개수: " + recommendations.size());

            List<Integer> recommendedIds = recommendations.stream()
                    .map(rec -> (Integer) rec.get("itemId"))
                    .collect(Collectors.toList());

            System.out.println("🔍 추천된 itemId 목록: " + recommendedIds);

            // 5. 추천된 ID로 실제 화물 정보를 DB에서 조회하여 반환
            List<CargoItemDto> result = cargoItemRepository.findAllByItemIdIn(recommendedIds).stream()
                    .map(CargoItemDto::from)
                    .collect(Collectors.toList());

            System.out.println("✅ 최종 반환 개수: " + result.size());
            System.out.println("===========================================");

            return result;

        } catch (Exception e) {
            System.err.println("❌ Gemini 응답 파싱 중 에러: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private String createRecommendationPrompt(List<CargoItemDto> shipperCargos) {
        String shipperCargosJson;
        try {
            shipperCargosJson = objectMapper.writeValueAsString(shipperCargos);
        } catch (Exception e) {
            System.err.println("❌ JSON 변환 에러: " + e.getMessage());
            shipperCargosJson = "[]";
        }

        return String.format(
                "You are an expert LCL freight contract matching specialist for a forwarder. " +
                        "From the 'List of Shipper Contracts' below, select the 3 most attractive contracts. " +
                        "The criteria for attractiveness are: 1) CBM and weight are not too small, 2) origin and destination are clear, and 3) the departure date (etd) is not too far in the future. " +
                        "For each recommendation, summarize the reason in one sentence. " +
                        "You MUST respond ONLY in the following JSON format. Do not add any other explanations.\n\n" +
                        "### List of Shipper Contracts:\n%s\n\n" +
                        "### Output Format:\n" +
                        "```json\n" +
                        "{\"recommendations\": [{\"itemId\": 1, \"reason\": \"example reason\"}]}\n" +
                        "```",
                shipperCargosJson
        );
    }
}