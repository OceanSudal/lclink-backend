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
        // No operations
    }

    @PostConstruct
    public void init() {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${gemini.api.key}")) {
            System.err.println("❌ ERROR: Gemini API Key is not configured or is incorrect. (Current Value: " + apiKey + ")");
        } else {
            System.out.println("✅ INFO: Gemini API Key loaded successfully.");
        }
    }

    @SuppressWarnings("unchecked")
    public String getRecommendation(String prompt) {
        if (apiKey == null || apiKey.isEmpty() || apiKey.equals("${gemini.api.key}")) {
            System.err.println("❌ ERROR: Skipping Gemini API call because the API Key is invalid.");
            return null;
        }

        String fullUrl = GEMINI_URL + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Gemini API Request Body construction
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("DEBUG: Sending request to Gemini API...");

            // This is the point where the actual network call is made
            Map<String, Object> response = restTemplate.postForObject(fullUrl, entity, Map.class);

            System.out.println("DEBUG: Received response from Gemini API.");

            // Extract the text part from the Gemini response structure
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
            // 👈 The critical addition: logging the full exception stack trace
            System.err.println("❌ FATAL ERROR: Exception occurred during Gemini API call.");
            System.err.println("   Cause: " + e.getMessage());
            e.printStackTrace();
            return null;
        }

        // Handling cases where response is received but structure is unexpected (e.g., block reason)
        System.err.println("❌ ERROR: Gemini API response was received but did not contain candidates (e.g., safety block or empty response).");
        return null;
    }
}