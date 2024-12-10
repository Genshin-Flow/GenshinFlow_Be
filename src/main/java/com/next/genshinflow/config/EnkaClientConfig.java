package com.next.genshinflow.config;

import com.next.genshinflow.infrastructure.EnkaClient;
import com.next.genshinflow.infrastructure.EnkaClientErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Slf4j
@Configuration
public class EnkaClientConfig {

    @Value("${enka.host}")
    private String host;

    @Bean
    public HttpServiceProxyFactory enkaClientProxyFactory() {
        RestClient restClient = RestClient.builder()
            .baseUrl(host)
            .defaultStatusHandler(new EnkaClientErrorHandler())
            .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    public EnkaClient enkaClient(@Autowired HttpServiceProxyFactory enkaClientProxyFactory) {
        try {
            return enkaClientProxyFactory.createClient(EnkaClient.class);
        }
        catch (Exception e) {
            log.error("Failed to create EnkaClient: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating EnkaClient", e);
        }
    }
}
