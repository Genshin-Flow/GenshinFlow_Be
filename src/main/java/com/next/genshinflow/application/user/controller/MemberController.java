package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.BasePageResponse;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.user.dto.member.ChangePasswordRequest;
import com.next.genshinflow.application.user.dto.member.ChangeUidRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import com.next.genshinflow.application.user.service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController implements MemberAPI {
    private final MemberService memberService;

    @GetMapping("/my-info")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MemberResponse> getMyInfo() {
        return ResponseEntity.ok(memberService.getMyInfo());
    }

    @GetMapping("/info/{userEmail}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<MemberResponse> getUserInfo(@Email @PathVariable String userEmail) {
        return ResponseEntity.ok(memberService.getMemberInfo(userEmail));
    }

    @GetMapping("/my-post")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BasePageResponse<PostResponse>> getMyPosts(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        BasePageResponse<PostResponse> postHistory = memberService.getMyPosts(page-1, size);
        return ResponseEntity.ok(postHistory);
    }

    @PatchMapping("/update/uid")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<MemberResponse> updateUid(@Valid @RequestBody ChangeUidRequest request) {
        MemberResponse updatedMemberResponse = memberService.updateUid(request);
        return ResponseEntity.ok(updatedMemberResponse);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        memberService.changePassword(request);
        return ResponseEntity.ok().build();
    }
}
