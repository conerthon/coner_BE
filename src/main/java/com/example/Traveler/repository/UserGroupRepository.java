package com.example.Traveler.repository;

import com.example.Traveler.domain.User;
import com.example.Traveler.domain.TravelGroup;
import com.example.Traveler.domain.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {

    // 특정 유저와 특정 그룹으로 매핑 정보가 있는지 확인 (중복 가입 방지용)
    boolean existsByUserAndTravelGroup(User user, TravelGroup travelGroup);
    List<UserGroup> findByTravelGroup(TravelGroup travelGroup);
    List<UserGroup> findByUser(User user);
}
