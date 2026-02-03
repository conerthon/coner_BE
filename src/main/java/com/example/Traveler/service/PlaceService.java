package com.example.Traveler.service;

import com.example.Traveler.domain.Place;
import com.example.Traveler.domain.TravelGroup;
import com.example.Traveler.domain.User;
import com.example.Traveler.repository.PlaceRepository;
import com.example.Traveler.repository.TravelGroupRepository;
import com.example.Traveler.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    private final TravelGroupRepository travelGroupRepository;

    /**
     * 1. URL 요약 및 저장 (AI 연동)
     */
    public Place captureUrl(String url, Long userId, Long groupId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));

        TravelGroup group = travelGroupRepository.findById(groupId) // groupRepository 주입 필요!
                .orElseThrow(() -> new RuntimeException("그룹을 찾을 수 없습니다."));

        try {
            // 1. 이미지 추출 (og:image 메타태그 활용)
            var document = org.jsoup.Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();
            String imageUrl = document.select("meta[property=og:image]").attr("content");

            // 2. AI 분석 호출
            String aiRawResponse = geminiService.getSummaryFromURL(url);

            // AI 응답 파싱 (파이프라인 '|' 기준)
            String[] parts = aiRawResponse.split("\\|");

            Place place = new Place();
            place.setUrl(url);
            place.setUser(user);
            place.setTravelGroup(group);
            place.setImageUrl(imageUrl);

            // 파싱 데이터 매핑
            place.setTitle(parts.length > 0 ? parts[0].trim() : "알 수 없는 장소");
            place.setDescription(parts.length > 1 ? parts[1].trim() : "설명이 없습니다.");
            place.setKeyword(parts.length > 2 ? parts[2].trim() : "#여행 #추천");

            return placeRepository.save(place);

        } catch (Exception e) {
            // 에러 발생 시 롤백 방지 및 최소 데이터 저장
            Place fallbackPlace = new Place();
            fallbackPlace.setUrl(url);
            fallbackPlace.setTitle("장소 정보 불러오기 실패");
            fallbackPlace.setDescription("URL 분석 중 오류가 발생했습니다: " + e.getMessage());
            fallbackPlace.setUser(user);
            fallbackPlace.setTravelGroup(group);
            return placeRepository.save(fallbackPlace);
        }
    }

    /**
     * 2. 모든 장소 조회
     */
    @Transactional(readOnly = true)
    public List<Place> findAll() {
        return placeRepository.findAll();
    }

    /**
     * 3. 특정 유저의 장소 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Place> findByUserId(Long userId) {
        // Repository에 findByUserId 또는 findByUser_Id 메서드가 선언되어 있어야 합니다.
        return placeRepository.findByUserId(userId);
    }

    /**
     * 4. 장소 삭제
     */
    public void delete(Long placeId) {
        if (!placeRepository.existsById(placeId)) {
            throw new RuntimeException("삭제할 장소가 존재하지 않습니다.");
        }
        placeRepository.deleteById(placeId);
    }
}
