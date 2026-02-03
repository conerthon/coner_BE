package com.example.Traveler.repository;

import com.example.Traveler.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByUserAndTravelGroupAndPlace(User user, TravelGroup group, Place place);

    // 투표한 전체 사람 수
    long countByTravelGroupAndPlace(TravelGroup group, Place place);

    @Query("SELECT COUNT(v) FROM Vote v WHERE v.travelGroup = :group AND v.place = :place AND v.isLike = true")
    long countConfirmedVotes(@Param("group") TravelGroup group, @Param("place") Place place);

    // 누가 어떤 투표를 했는지 전체 리스트 반환
    List<Vote> findAllByTravelGroupAndPlace(TravelGroup group, Place place);
}