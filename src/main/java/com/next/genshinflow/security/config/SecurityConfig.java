package com.next.genshinflow.security.config;

import com.next.genshinflow.enumeration.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // HTTP 헤더 설정
            .headers(headerConfig ->
                headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
            )

            // CSRF 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // 폼 로그인 설정
            .formLogin(formLogin -> formLogin
                .loginPage("/auths/login")
                .loginProcessingUrl("/process_login")
                .failureUrl("/auths/login?error")
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
            )
            // 예외 처리 설정
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .accessDeniedPage("/auths/access-denied")
            )
            // URL 인가 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/admin/**").hasRole(Role.ADMIN.getRole())
                .requestMatchers("/member/my-page").hasRole(Role.USER.getRole())
                .anyRequest().permitAll()
            );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // CORS 정책 설정
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //모든 출처에 대해 스크립트 기반의 HTTP 통신 허용
        configuration.setAllowedOrigins(Arrays.asList("*"));
        //HTTP Method에 대한 HTTP 통신 허용
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PATCH","DELETE"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //모든 URL에 앞서 구성한 CORS 정책을 적용
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
