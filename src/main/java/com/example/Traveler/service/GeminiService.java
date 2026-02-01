package com.example.Traveler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}") // application.properties에 키 설정 필요
    private String apiKey;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";

    public String getSummaryFromAI(String title, String description) {
        // AI에게 보낼 질문(Prompt)
        String prompt = String.format(
            "장소 이름: %s, 설명: %s. 이 정보를 바탕으로 이 장소가 어떤 곳인지 한 문장으로 요약하고, 관련 태그 3개를 #태그 형식으로 적어줘.",
            title, description
        );

        // 여기서는 간단하게 RestTemplate을 사용한 예시입니다.
        RestTemplate restTemplate = new RestTemplate();

        // JSON 요청 바디 구성 (Gemini API 규격)
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
            ))
        );

        try {
            Map response = restTemplate.postForObject(GEMINI_API_URL + apiKey, requestBody, Map.class);

            if (response != null) {
                // Gemini의 복잡한 JSON 구조 파고들기
                List candidates = (List) response.get("candidates");
                Map candidate = (Map) candidates.get(0);
                Map content = (Map) candidate.get("content");
                List parts = (List) content.get("parts");
                Map part = (Map) parts.get(0);

                return part.get("text").toString();
            }
            return "요약 데이터를 가져오지 못했습니다.";
        } catch (Exception e) {
            return "요약을 생성할 수 없습니다.";
        }
    }
}
