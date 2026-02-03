package com.example.Traveler.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConfirmedPlaceResponse {
    private Long placeId;
    private String title;
    private String description;
    private String imageUrl;
    private String keyword;
}
