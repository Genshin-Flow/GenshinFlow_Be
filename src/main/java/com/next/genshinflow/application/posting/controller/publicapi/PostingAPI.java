package com.next.genshinflow.application.posting.controller.publicapi;


import com.next.genshinflow.application.PageResponse;
import com.next.genshinflow.application.posting.request.PostingCreateRequest;
import com.next.genshinflow.application.posting.request.PostingDeleteRequest;
import com.next.genshinflow.application.posting.request.PostingModifyRequest;
import com.next.genshinflow.application.posting.request.PostingPullUpRequest;
import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.application.user.response.MemberResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.awt.print.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Posting API", description = "포스팅 API")
public interface PostingAPI {

    @Operation(
        summary = "포스팅 목록",
        description = "최근 포스팅을 size 개 가져옵니다. 최대개수 : 100"
    )
    ResponseEntity<PageResponse<PostingResponse>> getRequests(
        Pageable pageable
    );

    @Operation(summary = "비회원 리퀘스트 작성", description = "비회원의 리퀘스트를 등록합니다.")
    ResponseEntity<PostingResponse> createRequestByNonMember(
        PostingCreateRequest postingCreateRequest
    );

    @Operation(summary = "회원 리퀘스트 작성", description = "회원의 리퀘스트를 등록합니다")
    ResponseEntity<PostingResponse> createRequestByUser(
        MemberResponse memberResponse,
        PostingCreateRequest postingCreateRequest
    );

    @Operation(summary = "비회원 리퀘스트 수정", description = "비회원의 리퀘스트를 수정합니다.")
    ResponseEntity<PostingResponse> modifyRequestByNonMember(
        PostingModifyRequest postingModifyRequest
    );

    @Operation(summary = "회원 리퀘스트 수정", description = "회원의 리퀘스트를 수정합니다.")
    ResponseEntity<PostingResponse> modifyRequestByUser(
        MemberResponse memberResponse,
        PostingModifyRequest postingModifyRequest
    );

    @Operation(summary = "비회원 리퀘스트 삭제", description = "비회원의 리퀘스트를 삭제합니다.")
    ResponseEntity<PostingResponse> deleteRequestByNonMember(
        PostingDeleteRequest postingDeleteRequest
    );

    @Operation(summary = "회원 리퀘스트 삭제", description = "회원의 리퀘스트를 삭제합니다.")
    ResponseEntity<PostingResponse> deleteRequestByUser(
        MemberResponse memberResponse,
        PostingDeleteRequest postingDeleteRequest
    );

    @Operation(summary = "비회원 리퀘스트 끌어올리기", description = "비회원의 리퀘스트을 끌올합니다.")
    ResponseEntity<PostingResponse> pullUpRequestByNonMember(
        PostingPullUpRequest postingPullUpRequest
    );

    @Operation(summary = "회원 리퀘스트 끌어올리기", description = "회원의 리퀘스트를 끌올합니다.")
    ResponseEntity<PostingResponse> pullUpRequestByUser(
        MemberResponse memberResponse,
        PostingPullUpRequest postingPullUpRequest
    );
}
