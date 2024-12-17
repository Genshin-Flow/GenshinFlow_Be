package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.user.dto.member.ChangeUidRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
    @GetMapping("/info")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberResponse> getUserInfo() {
        return ResponseEntity.ok(memberService.getMemberInfo());
    }

    @Operation(summary = "UID 변경")
    @PatchMapping("/update/uid")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MemberResponse> updateUid(@RequestBody ChangeUidRequest request) {

        MemberResponse updatedMemberResponse = memberService.updateUid(request);
        return ResponseEntity.ok(updatedMemberResponse);
    }

    @Operation(
        summary = "자신이 작성한 포스팅 목록",
        description = "size = 10"
    )
    @GetMapping("/my-post")
    public ResponseEntity<List<PostResponse>> getMyPosts(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        Page<PostResponse> postHistory = memberService.getMyPosts(page, size);
        return ResponseEntity.ok(postHistory.getContent());
    }
}
