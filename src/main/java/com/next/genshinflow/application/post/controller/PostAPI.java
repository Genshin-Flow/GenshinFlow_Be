package com.next.genshinflow.application.post.controller;

import com.next.genshinflow.application.BasePageResponse;
import com.next.genshinflow.application.post.dto.GuestPostActionRequest;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Posting", description = "포스팅 API")
public interface PostAPI {
    @Operation(
        summary = "회원 게시물 작성",
        description = "bearerAuth = User, Admin",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    ResponseEntity<PostResponse> createUserPost(@Valid @RequestBody PostCreateRequest request);

    @Operation(summary = "비회원 게시물 작성")
    ResponseEntity<PostResponse> createGuestPost(@Valid @RequestBody PostCreateRequest request);

    @Operation(summary = "포스팅 목록", description = "size = 20")
    ResponseEntity<BasePageResponse<PostResponse>> getPosts(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "20") int size
    );

    @Operation(
        summary = "회원 게시물 완료 처리",
        description = "bearerAuth = User, Admin",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    ResponseEntity<Void> completeUserPost(@PathVariable("postId") @NotNull long postId);

    @Operation(summary = "비회원 게시물 완료 처리")
    ResponseEntity<Void> completeGuestPost(@Valid @RequestBody GuestPostActionRequest request);

    @Operation(
        summary = "회원 게시물 수정",
        description = "bearerAuth = User, Admin",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    ResponseEntity<PostResponse> modifyUserPost(@Valid @RequestBody PostModifyRequest request);

    @Operation(summary = "비회원 게시물 수정")
    ResponseEntity<PostResponse> modifyGuestPost(@Valid @RequestBody PostModifyRequest request);

    @Operation(
        summary = "회원 리퀘스트 끌어올리기",
        description = "bearerAuth = User, Admin",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    ResponseEntity<Void> pullUpUserPost(@PathVariable("postId") @NotNull long postId);

    @Operation(summary = "비회원 리퀘스트 끌어올리기")
    ResponseEntity<Void> pullUpGuestPost(@Valid @RequestBody GuestPostActionRequest request);

    @Operation(
        summary = "회원 게시물 삭제",
        description = "bearerAuth = User, Admin",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    ResponseEntity<Void> deleteUserPost(@PathVariable("postId") @NotNull long postId);

    @Operation(summary = "비회원 게시물 삭제")
    ResponseEntity<Void> deleteGuestPost(@Valid @RequestBody GuestPostActionRequest request);
}
