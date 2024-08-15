package com.next.genshinflow.application.posting.controller.publicapi;

import com.next.genshinflow.application.PageResponse;
import com.next.genshinflow.application.posting.request.PostingCreateRequest;
import com.next.genshinflow.application.posting.request.PostingDeleteRequest;
import com.next.genshinflow.application.posting.request.PostingModifyRequest;
import com.next.genshinflow.application.posting.request.PostingPullUpRequest;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.application.user.response.MemberResponse;
import com.next.genshinflow.auth.UserAuth;
import java.awt.print.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/public/v1/postings")
@RestController
public class PostingController implements PostingAPI {

    private static final String LOGIN_HEADER = "";
    private static final String NO_LOGIN_HEADER = "!" + LOGIN_HEADER;


    @GetMapping
    public ResponseEntity<PageResponse<PostingResponse>> getRequests(
        @RequestParam Pageable pageable
    ) {
        return null;
    }

    @PostMapping(value = "/create", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> createRequestByNonMember(
        @RequestBody PostingCreateRequest postingCreateRequest
    ) {
        return null;
    }

    @UserAuth
    @PostMapping(value = "/create", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> createRequestByUser(
        @AuthenticationPrincipal MemberResponse memberResponse,
        @RequestBody PostingCreateRequest postingCreateRequest
    ) {
        return null;
    }

    @PutMapping(value = "/modify", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> modifyRequestByNonMember(
        @RequestBody PostingModifyRequest postingModifyRequest
    ) {
        return null;
    }

    @UserAuth
    @PutMapping(value = "/modify", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> modifyRequestByUser(
        @AuthenticationPrincipal MemberResponse memberResponse,
        @RequestBody PostingModifyRequest postingModifyRequest
    ) {
        return null;
    }

    @DeleteMapping(value = "/delete", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> deleteRequestByNonMember(
        @RequestBody PostingDeleteRequest postingDeleteRequest
    ) {
        return null;
    }

    @UserAuth
    @DeleteMapping(value = "/delete", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> deleteRequestByUser(
        @AuthenticationPrincipal MemberResponse memberResponse,
        @RequestBody PostingDeleteRequest postingDeleteRequest
    ) {
        return null;
    }

    @PutMapping(value = "/pullup", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> pullUpRequestByNonMember(
        @RequestBody PostingPullUpRequest postingPullUpRequest
    ) {
        return null;
    }

    @UserAuth
    @PutMapping(value = "/pullup", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> pullUpRequestByUser(
        @AuthenticationPrincipal MemberResponse memberResponse,
        @RequestBody PostingPullUpRequest postingPullUpRequest
    ) {
        return null;
    }
}
