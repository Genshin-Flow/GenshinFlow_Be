package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final AuthService authService;
    private final MemberRepository memberRepository;


    @Transactional(readOnly = true)
    public MemberResponse getMyInfo() {
        return MemberMapper.memberToResponse(
            SecurityUtil.getCurrentUsername()
                .flatMap(memberRepository::findByEmail)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND))
        );
    }

    @Transactional(readOnly = true)
    public MemberResponse getMemberInfo(Long memberId) {
        MemberEntity memberEntity = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
        return MemberMapper.memberToResponse(memberEntity);
    }

    public MemberResponse updateUid(Long memberId, Long uid) {
        MemberEntity findMember = authService.findMember(memberId);

        findMember.setUid(uid);
        MemberEntity updatedMember = memberRepository.save(findMember);

        UserInfoResponse apiResponse = authService.getUserInfoFromApi(uid);
        authService.updateMemberIfPlayerInfoChanged(updatedMember, apiResponse);

        return MemberMapper.memberToResponse(updatedMember);
    }
}
