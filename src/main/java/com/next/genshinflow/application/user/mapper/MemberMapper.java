package com.next.genshinflow.application.user.mapper;

import com.next.genshinflow.application.user.dto.*;
import com.next.genshinflow.domain.user.entity.MemberEntity;

public class MemberMapper {

    private MemberMapper() {}

    public MemberEntity postDtoToMember(SignUpRequest signUpRequest) {
        return MemberEntity.builder()
            .uid(signUpRequest.getUid())
            .email(signUpRequest.getEmail())
            .password(signUpRequest.getPassword())
            .build();
    }

    public static MemberResponse memberToResponse(MemberEntity member) {
        if (member == null) return null;

        return MemberResponse.builder()
            .uid(member.getUid())
            .name(member.getName())
            .email(member.getEmail())
            .image(member.getImage())
            .status(member.getStatus())
            .role(member.getRole())
            .build();
    }
}
