package com.next.genshinflow.application.user.dto.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.next.genshinflow.domain.user.entity.Discipline;
import com.next.genshinflow.domain.user.entity.Warning;
import com.next.genshinflow.enumeration.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @Schema(description = "유저 권한", type = "enum", example = "관리자, 유저, 정지")
    private Role role;

    @Schema(description = "OAuth 유저 구분", type = "boolean")
    private Boolean oAuthUser;

    @Schema(description = "회원가입 날짜", type = "LocalDateTime")
    private LocalDateTime createdAt;

    @Schema(description = "제재 처리 날짜", type = "LocalDateTime")
    private LocalDateTime disciplineDate;

    @Schema(description = "제재 기록", type = "List")
    private List<Discipline> disciplinaryHistory;

    @Schema(description = "경고 기록", type = "List")
    private List<Warning> warningHistory;
}
