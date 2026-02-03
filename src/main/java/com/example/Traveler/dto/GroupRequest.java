package com.example.Traveler.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class GroupRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {
        private String groupName;
    }

    @Getter
    @NoArgsConstructor
    public static class Join {
        private String inviteCode;
    }
}
