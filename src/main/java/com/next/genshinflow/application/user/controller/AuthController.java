package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.auth.*;
import com.next.genshinflow.application.user.dto.mailAuthentication.MailRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.service.AuthService;
import com.next.genshinflow.application.user.service.MailSendService;
import com.next.genshinflow.domain.utils.UriCreator;
import com.next.genshinflow.security.jwt.JwtFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Auth", description = "사용자 인증 및 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final MailSendService mailSendService;

    @Operation(summary = "인증코드 발송", description = "입력된 이메일로 인증코드를 발송함")
    @PostMapping("/verification-code/send")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody @Valid MailRequest mailRequest) {
        mailSendService.sendVerificationEmail(mailRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원 가입 처리")
    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponse> signUpMember(@Valid @RequestBody SignUpRequest request) {
        MemberResponse createdMember = authService.createMember(request);
        URI location = UriCreator.createUri("/member", createdMember.getId());

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 처리")
    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signInMember(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.authenticate(loginRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.OK);
    }

    @Operation(summary = "OAuth 회원가입", description = "이메일로 회원가입 처리")
    @PostMapping("/sign-up/{provider}")
    public ResponseEntity<TokenResponse> signUpOAuth(@Valid @RequestBody OAuthSignUpRequest request,
                                                     @PathVariable("provider") @NotBlank String provider) {

        // 회원가입 처리
        MemberResponse createdMember = authService.createOAuthMember(request);

        // 로그인 처리
        OAuthSignInRequest loginRequest = OAuthSignInRequest.builder()
            .email(createdMember.getEmail()).build();

        TokenResponse tokenResponse = authService.authenticateWithOAuth(loginRequest, provider);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.CREATED);
    }

    @Operation(summary = "OAuth 로그인", description = "이메일로 로그인 처리")
    @PostMapping("/sign-in/{provider}")
    public ResponseEntity<TokenResponse> signInOAuth(@Valid @RequestBody OAuthSignInRequest loginRequest,
                                                     @PathVariable("provider") @NotBlank String provider) {

        TokenResponse tokenResponse = authService.authenticateWithOAuth(loginRequest, provider);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.OK);
    }

    @Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰을 이용해 만료된 액세스 토큰을 갱신함."
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestHeader("AccessToken") String accessTokenHeader,
                                                            @RequestHeader("RefreshToken") String refreshTokenHeader) {
        String accessToken = accessTokenHeader.replace("Bearer ", "");
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");
        TokenResponse tokenResponse = authService.refreshAccessToken(accessToken, refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "비밀번호 변경", description = "이메일 인증(/verification-code/send) 후 유저의 비밀번호 변경")
    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER','OAUTH_USER','ADMIN')")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request) {

        authService.changePassword(request.getEmail(), request.getAuthNum(), request.getPassword());
        return ResponseEntity.ok().build();
    }
}
