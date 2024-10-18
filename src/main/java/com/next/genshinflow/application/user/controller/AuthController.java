package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.*;
import com.next.genshinflow.application.user.dto.auth.LoginRequest;
import com.next.genshinflow.application.user.dto.auth.SignUpRequest;
import com.next.genshinflow.application.user.dto.auth.TokenResponse;
import com.next.genshinflow.application.user.dto.mailAuthentication.MailRequest;
import com.next.genshinflow.application.user.service.MailSendService;
import com.next.genshinflow.application.user.service.MemberService;
import com.next.genshinflow.domain.utils.UriCreator;
import com.next.genshinflow.security.jwt.JwtFilter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Tag(name = "Auth", description = "로그인 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final MemberService memberService;
    private final MailSendService mailSendService;

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원 가입 처리")
    @PostMapping("/signup")
    public ResponseEntity signUpMember(@Valid @RequestBody SignUpRequest request) {
        MemberResponse createdMember = memberService.createMember(request);
        URI location = UriCreator.createUri("/member", createdMember.getId());

        return ResponseEntity.created(location).build();
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 처리")
    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signInMember(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = memberService.authenticate(loginRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.OK);
    }

    @Operation(
        summary = "액세스 토큰 갱신",
        description = "리프레시 토큰을 이용해 만료된 액세스 토큰을 갱신함."
    )
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");
        TokenResponse tokenResponse = memberService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }

    @Operation(summary = "인증코드 발송", description = "입력된 이메일로 인증코드를 발송함")
    @PostMapping("/mailSend")
    public String mailSend(@RequestBody @Valid MailRequest mailRequest) {
        return mailSendService.joinEmail(mailRequest.getEmail());
    }
}
