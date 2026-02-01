package com.example.Traveler.repository;

import com.example.Traveler.domain.Place;
import com.example.Traveler.domain.TravelGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    // 기본적인 저장, 조회 기능은 JpaRepository가 다 해줍니다!
    // 해당 그룹에 포함되는 장소만 뽑아오기 (투표 후보들)
    List<Place> findAllByTravelGroup(TravelGroup group);
}
