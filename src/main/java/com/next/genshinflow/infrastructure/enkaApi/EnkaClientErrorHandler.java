package com.next.genshinflow.infrastructure.enkaApi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class EnkaClientErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        log.error("Error occurred while calling external API: HTTP Status Code {}, Message: {}",
            response.getStatusCode(), response.getStatusText());

        super.handleError(response);
    }
}
