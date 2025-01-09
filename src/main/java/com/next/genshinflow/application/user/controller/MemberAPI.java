package com.next.genshinflow.application.user.controller;

import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.user.dto.member.ChangePasswordRequest;
import com.next.genshinflow.application.user.dto.member.ChangeUidRequest;
import com.next.genshinflow.application.user.dto.member.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Member", description = "회원 관련 API")
public interface MemberAPI {
    @Operation(summary = "내 정보 조회")
    ResponseEntity<MemberResponse> getMyInfo();

    @Operation(
        summary = "유저 정보 조회",
        description = "유저 ID로 정보를 조회함. 이 엔드포인트는 관리자 권한이 필요함."
    )
    ResponseEntity<MemberResponse> getUserInfo(@Email @PathVariable String email);

    @Operation(
        summary = "자신이 작성한 포스팅 목록",
        description = "size = 10"
    )
    ResponseEntity<List<PostResponse>> getMyPosts(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "10") int size
    );

    @Operation(summary = "UID 변경")
    ResponseEntity<MemberResponse> updateUid(@Valid @RequestBody ChangeUidRequest request);

    @Operation(summary = "비밀번호 변경", description = "이메일 인증(/verification-code/send) 후 유저의 비밀번호 변경")
    ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request);
}