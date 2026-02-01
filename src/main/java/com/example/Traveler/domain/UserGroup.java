package com.example.Traveler.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
public class UserGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UserGroup - User 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // UserGroup - TravelGroup 엔티티
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private TravelGroup travelGroup;

    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Builder
    public UserGroup(User user, TravelGroup travelGroup, GroupRole role) {
        this.user = user;
        this.travelGroup = travelGroup;
        this.role = role;
    }
}
