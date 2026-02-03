package com.example.Traveler.service;

import com.example.Traveler.domain.*;
import com.example.Traveler.dto.ConfirmedPlaceResponse;
import com.example.Traveler.dto.VoteResultResponse;
import com.example.Traveler.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
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

    // 한 장소에 대해 투표 실행
    @Transactional
    public void castVote(Long groupId, Long placeId, Long userId, boolean isLike) {
        // 1. 불러오기
        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹이 존재하지 않습니다."));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소가 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // 2. 해당 그룹에 유저가 있는지 확인
        if (!userGroupRepository.existsByUserAndTravelGroup(user, group)) {
            throw new IllegalStateException("해당 그룹에 참여 중인 유저가 아닙니다.");
        }

        // 3. 중복 투표 확인
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


    // 한 장소에 대한 투표 결과
    public VoteResultResponse getVoteResult(Long groupId, Long placeId) {
        // 1. 불러오기
        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음"));
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("장소 없음"));

        // 투표 기록
        List<Vote> allVotes = voteRepository.findAllByTravelGroupAndPlace(group, place);
        // 투표한 사람 수
        long totalGroupMembers = group.getUserGroups().size();
        // 좋아요 누른 사람
        List<String> likedUsers = allVotes.stream()
                .filter(Vote::isLike)
                .map(v -> v.getUser().getNickname())
                .collect(Collectors.toList());
        // 싫어요 누른 사람
        List<String> dislikedUsers = allVotes.stream()
                .filter(v -> !v.isLike())
                .map(v -> v.getUser().getNickname())
                .collect(Collectors.toList());

        boolean isConfirmed;
        // 50% 넘어야 살아남도록 (2명이면 1명 찬성했을 때 살아남음)
        if (totalGroupMembers <= 2) {
            isConfirmed = !likedUsers.isEmpty();
            // System.out.println("찬성 멤버 수: " + likedUsers.size());
        } else {
            isConfirmed = (double) likedUsers.size() / totalGroupMembers > 0.5;
        }

        return new VoteResultResponse(
                place.getId(),
                place.getTitle(),
                place.getDescription(),
                place.getImageUrl(),
                place.getKeyword(),
                allVotes.size(),
                likedUsers.size(),
                dislikedUsers.size(),
                isConfirmed,
                likedUsers,
                dislikedUsers
        );
    }

    // 살아남은 장소 리스트만 조회
    public List<ConfirmedPlaceResponse> getConfirmedPlaces(Long groupId) {
        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음"));

        // 모든 장소를 가져오기 위해서 PlaceRepository에 findAllByTravelGroup(group) 메서드 필요
        List<Place> allPlaces = placeRepository.findAllByTravelGroup(group);
        // System.out.println("장소 수: " + allPlaces);
        long totalGroupMembers = group.getUserGroups().size();
        // System.out.println("투표한 사람: " + totalGroupMembers);

        return allPlaces.stream()
                .map(place -> {
                    try {
                        // 메서드 이름을 countConfirmedVotes로 변경
                        long likeCount = voteRepository.countConfirmedVotes(group, place);

                        boolean isConfirmed = (totalGroupMembers <= 2) ? (likeCount >= 1) : (double) likeCount / totalGroupMembers > 0.5;

                        if (isConfirmed) {
                            return new ConfirmedPlaceResponse(
                                    place.getId(), place.getTitle(), place.getImageUrl(), place.getKeyword(), place.getDescription());
                        }
                    } catch (Exception e) {
                        System.out.println("에러 발생 장소 ID: " + place.getId());
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // 그룹마다 살아남은 장소 개수
    public int getConfirmedCount(Long groupId) {
        return getConfirmedPlaces(groupId).size();
    }
}