package com.next.genshinflow.application.posting.controller.publicapi;

import com.next.genshinflow.application.posting.response.PostingResponse;
import com.next.genshinflow.auth.UserAuth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/public/v1/postings")
@RestController
public class PostingController implements PostingAPI {


    public ResponseEntity<List<PostingResponse>> getRequests(long size) {
        return null;
    }

    @UserAuth
    public ResponseEntity<List<PostingResponse>> getMyRequests() {
        return null;
    }

    public ResponseEntity<PostingResponse> createRequestByNonMember() {
        return null;
    }

    @UserAuth
    public ResponseEntity<PostingResponse> createRequestByUser() {
        return null;
    }

    public ResponseEntity<PostingResponse> modifyRequestByNonMember() {
        return null;
    }

    @UserAuth
    public ResponseEntity<PostingResponse> modifyRequestByUser() {
        return null;
    }

    public ResponseEntity<PostingResponse> deleteRequestByNonMember() {
        return null;
    }

    @UserAuth
    public ResponseEntity<PostingResponse> deleteRequestByUser() {
        return null;
    }

    public ResponseEntity<PostingResponse> pullUpRequestByNonMember() {
        return null;
    }

    @UserAuth
    public ResponseEntity<PostingResponse> pullUpRequestByUser() {
        return null;
    }
}
