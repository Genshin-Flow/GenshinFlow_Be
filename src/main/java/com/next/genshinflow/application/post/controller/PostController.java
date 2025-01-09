package com.next.genshinflow.application.post.controller;

import com.next.genshinflow.application.BasePageResponse;
import com.next.genshinflow.application.post.dto.GuestPostActionRequest;
import com.next.genshinflow.application.post.dto.PostModifyRequest;
import com.next.genshinflow.application.post.dto.PostResponse;
import com.next.genshinflow.application.post.dto.PostCreateRequest;
import com.next.genshinflow.application.post.service.PostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/postings")
@RestController
public class PostController implements PostAPI {
    private final PostService postService;

    @PostMapping("/user")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PostResponse> createUserPost(@Valid @RequestBody PostCreateRequest request) {
        PostResponse postResponse = postService.createUserPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @PostMapping("/guest")
    public ResponseEntity<PostResponse> createGuestPost(@Valid @RequestBody PostCreateRequest request) {
        PostResponse postResponse = postService.createGuestPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(postResponse);
    }

    @GetMapping
    public ResponseEntity<BasePageResponse<PostResponse>> getPosts(
        @Positive @RequestParam(value = "page", defaultValue = "1") int page,
        @Positive @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        BasePageResponse<PostResponse> pageResponse = postService.getPosts(page-1, size);
        return ResponseEntity.ok(pageResponse);
    }

    @PatchMapping("/user/{postId}/complete")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> completeUserPost(@PathVariable("postId") @NotNull long postId) {
        postService.completeUserPost(postId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/guest/complete")
    public ResponseEntity<Void> completeGuestPost(@Valid @RequestBody GuestPostActionRequest request) {
        postService.completeGuestPost(request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/user/modify")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<PostResponse> modifyUserPost(@Valid @RequestBody PostModifyRequest request) {
        PostResponse postResponse = postService.modifyUserPost(request);
        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/guest/modify")
    public ResponseEntity<PostResponse> modifyGuestPost(@Valid @RequestBody PostModifyRequest request) {
        PostResponse postResponse = postService.modifyGuestPost(request);
        return ResponseEntity.ok(postResponse);
    }

    @PatchMapping("/user/{postId}/pull-up")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> pullUpUserPost(@PathVariable("postId") @NotNull long postId) {
        postService.pullUpUserPost(postId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/guest/pull-up")
    public ResponseEntity<Void> pullUpGuestPost(@Valid @RequestBody GuestPostActionRequest request) {
        postService.pullUpGuestPost(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{postId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<Void> deleteUserPost(@PathVariable("postId") @NotNull long postId) {
        postService.deleteUserPost(postId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/guest")
    public ResponseEntity<Void> deleteGuestPost(@Valid @RequestBody GuestPostActionRequest request) {
        postService.deleteGuestPost(request);
        return ResponseEntity.ok().build();
    }
}
