package com.example.Traveler.service;

import com.example.Traveler.domain.Place;
import com.example.Traveler.domain.User;
import com.example.Traveler.repository.PlaceRepository;
import com.example.Traveler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    public Place captureUrl(String url, Long userId) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("ID가 " + userId + "인 유저를 찾을 수 없습니다."));

        try {
            var document = org.jsoup.Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            String title = document.select("meta[property=og:title]").attr("content");
            if (title.isEmpty()) title = document.title();

            String description = document.select("meta[property=og:description]").attr("content");
            String image = document.select("meta[property=og:image]").attr("content");

            Place place = new Place();
            place.setUrl(url);
            place.setTitle(title);
            place.setDescription(description);
            place.setImageUrl(image);
            place.setUser(user);

            // 만약 설명이 너무 짧거나 비어있다면 AI
            if (description == null || description.length() < 10) {
                String aiResult = geminiService.getSummaryFromAI(title, description);

                place.setDescription(aiResult);

                String tags = extractTags(aiResult);
                place.setKeyword(tags);
            }

            return placeRepository.save(place);
        } catch (Exception e) {
            throw new RuntimeException("URL 분석 중 오류 발생: " + e.getMessage());
        }
    }

    // 태그만 쏙쏙 뽑아주는 도우미 메서드
    private String extractTags(String text) {
        if (text == null) return null;
        StringBuilder tags = new StringBuilder();
        String[] words = text.split("\\s+"); // 공백 기준으로 나누기
        for (String word : words) {
            if (word.startsWith("#")) {
                tags.append(word).append(" ");
            }
        }
        return tags.toString().trim();
    }
}
