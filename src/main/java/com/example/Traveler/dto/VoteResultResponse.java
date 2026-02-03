package com.example.Traveler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class VoteResultResponse {
    private Long placeId; // 장소 정보들
    private String placeTitle;
    private String description;
    private String imageUrl;
    private String keyword;
    private long totalVoters; // 총 투표한 인원수
    private long likeCount; // 좋아요 표시한 인원수
    private long dislikeCount; // 싫어요 표시한 인원수
    private boolean isConfirmed; // 장소가 살아남았는지
    private List<String> likedUserNicknames;    // 찬성한 사람 이름들
    private List<String> dislikedUserNicknames; // 반대한 사람 이름들
}
