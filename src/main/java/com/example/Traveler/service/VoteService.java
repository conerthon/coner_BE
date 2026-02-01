package com.example.Traveler.service;

import com.example.Traveler.domain.*;
import com.example.Traveler.dto.ConfirmedPlaceResponse;
import com.example.Traveler.dto.VoteResultResponse;
import com.example.Traveler.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VoteService {

    private final VoteRepository voteRepository;
    private final UserGroupRepository userGroupRepository;
    private final TravelGroupRepository travelGroupRepository;
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;

    // 투표 실행
    @Transactional
    public void castVote(Long groupId, Long placeId, Long userId, boolean isLike) {
        // 1. 엔티티 존재 여부 확인
        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소가 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 그룹 멤버 권한 확인
        if (!userGroupRepository.existsByUserAndTravelGroup(user, group)) {
            throw new IllegalStateException("해당 그룹에 참여 중인 유저가 아닙니다.");
        }

        // 3. 중복 투표 체크 (수정 불가이므로 이미 존재하면 예외 발생)
        voteRepository.findByUserAndTravelGroupAndPlace(user, group, place)
                .ifPresent(v -> {
                    throw new IllegalStateException("이미 투표를 완료한 장소입니다.");
                });

        // 4. 투표 저장
        Vote vote = Vote.builder()
                .user(user)
                .travelGroup(group)
                .place(place)
                .isLike(isLike)
                .build();

        voteRepository.save(vote);
    }


    // 특정 장소의 투표 상세 결과 집계
    public VoteResultResponse getVoteResult(Long groupId, Long placeId) {
        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음"));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        List<Vote> allVotes = voteRepository.findAllByTravelGroupAndPlace(group, place);
        long totalGroupMembers = group.getUserGroups().size();

        List<String> likedUsers = allVotes.stream()
                .filter(Vote::isLike)
                .map(v -> v.getUser().getNickname())
                .collect(Collectors.toList());

        List<String> dislikedUsers = allVotes.stream()
                .filter(v -> !v.isLike())
                .map(v -> v.getUser().getNickname())
                .collect(Collectors.toList());

        // 과반수(50% 초과) 여부
        boolean isConfirmed = (double) likedUsers.size() / totalGroupMembers > 0.5;

        return new VoteResultResponse(
                place.getId(),
                place.getTitle(),
                allVotes.size(),
                likedUsers.size(),
                dislikedUsers.size(),
                isConfirmed,
                likedUsers,
                dislikedUsers
        );
    }

    // 과반수 찬성으로 확정된 장소 리스트만 조회
    public List<ConfirmedPlaceResponse> getConfirmedPlaces(Long groupId) {
        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음"));

        // PlaceRepository에 findAllByTravelGroup(group) 메서드가 필요
        List<Place> allPlaces = placeRepository.findAllByTravelGroup(group);
        long totalGroupMembers = group.getUserGroups().size();

        return allPlaces.stream()
                .filter(place -> {
                    long likeCount = voteRepository.countByTravelGroupAndPlaceAndIsLikeTrue(group, place);
                    return (double) likeCount / totalGroupMembers > 0.5;
                })
                .map(place -> new ConfirmedPlaceResponse(
                        place.getId(),
                        place.getTitle(),
                        place.getImageUrl(),
                        place.getKeyword(),
                        voteRepository.countByTravelGroupAndPlaceAndIsLikeTrue(group, place)
                ))
                .collect(Collectors.toList());
    }
}