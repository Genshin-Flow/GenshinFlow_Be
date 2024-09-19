package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.MemberResponse;
import com.next.genshinflow.application.user.dto.TokenResponse;
import com.next.genshinflow.application.user.service.MemberService;
import com.next.genshinflow.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MemberResponse> getMyInfo() {
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @GetMapping("/info/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberResponse> getUserInfo(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }
}
