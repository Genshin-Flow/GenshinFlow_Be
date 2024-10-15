package com.next.genshinflow.infrastructure;

import com.next.genshinflow.application.user.dto.UserInfoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(accept = MediaType.APPLICATION_JSON_VALUE, contentType = MediaType.APPLICATION_JSON_VALUE)
public interface EnkaClient {

    @GetExchange("/api/uid/{uid}")
    ResponseEntity<UserInfoResponse> getUserInfo(
        @PathVariable("uid") long uid,
        @RequestParam long info
    );
}
