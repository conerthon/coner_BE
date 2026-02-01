package com.example.Traveler.repository;

import com.example.Traveler.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndTravelGroupAndPlace(User user, TravelGroup group, Place place);

    // 특정 장소의 좋아요 개수 합산
    long countByTravelGroupAndPlaceAndIsLikeTrue(TravelGroup group, Place place);

    // 찬성/반대 상관없이 투표한 전체 인원수 확인
    long countByTravelGroupAndPlace(TravelGroup group, Place place);

    // 누가 어떤 투표를 했는지 전체 리스트 반환
    List<Vote> findAllByTravelGroupAndPlace(TravelGroup group, Place place);
}