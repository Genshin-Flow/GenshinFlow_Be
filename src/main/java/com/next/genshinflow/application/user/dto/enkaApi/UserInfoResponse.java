package com.next.genshinflow.application.user.dto.enkaApi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserInfoResponse {
    private PlayerInfo playerInfo;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlayerInfo {
        private String nickname;
        private int level;
        private int worldLevel;
        private int towerFloorIndex;
        private int towerLevelIndex;
        private ProfilePicture profilePicture;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfilePicture {
        private int id;
    }
}