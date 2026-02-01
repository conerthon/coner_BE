package com.example.Traveler.service;

import com.example.Traveler.domain.Place;
import com.example.Traveler.domain.User; // 1. User 엔티티 임포트 확인!
import com.example.Traveler.repository.PlaceRepository;
import com.example.Traveler.repository.UserRepository; // 2. UserRepository 임포트 추가
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository; // 3. 의존성 주입 추가!

    public Place captureUrl(String url, Long userId) {
        // 4. 유저 확인 로직
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("ID가 " + userId + "인 유저를 찾을 수 없습니다."));

        try {
            // "사람인 척" 하기 위한 User-Agent 설정 추가 (403/404 방지)
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
            place.setUser(user); // 5. 장소에 유저 정보 연결!

            return placeRepository.save(place);
        } catch (Exception e) {
            throw new RuntimeException("URL 분석 중 오류 발생: " + e.getMessage());
        }
    }
}
