package com.example.Traveler.controller;

import com.example.Traveler.domain.Place;
import com.example.Traveler.domain.User;
import com.example.Traveler.service.PlaceService;
import jakarta.servlet.http.HttpSession;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    // 1. 장소 요약 및 저장 (AI 연동)
    @PostMapping("/capture")
    public ResponseEntity<Place> capture(@RequestBody PlaceRequest request) {
        // AI가 분석한 결과를 DB에 저장하고 그 객체를 반환
        return ResponseEntity.ok(placeService.captureUrl(request.getUrl(), request.getUserId(), request.getGroupId()));
    }

    // 2. 전체 장소 목록 조회
    @GetMapping
    public ResponseEntity<List<Place>> getAllPlaces() {
        return ResponseEntity.ok(placeService.findAll());
    }

    // 3. 특정 유저가 저장한 장소 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Place>> getPlacesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(placeService.findByUserId(userId));
    }

    // 4. 장소 삭제
    @DeleteMapping("/{placeId}")
    public ResponseEntity<String> deletePlace(@PathVariable Long placeId) {
        placeService.delete(placeId);
        return ResponseEntity.ok("장소가 성공적으로 삭제되었습니다. ID: " + placeId);
    }
}

// 요청 데이터를 담는 DTO
@Data
class PlaceRequest {
    private String url;
    private Long userId;
    private Long groupId;
}
