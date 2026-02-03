package com.example.Traveler.repository;

import com.example.Traveler.domain.Place;
import com.example.Traveler.domain.TravelGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByUserId(Long userId); // 유저 ID로 장소 리스트 찾기
    // 해당 그룹에 포함되는 장소만 뽑아오기 (투표 후보들)
    List<Place> findAllByTravelGroup(TravelGroup group);
}
