package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.*;
import com.next.genshinflow.application.user.dto.auth.LoginRequest;
import com.next.genshinflow.application.user.dto.auth.SignUpRequest;
import com.next.genshinflow.application.user.dto.auth.TokenResponse;
import com.next.genshinflow.application.user.dto.enkaApi.UserInfoResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.domain.user.repository.RedisRepository;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.jwt.TokenProvider;
import com.next.genshinflow.security.util.SecurityUtil;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {
    private final EnkaService enkaService;
    private final MailSendService mailSendService;
    private final MemberRepository memberRepository;
    private final RedisRepository redisRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

     //회원가입시 uid, email, pw 입력칸만 있음
     //입력 받은 uid로 유저 정보를 가져오는 로직 필요함
    public MemberResponse createMember(SignUpRequest signUpRequest) {
        mailSendService.verifyAuthCode(signUpRequest.getEmail(), signUpRequest.getAuthNum());
        verifyExistEmail(signUpRequest.getEmail());

        UserInfoResponse apiResponse = getUserInfoFromApi(signUpRequest.getUid());

        MemberEntity member = buildMemberEntity(signUpRequest, apiResponse);
        MemberEntity savedMember = memberRepository.save(member);

        log.info("Member created: {}", savedMember);

        return MemberMapper.memberToResponse(savedMember);
    }

    // 로그인
    public TokenResponse authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        MemberEntity findMember = findMember(loginRequest.getEmail());
        UserInfoResponse apiResponse = getUserInfoFromApi(findMember.getUid());

        updateMemberIfChanged(findMember, apiResponse);

        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String accessToken, String refreshToken) throws IOException {
        if (!tokenProvider.validateToken(accessToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_ACCESS_TOKEN);
        }

        String email = tokenProvider.getUserInfoFromToken(accessToken).getSubject();
        String refreshTokenFromRedis = redisRepository.getData(email);

        if (refreshTokenFromRedis == null || !refreshTokenFromRedis.equals(refreshToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String newAccessToken = tokenProvider.generateAccessToken(authentication);

        return new TokenResponse(newAccessToken, refreshToken);
    }

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

    // 이메일 검증
    private void verifyExistEmail(String email) {
        Optional<MemberEntity> member = memberRepository.findByEmail(email);
        if (member.isPresent())
            throw new BusinessLogicException(ExceptionCode.MEMBER_EXISTS);
    }

    public MemberEntity findMember(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }

    private UserInfoResponse getUserInfoFromApi(long uid) {
        UserInfoResponse apiResponse = enkaService.fetchUserInfoFromApi(uid);

        if (apiResponse == null || apiResponse.getPlayerInfo() == null) {
            throw new BusinessLogicException(ExceptionCode.EXTERNAL_API_ERROR);
        }
        return apiResponse;
    }

    private void updateMemberIfChanged(MemberEntity member, UserInfoResponse apiResponse) {
        UserInfoResponse.PlayerInfo playerInfo = apiResponse.getPlayerInfo();
        if (playerInfo == null) return;
        boolean isUpdated = false;

        if (!member.getName().equals(playerInfo.getNickname())) {
            member.setName(playerInfo.getNickname());
            isUpdated = true;
        }
        if (member.getLevel() != playerInfo.getLevel()) {
            member.setLevel(playerInfo.getLevel());
            isUpdated = true;
        }
        if (member.getWorldLevel() != playerInfo.getWorldLevel()) {
            member.setWorldLevel(playerInfo.getWorldLevel());
            isUpdated = true;
        }

        String tower = playerInfo.getTowerFloorIndex() + "-" + playerInfo.getTowerLevelIndex();
        if (!member.getTowerLevel().equals(tower)) {
            member.setTowerLevel(tower);
            isUpdated = true;
        }

        if (isUpdated) {
            memberRepository.save(member);
        }
    }

    private MemberEntity buildMemberEntity(SignUpRequest signUpRequest, UserInfoResponse apiResponse) {
        String profileImgPath = enkaService.getIconPathForProfilePicture(apiResponse.getPlayerInfo().getProfilePicture().getId());
        String tower = apiResponse.getPlayerInfo().getTowerFloorIndex() + "-" + apiResponse.getPlayerInfo().getTowerLevelIndex();

        return MemberEntity.builder()
            .uid(signUpRequest.getUid())
            .name(apiResponse.getPlayerInfo().getNickname())
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .image(profileImgPath)
            .level(apiResponse.getPlayerInfo().getLevel())
            .worldLevel(apiResponse.getPlayerInfo().getWorldLevel())
            .towerLevel(tower)
            .status(AccountStatus.ACTIVE_USER)
            .role(Role.USER.getRole())
            .build();
    }
}
