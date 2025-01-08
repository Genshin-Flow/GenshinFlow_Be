package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.auth.OAuthSignUpRequest;
import com.next.genshinflow.application.user.dto.auth.SignUpRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.application.validation.UserValidationManager;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.infrastructure.enkaApi.EnkaService;
import com.next.genshinflow.infrastructure.enkaApi.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserProfileService userProfileService;
    private final EnkaService enkaService;
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final UserValidationManager validationManager;

    // 일반 유저 회원가입
    public MemberResponse createUser(SignUpRequest request) {
        validationManager.verifyAuthCode(request.getEmail(), request.getAuthNum());
        return saveNewMember(request.getEmail(), request.getUid(), request.getPassword(), request.getAuthNum(), false);
    }

    // OAuth 회원가입
    public MemberResponse createOAuthUser(OAuthSignUpRequest request) {
        return saveNewMember(request.getEmail(), request.getUid(), null, null, true);
    }

    private MemberResponse saveNewMember(String email, long uid, String password, String authNum, boolean oauth) {
        validationManager.validateCreateUser(email, uid);

        UserInfoResponse apiResponse = enkaService.getUserInfoFromApi(uid);
        MemberEntity member = userProfileService.buildMemberEntity(uid, email, password, apiResponse, oauth);
        MemberEntity savedMember = memberRepository.save(member);

        redisRepository.deleteData(authNum);
        return MemberMapper.memberToResponse(savedMember);
    }
}
