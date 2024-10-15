package com.next.genshinflow.infrastructure.enka;

import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

public class EnkaClientErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        // TODO: handle error if needed
        super.handleError(response);
    }
}
