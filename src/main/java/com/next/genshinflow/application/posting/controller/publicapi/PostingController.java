package com.next.genshinflow.application.posting.controller.publicapi;

import com.next.genshinflow.application.PageResponse;
import com.next.genshinflow.application.posting.request.PostingCreateRequest;
import com.next.genshinflow.application.posting.request.PostingDeleteRequest;
import com.next.genshinflow.application.posting.request.PostingModifyRequest;
import com.next.genshinflow.application.posting.request.PostingPullUpRequest;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.application.posting.service.PostingAppService;
import com.next.genshinflow.application.user.response.MemberResponse;
import com.next.genshinflow.auth.UserAuth;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    private final PostingAppService postingAppService;

    @GetMapping("/{postingId}")
    public ResponseEntity<PostingResponse> getRequests(
        @PathVariable long postingId
    ) {
        return ResponseEntity.ok(postingAppService.getPosting(postingId));
    }

    @UserAuth(required = false)
    @GetMapping
    public ResponseEntity<PageResponse<PostingResponse>> getRequests(
        @AuthenticationPrincipal MemberResponse memberResponse,
        @RequestParam Pageable pageable
    ) {
        return ResponseEntity.ok(postingAppService.getPostings(memberResponse, pageable));
    }

    @PostMapping(value = "/create", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> createRequestByNonMember(
        @RequestBody PostingCreateRequest request
    ) {
        PostingResponse response = postingAppService.createNonMemberPosting(request);
        return ResponseEntity.created(URI.create("/" + response.id()))
            .body(response);
    }

    @UserAuth
    @PostMapping(value = "/create", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> createRequestByUser(
        @AuthenticationPrincipal MemberResponse member,
        @RequestBody PostingCreateRequest request
    ) {
        PostingResponse response = postingAppService.createMemberPosting(request, member);
        return ResponseEntity.created(URI.create("/" + response.id()))
            .body(response);
    }

    @PutMapping(value = "/modify", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> modifyRequestByNonMember(
        @RequestBody PostingModifyRequest request
    ) {
        PostingResponse response = postingAppService.modifyNonMemberPosting(request);
        return ResponseEntity.ok(response);
    }

    @UserAuth
    @PutMapping(value = "/modify", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> modifyRequestByUser(
        @AuthenticationPrincipal MemberResponse member,
        @RequestBody PostingModifyRequest request
    ) {
        PostingResponse response = postingAppService.modifyMemberPosting(request, member);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/delete", headers = NO_LOGIN_HEADER)
    public ResponseEntity<Void> deleteRequestByNonMember(
        @RequestBody PostingDeleteRequest request
    ) {
        postingAppService.deleteNonMemberPosting(request);
        return ResponseEntity.noContent().build();
    }

    @UserAuth
    @DeleteMapping(value = "/delete", headers = LOGIN_HEADER)
    public ResponseEntity<Void> deleteRequestByUser(
        @AuthenticationPrincipal MemberResponse member,
        @RequestBody PostingDeleteRequest request
    ) {
        postingAppService.deleteMemberPosting(request, member);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "/pullup", headers = NO_LOGIN_HEADER)
    public ResponseEntity<PostingResponse> pullUpRequestByNonMember(
        @RequestBody PostingPullUpRequest request
    ) {
        PostingResponse response = postingAppService.pullUpNonMemberPosting(request);
        return ResponseEntity.ok(response);
    }

    @UserAuth
    @PutMapping(value = "/pullup", headers = LOGIN_HEADER)
    public ResponseEntity<PostingResponse> pullUpRequestByUser(
        @AuthenticationPrincipal MemberResponse member,
        @RequestBody PostingPullUpRequest request
    ) {
        PostingResponse response = postingAppService.pullUpMemberPosting(request, member);
        return ResponseEntity.ok(response);
    }
}
