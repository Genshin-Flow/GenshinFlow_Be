package com.next.genshinflow.config;

import com.next.genshinflow.infrastructure.enka.EnkaClient;
import com.next.genshinflow.infrastructure.enka.EnkaClientErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class EnkaClientConfig {

    @Value("${enka.host}")
    private String host;

    @Bean
    HttpServiceProxyFactory enkaClientProxyFactory() {
        RestClient restClient = RestClient.builder()
            .baseUrl(host)
            .defaultStatusHandler(new EnkaClientErrorHandler())
            .build();
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        return HttpServiceProxyFactory.builderFor(adapter).build();
    }

    @Bean
    EnkaClient enkaClient(HttpServiceProxyFactory enkaClientProxyFactory) {
        return enkaClientProxyFactory.createClient(EnkaClient.class);
    }

}
