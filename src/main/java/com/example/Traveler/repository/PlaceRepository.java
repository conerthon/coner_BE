package com.example.Traveler.repository;

import com.example.Traveler.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    // 기본적인 저장, 조회 기능은 JpaRepository가 다 해줍니다!
}
