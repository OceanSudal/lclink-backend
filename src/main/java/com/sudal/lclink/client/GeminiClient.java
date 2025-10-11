package com.sudal.lclink.client;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GeminiClient implements InitializingBean {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent";

    public GeminiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("===========================================");
        System.out.println("🔍 API Key 디버깅");
        System.out.println("API Key 값: [" + apiKey + "]");
        System.out.println("API Key가 null인가? " + (apiKey == null));
        System.out.println("API Key가 비어있는가? " + (apiKey != null && apiKey.isEmpty()));
        System.out.println("API Key 길이: " + (apiKey != null ? apiKey.length() : 0));

        if (apiKey != null && apiKey.length() > 10) {
            System.out.println("API Key 시작: " + apiKey.substring(0, 10));
        }
        System.out.println("===========================================");
    }

    // API 키 확인 (추가)
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${GEMINI_API_KEY:}")) {
            System.err.println("⚠️ GEMINI API KEY가 설정되지 않았습니다!");
            System.err.println("현재 값: " + apiKey);
        } else {
            System.out.println("✅ Gemini API Key 로드 완료 (길이: " + apiKey.length() + ")");
            // 보안을 위해 전체 키는 출력하지 않음
            System.out.println("키 시작 부분: " + apiKey.substring(0, Math.min(10, apiKey.length())) + "...");
        }
    }

    @SuppressWarnings("unchecked")
    public String getRecommendation(String prompt) {
        // API 키 재확인 (추가)
        if (apiKey == null || apiKey.isEmpty()) {
            System.err.println("❌ API Key가 없습니다!");
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-goog-api-key", apiKey);

        // 요청 로깅 추가
        System.out.println("📤 Gemini API 호출 시작...");
        System.out.println("📝 프롬프트: " + prompt);

        // Gemini API가 요구하는 요청 본문 형식
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(GEMINI_URL, entity, Map.class);

            System.out.println("📥 전체 응답: " + response);

            // Gemini 응답에서 실제 텍스트 부분 추출
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> responseContent = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) responseContent.get("parts");
                    String result = (String) parts.get(0).get("text");
                    System.out.println("✅ Gemini API 응답 성공");
                    return result;
                }
            }
            System.err.println("⚠️ Gemini 응답 형식이 예상과 다릅니다.");
        } catch (Exception e) {
            System.err.println("❌ Gemini API 호출 중 에러 발생: " + e.getMessage());
            e.printStackTrace(); // 상세한 스택 트레이스 출력
            return null;
        }
        return null;
    }

}