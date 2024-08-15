package com.next.genshinflow.application.user.mapper;

import com.next.genshinflow.application.user.dto.ChangePasswordRequest;
import com.next.genshinflow.application.user.dto.MyPageRequest;
import com.next.genshinflow.application.user.dto.SignUpRequest;
import com.next.genshinflow.application.user.response.MemberResponse;
import com.next.genshinflow.domain.user.entity.MemberEntity;

import java.util.List;

public interface MemberMapper {
    MemberEntity postToMember(SignUpRequest signUpRequest);

    MemberEntity patchToMember(MyPageRequest myPageRequest);

    MemberEntity patchToPassword(ChangePasswordRequest passwordRequest);

    MemberResponse memberToResponse(MemberEntity memberEntity);

    List<MemberResponse> membersToResponses(List<MemberEntity> memberEntities);
}
