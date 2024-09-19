package com.next.genshinflow.application.user.service;

import com.next.genshinflow.application.user.dto.LoginRequest;
import com.next.genshinflow.application.user.dto.MemberResponse;
import com.next.genshinflow.application.user.dto.SignUpRequest;
import com.next.genshinflow.application.user.dto.TokenResponse;
import com.next.genshinflow.application.user.mapper.MemberMapper;
import com.next.genshinflow.domain.user.entity.MemberEntity;
import com.next.genshinflow.domain.user.repository.MemberRepository;
import com.next.genshinflow.enumeration.AccountStatus;
import com.next.genshinflow.enumeration.Role;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.jwt.TokenProvider;
import com.next.genshinflow.security.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

     //회원가입시 uid, email, pw 입력칸만 있음
     //입력 받은 uid로 유저 정보를 가져오는 로직 필요함
    public MemberResponse createMember(SignUpRequest signUpRequest) {
        verifyExistEmail(signUpRequest.getEmail());

        // 테스트용
        String role = Role.USER.getRole();

        if (signUpRequest.getEmail().equals("admin@test.com")) {
            role = Role.ADMIN.getRole();
        }

        MemberEntity member = MemberEntity.builder()
            .uid(signUpRequest.getUid())
            .email(signUpRequest.getEmail())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .status(AccountStatus.ACTIVE_USER)
            .role(role)
            .build();

//            .name(uid api.getName())
//            .image(uid ap.getImage())
//            .status(uid ap.getStatus())
//            .build();

        return MemberMapper.memberToResponse(memberRepository.save(member));
    }

    // 로그인
    public TokenResponse authenticate(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BusinessLogicException(ExceptionCode.INVALID_REFRESH_TOKEN);
        }

        Authentication authentication = tokenProvider.getAuthentication(refreshToken);
        String newAccessToken = tokenProvider.createToken(authentication);

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

    // 해당 사용자, 권한 검증
//    public void verifyPermission(long loginId, long memberId) {
//        MemberEntity member = findMember(loginId);
//
//        boolean isAdmin = member.getRole().stream()
//            .anyMatch(authority -> authority.getAuthorityName().equals(Role.ADMIN.getRole()));
//
//        if (!isAdmin) {
//            if (loginId != memberId) {
//                throw new BusinessLogicException(ExceptionCode.NO_PERMISSION);
//            }
//        }
//    }

    public MemberEntity findMember(long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
    }
}
