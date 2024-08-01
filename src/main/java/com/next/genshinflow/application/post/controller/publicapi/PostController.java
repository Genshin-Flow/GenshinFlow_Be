package com.next.genshinflow.application.post.controller.publicapi;

import com.next.genshinflow.application.post.response.PostUpdatesResponse;
import com.next.genshinflow.application.post.response.PostsResponse;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/public/v1/posts")
@RestController
public class PostController implements PostApi {

    @Override
    public ResponseEntity<PostsResponse> getPosts() {
        return null;
    }

    @Override
    public ResponseEntity<PostUpdatesResponse> getUpdates(
        LocalDateTime lastUpdatedAt
    ) {
        return null;
    }
}
