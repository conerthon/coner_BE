package com.example.Traveler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class VoteResultResponse {
    private Long placeId;
    private String placeTitle;
    private long totalVoters;
    private long likeCount;
    private long dislikeCount;
    private boolean isConfirmed;
    private List<String> likedUserNicknames;    // 찬성한 사람 이름들
    private List<String> dislikedUserNicknames; // 반대한 사람 이름들
}
