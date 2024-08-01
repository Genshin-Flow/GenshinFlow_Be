package com.next.genshinflow.application.post.response;

import java.util.List;

public record PostsResponse(
    List<PostResponse> posts
) {

}
