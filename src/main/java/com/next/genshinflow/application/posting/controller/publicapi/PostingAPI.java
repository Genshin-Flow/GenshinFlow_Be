package com.next.genshinflow.application.posting.controller.publicapi;


import com.next.genshinflow.application.posting.response.PostingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;

@Tag(name = "Posting API", description = "포스팅 API")
public interface PostingAPI {

    @Operation(
        summary = "포스팅 목록",
        description = "최근 포스팅을 size 개 가져옵니다. 최대개수 : 100"
    )
    ResponseEntity<List<PostingResponse>> getRequests(
        long size
    );

    @Operation(
        summary = "자신이 작성한 포스팅 목록",
        description = "요청자가 작성한 포스팅를 가져옵니다."
    )
    ResponseEntity<List<PostingResponse>> getMyRequests();

    @Operation(summary = "비회원 리퀘스트 작성", description = "비회원의 리퀘스트를 등록합니다.")
    ResponseEntity<PostingResponse> createRequestByNonMember(

    );

    @Operation(summary = "회원 리퀘스트 작성", description = "회원의 리퀘스트를 등록합니다")
    ResponseEntity<PostingResponse> createRequestByUser(

    );

    @Operation(summary = "비회원 리퀘스트 수정", description = "비회원의 리퀘스트를 수정합니다.")
    ResponseEntity<PostingResponse> modifyRequestByNonMember(

    );

    @Operation(summary = "회원 리퀘스트 수정", description = "회원의 리퀘스트를 수정합니다.")
    ResponseEntity<PostingResponse> modifyRequestByUser(

    );

    @Operation(summary = "비회원 리퀘스트 삭제", description = "비회원의 리퀘스트를 삭제합니다.")
    ResponseEntity<PostingResponse> deleteRequestByNonMember(

    );

    @Operation(summary = "회원 리퀘스트 삭제", description = "회원의 리퀘스트를 삭제합니다.")
    ResponseEntity<PostingResponse> deleteRequestByUser(

    );

    @Operation(summary = "비회원 리퀘스트 끌어올리기", description = "비회원의 리퀘스트을 끌올합니다.")
    ResponseEntity<PostingResponse> pullUpRequestByNonMember(

    );

    @Operation(summary = "회원 리퀘스트 끌어올리기", description = "회원의 리퀘스트를 끌올합니다.")
    ResponseEntity<PostingResponse> pullUpRequestByUser(

    );
}
