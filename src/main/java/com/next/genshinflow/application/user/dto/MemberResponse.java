package com.next.genshinflow.application.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.next.genshinflow.enumeration.AccountStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberResponse {
    @Schema(description = "유저 ID", type = "long", example = "1")
    @JsonIgnore
    private long id;

    @Schema(description = "원신 UID", type = "long", example = "1800812993")
    private long uid;

    @Schema(description = "유저 닉네임", type = "String", example = "잠자는남도곰")
    private String name;

    @Schema(description = "유저 이메일", type = "String", example = "user@example.com")
    private String email;

    @Schema(description = "프로필 이미지", type = "String", example = "https://enka.network/ui/UI_AvatarIcon_72173_Circle.png")
    private String image;

    @Schema(description = "원신 레벨", type = "int", example = "60")
    private int level;

    @Schema(description = "원신 월드 레벨", type = "int", example = "5")
    private int worldLevel;

    @Schema(description = "원신 나선비경", type = "String", example = "12-3")
    private String towerLevel;

    @Schema(description = "유저 상태", type = "enum", example = "활동, 휴먼, 정지, 제재 등")
    private AccountStatus status;

    @Schema(description = "유저 권한", type = "String", example = "관리자")
    private String role;
}
