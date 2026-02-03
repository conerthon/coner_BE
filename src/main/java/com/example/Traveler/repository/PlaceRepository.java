package com.example.Traveler.repository;

import com.example.Traveler.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByUserId(Long userId); // 유저 ID로 장소 리스트 찾기
}
