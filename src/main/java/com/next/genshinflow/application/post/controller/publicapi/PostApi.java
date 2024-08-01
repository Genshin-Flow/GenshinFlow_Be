package com.next.genshinflow.application.post.controller.publicapi;


import com.next.genshinflow.application.post.response.PostUpdatesResponse;
import com.next.genshinflow.application.post.response.PostsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;

@Tag(name = "Post APi", description = "포스트 API")
public interface PostApi {

    @Operation(summary = "초기 진입시 요청하는 post 목록", description = "최초 1회만 사용하여 초기 데이터를 가져옵니다.")
    ResponseEntity<PostsResponse> getPosts();

    @Parameter(name = "lastUpdatedAt", description = "이전 마지막 업데이트 일시")
    @Operation(summary = "진입 후 생긴 업데이트 목록", description = "최초 진입 후, 업데이트 목록을 가져옵니다.")
    ResponseEntity<PostUpdatesResponse> getUpdates(LocalDateTime lastUpdatedAt);
}
