package com.example.Traveler.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "votes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "group_id", "place_id"})
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Vote - User 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Vote - TravelGroup 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TravelGroup travelGroup;

    // Vote - Place 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(nullable = false)
    private boolean isLike; // true: 좋아요, false: 싫어요

    @Builder
    public Vote(User user, TravelGroup travelGroup, Place place, boolean isLike) {
        this.user = user;
        this.travelGroup = travelGroup;
        this.place = place;
        this.isLike = isLike;
    }
}
