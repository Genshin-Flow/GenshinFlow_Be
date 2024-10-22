package com.next.genshinflow.application.user.mapper;

import com.next.genshinflow.application.user.dto.*;
import com.next.genshinflow.domain.user.entity.MemberEntity;

public class MemberMapper {

    private MemberMapper() {}

    public static MemberResponse memberToResponse(MemberEntity member) {
        if (member == null) return null;

        return MemberResponse.builder()
            .uid(member.getUid())
            .name(member.getName())
            .email(member.getEmail())
            .image(member.getImage())
            .level(member.getLevel())
            .worldLevel(member.getWorldLevel())
            .towerLevel(member.getTowerLevel())
            .status(member.getStatus())
            .role(member.getRole())
            .build();
    }
}
