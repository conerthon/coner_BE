package com.example.Traveler.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class TravelGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String groupName;

    @Column(unique = true, nullable = false, length = 6)
    private String inviteCode;

    // TravelGroup - UserGroup 엔티티
    @JsonIgnore // dto를 만들어서 보내는 것이 좋음.
    @OneToMany(mappedBy = "travelGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserGroup> userGroups = new ArrayList<>();

    @Builder
    public TravelGroup(String name, String destination, String inviteCode) {
        this.groupName = name;
        this.inviteCode = inviteCode;
    }
}
