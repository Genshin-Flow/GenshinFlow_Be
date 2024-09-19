package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.LoginRequest;
import com.next.genshinflow.application.user.dto.MemberResponse;
import com.next.genshinflow.application.user.dto.SignUpRequest;
import com.next.genshinflow.application.user.dto.TokenResponse;
import com.next.genshinflow.application.user.service.MemberService;
import com.next.genshinflow.domain.utils.UriCreator;
import com.next.genshinflow.security.jwt.JwtFilter;
import com.next.genshinflow.security.jwt.TokenProvider;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity signUpMember(@Valid @RequestBody SignUpRequest request) {
        MemberResponse createdMember = memberService.createMember(request);
        URI location = UriCreator.createUri("/member", createdMember.getId());

        return ResponseEntity.created(location).build();
    }

    @PostMapping("/signin")
    public ResponseEntity<TokenResponse> signInMember(@Valid @RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = memberService.authenticate(loginRequest);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenResponse.getAccessToken());

        return new ResponseEntity<>(tokenResponse, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader) {
        String refreshToken = refreshTokenHeader.replace("Bearer ", "");
        TokenResponse tokenResponse = memberService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(tokenResponse);
    }
}