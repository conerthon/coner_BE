package com.example.Traveler.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 투표를 위해서 연관관계 추가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TravelGroup travelGroup;
    
    @Column(columnDefinition = "TEXT")
    private String url;

    private String title;

    @Column(length = 1000)
    private String description;

    private String imageUrl;
    private String keyword;
}
