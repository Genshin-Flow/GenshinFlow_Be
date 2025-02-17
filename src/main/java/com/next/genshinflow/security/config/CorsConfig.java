package com.next.genshinflow.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "genshin-flow-git-fix-casesensitive-fefdfea1s-projects.vercel.app"
        ));
        configuration.addAllowedOriginPattern("*"); // 모든 출처 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Collections.singletonList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //모든 URL에 앞서 구성한 CORS 정책을 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}