package com.example.Traveler.service;

import com.example.Traveler.domain.GroupRole;
import com.example.Traveler.domain.TravelGroup;
import com.example.Traveler.domain.User;
import com.example.Traveler.domain.UserGroup;
import com.example.Traveler.repository.TravelGroupRepository;
import com.example.Traveler.repository.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final TravelGroupRepository travelGroupRepository;
    private final UserGroupRepository userGroupRepository;

    // 방장(host) 그룹 생성 메소드
    @Transactional
    public TravelGroup createGroup(String groupName, User user) {
        // 초대 코드 생성
        String inviteCode = generateUniqueInviteCode();

        // 여행 그룹 생성
        TravelGroup group = TravelGroup.builder()
                .name(groupName)
                .inviteCode(inviteCode)
                .build();
        TravelGroup savedGroup = travelGroupRepository.save(group);

        // 호스트 저장 (방장)
        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .travelGroup(savedGroup)
                .role(GroupRole.HOST)
                .build();
        userGroupRepository.save(userGroup);

        return savedGroup;
    }

    // 멤버 초대 메소드
    @Transactional
    public void joinGroup(String inviteCode, User user) {
        // 초대 코드로 그룹 찾기
        TravelGroup group = travelGroupRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 초대 코드입니다."));

        // 가입 중복 확인
        if (userGroupRepository.existsByUserAndTravelGroup(user, group)) {
            throw new IllegalStateException("이미 가입된 그룹입니다.");
        }

        // 가입 처리
        UserGroup userGroup = UserGroup.builder()
                .user(user)
                .travelGroup(group)
                .role(GroupRole.MEMBER) // 역할은 멤버로 저장
                .build();

        userGroupRepository.save(userGroup);
    }

    // 초대 코드 생성 메소드
    private String generateUniqueInviteCode() {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(6).toUpperCase();
        } while (travelGroupRepository.existsByInviteCode(code)); // 새로운 코드가 나올때까지
        return code;
    }
}