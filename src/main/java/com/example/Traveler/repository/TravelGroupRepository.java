package com.example.Traveler.repository;

import com.example.Traveler.domain.TravelGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/* save(group) : 새로운 여행 그룹 저장 또는 수정

findById(id) : ID값으로 그룹 조회

findAll() : 모든 그룹 목록 조회

delete(group) : 특정 그룹 삭제
*/

public interface TravelGroupRepository extends JpaRepository<TravelGroup, Long> {
    Optional<TravelGroup> findByInviteCode(String inviteCode); // 초대코드로 유저 찾기
    boolean existsByInviteCode(String inviteCode); // 초대코드 중복 확인
}
