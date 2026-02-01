package com.example.Traveler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmedPlaceResponse {
    private Long placeId;
    private String title;
    private String imageUrl;
    private String keyword;
    private long likeCount; // 몇 명이나 찬성했는지 보여주면 좋으니까요!
}
