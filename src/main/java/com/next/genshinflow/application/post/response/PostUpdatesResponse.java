package com.next.genshinflow.application.post.response;

import java.util.List;

public record PostUpdatesResponse(
    List<PostResponse> created,
    List<PostResponse> updated,
    List<Long> deleted
) {

}
