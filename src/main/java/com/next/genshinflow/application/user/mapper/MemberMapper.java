package com.next.genshinflow.application.user.mapper;

import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.enumeration.Role;

public class MemberMapper {

    private MemberMapper() {}

    public static MemberEntity toMember(long uid, String email, UserInfoResponse apiResponse, String profileImgPath, String tower, boolean oauth) {

        MemberEntity member = new MemberEntity();
        member.setUid(uid);
        member.setName(apiResponse.getPlayerInfo().getNickname());
        member.setEmail(email);
        member.setImage(profileImgPath);
        member.setLevel(apiResponse.getPlayerInfo().getLevel());
        member.setWorldLevel(apiResponse.getPlayerInfo().getWorldLevel());
        member.setTowerLevel(tower);
        member.setRole(Role.USER);
        member.setOAuthUser(oauth);

        return member;
    }

    public static MemberResponse memberToResponse(MemberEntity member) {
        if (member == null) return null;

        return MemberResponse.builder()
            .id(member.getId())
            .uid(member.getUid())
            .name(member.getName())
            .email(member.getEmail())
            .image(member.getImage())
            .level(member.getLevel())
            .worldLevel(member.getWorldLevel())
            .towerLevel(member.getTowerLevel())
            .role(member.getRole())
            .oAuthUser(member.getOAuthUser())
            .disciplineDate(member.getDisciplineDate())
            .disciplinaryHistory(member.getDisciplinaryHistory())
            .warningHistory(member.getWarningHistory())
            .createdAt(member.getCreatedAt())
            .build();
    }
}
