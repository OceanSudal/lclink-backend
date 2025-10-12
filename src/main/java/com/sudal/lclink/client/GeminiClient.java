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

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent";

    public GeminiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 모든 로깅 구문 삭제됨
    }

    // API 키 확인 (추가)
    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${GEMINI_API_KEY:}")) {
            // 모든 로깅 구문 삭제됨
        } else {
            // 모든 로깅 구문 삭제됨
        }
    }

    @SuppressWarnings("unchecked")
    public String getRecommendation(String prompt) {
        // API 키 재확인 (추가)
        if (apiKey == null || apiKey.isEmpty()) {
            return null;
        }

        String fullUrl = GEMINI_URL + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("x-goog-api-key", apiKey);

        // Gemini API가 요구하는 요청 본문 형식
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            Map<String, Object> response = restTemplate.postForObject(fullUrl, entity, Map.class);

            // Gemini 응답에서 실제 텍스트 부분 추출
            if (response != null && response.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    Map<String, Object> responseContent = (Map<String, Object>) candidate.get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) responseContent.get("parts");
                    String result = (String) parts.get(0).get("text");
                    return result;
                }
            }
        } catch (Exception e) {
            // 모든 로깅 구문 삭제됨
            return null;
        }
        return null;
    }

}