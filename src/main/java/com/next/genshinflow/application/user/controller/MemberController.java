package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.user.dto.MemberResponse;
import com.next.genshinflow.application.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MemberResponse> getMyInfo() {
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @Operation(
        summary = "유저 정보 조회",
        description = "유저 ID로 정보를 조회함. 이 엔드포인트는 관리자 권한이 필요함."
    )
    @GetMapping("/info/{memberId}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberResponse> getUserInfo(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberInfo(memberId));
    }
}
