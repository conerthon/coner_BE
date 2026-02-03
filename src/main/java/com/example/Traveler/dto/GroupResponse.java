package com.example.Traveler.dto;

import com.example.Traveler.domain.TravelGroup;
import lombok.Getter;

@Getter
public class GroupResponse {
    private Long id;
    private String groupName;
    private String inviteCode;

    public GroupResponse(TravelGroup group) {
        this.id = group.getId();
        this.groupName = group.getGroupName();
        this.inviteCode = group.getInviteCode();
    }
}
