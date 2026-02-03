package com.example.Traveler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiurl;

    public String getSummaryFromURL(String url) {
        // AI에게 URL을 직접 읽고 지정된 규격으로 응답하라고 지시
        String prompt = String.format(
            "다음 URL의 콘텐츠를 분석해줘: %s. " +
            "응답은 반드시 [장소제목]|[한 줄 요약]|[#태그1 #태그2 #태그3] 형식으로만 답해줘. " +
            "다른 설명은 절대 하지마.", url
        );

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(Map.of("text", prompt))
            ))
        );

        try {
            String finalUrl = apiurl + apiKey;
            Map response = restTemplate.postForObject(finalUrl, requestBody, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List candidates = (List) response.get("candidates");
                Map candidate = (Map) candidates.get(0);
                Map content = (Map) candidate.get("content");
                List parts = (List) content.get("parts");
                Map part = (Map) parts.get(0);

                return part.get("text").toString().trim();
            }
            return "분석 실패|내용을 가져올 수 없습니다.|#여행";
        } catch (Exception e) {
            System.err.println("Gemini 호출 에러: " + e.getMessage());
            return "에러 발생|AI 서비스 연결 실패|#에러";
        }
    }
}
