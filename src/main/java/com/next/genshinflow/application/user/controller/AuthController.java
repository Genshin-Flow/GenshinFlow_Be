package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.service.TokenService;
import com.next.genshinflow.application.user.service.SignInService;
import com.next.genshinflow.application.user.service.SignUpService;
import com.next.genshinflow.application.user.dto.auth.*;
import com.next.genshinflow.infrastructure.mail.MailRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.infrastructure.mail.MailSendService;
import com.next.genshinflow.domain.utils.UriCreator;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import com.next.genshinflow.security.jwt.JwtFilter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController implements AuthAPI {
    private final MailSendService mailSendService;
    private final SignUpService signUpService;
    private final SignInService signInService;
    private final TokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;


    @PostMapping("/verification-code/send")
    public ResponseEntity<Void> sendVerificationCode(@RequestBody @Valid MailRequest mailRequest) {
        mailSendService.sendVerificationEmail(mailRequest.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MemberResponse> signUpMember(@Valid @RequestBody SignUpRequest request) {
        MemberResponse createdMember = signUpService.createUser(request);
        URI location = UriCreator.createUri("/member", createdMember.getId());

        return ResponseEntity.created(location).body(createdMember);
    }

    @PostMapping("/sign-up/{provider}")
    public ResponseEntity<TokenResponse> signUpOAuth(@Validated @RequestBody OAuthSignUpRequest request,
                                                     @PathVariable("provider") @NotBlank String provider) {
        // 회원가입 처리
        MemberResponse createdMember = signUpService.createOAuthUser(request);

        // 로그인 처리
        OAuthSignInRequest loginRequest = OAuthSignInRequest.builder()
            .email(createdMember.getEmail()).build();

        TokenResponse tokenResponse = signInService.authenticateWithOAuth(loginRequest, provider);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signInMember(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = signInService.authenticate(loginRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/sign-in/{provider}")
    public ResponseEntity<TokenResponse> signInOAuth(@Validated @RequestBody OAuthSignInRequest loginRequest,
                                                     @PathVariable("provider") @NotBlank String provider) {

        TokenResponse tokenResponse = signInService.authenticateWithOAuth(loginRequest, provider);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(
        @Valid @RequestBody RefreshTokenRequest request
    ) {
        if (request.getRefreshToken() == null || request.getRefreshToken().isBlank())
            throw new BusinessLogicException(ExceptionCode.REFRESH_TOKEN_REQUIRED);

        TokenResponse tokenResponse = tokenService.refreshAccessToken(
            request.getRefreshToken().replace("Bearer ", "")
        );
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null)
            eventPublisher.publishEvent(new LogoutSuccessEvent(authentication));

        return ResponseEntity.ok().build();
    }
}
