package com.next.genshinflow.application.user.mapper;

import com.next.genshinflow.application.user.dto.MemberDto;
import com.next.genshinflow.application.user.response.MemberResponse;
import com.next.genshinflow.domain.user.entity.MemberEntity;

import java.util.List;

public interface MemberMapper {
    MemberEntity postToMember(MemberDto.PostDto postDto);

    MemberEntity patchToMember(MemberDto.PatchDto patchDto);

    MemberResponse memberToResponse(MemberEntity memberEntity);

    List<MemberResponse> membersToResponses(List<MemberEntity> memberEntities);
}
