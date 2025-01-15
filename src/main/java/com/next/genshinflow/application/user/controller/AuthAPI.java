package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.auth.*;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.infrastructure.mail.MailRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Auth", description = "사용자 인증 및 관리 API")
public interface AuthAPI {
    @Operation(summary = "인증코드 발송", description = "입력된 이메일로 인증코드를 발송함")
    ResponseEntity<Void> sendVerificationCode(@RequestBody @Valid MailRequest mailRequest);

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원 가입 처리")
    ResponseEntity<MemberResponse> signUpMember(@Valid @RequestBody SignUpRequest request);

    @Operation(summary = "OAuth 회원가입", description = "이메일로 회원가입 처리")
    ResponseEntity<TokenResponse> signUpOAuth(
        @Validated @RequestBody OAuthSignUpRequest request,
        @PathVariable("provider") @NotBlank String provider
    );

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 처리")
    ResponseEntity<TokenResponse> signInMember(@Valid @RequestBody LoginRequest loginRequest);

    @Operation(summary = "OAuth 로그인", description = "이메일로 로그인 처리")
    ResponseEntity<TokenResponse> signInOAuth(
        @Validated @RequestBody OAuthSignInRequest loginRequest,
        @PathVariable("provider") @NotBlank String provider
    );

    @Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰을 이용해 만료된 액세스 토큰을 갱신함."
    )
    ResponseEntity<TokenResponse> refreshAccessToken(
        @Valid @RequestBody RefreshTokenRequest request
    );

    @Operation(
        summary = "로그아웃",
        description = "bearerAuth = User, Admin / Dashboard Status에 사용됨"
    )
    ResponseEntity<Void> logout();
}
