package com.next.genshinflow.security.config;

import com.next.genshinflow.security.jwt.JwtAccessDeniedHandler;
import com.next.genshinflow.security.jwt.JwtAuthenticationEntryPoint;
import com.next.genshinflow.security.jwt.JwtFilter;
import com.next.genshinflow.security.jwt.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    // 유연성을 위해 DelegatingPasswordEncoder를 사용함
    // 복잡성을 줄이고, 단일 인코딩 방식으로 일관성을 유지해야 할때는 BCryptPasswordEncoder로 변경
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtFilter jwtFilter = new JwtFilter(tokenProvider);

        http
            // CORS 설정 추가
            .cors(cors -> cors.configurationSource(corsConfigurationSource))

            // CSRF 비활성화
            .csrf(AbstractHttpConfigurer::disable)

            // 예외 처리 설정
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            // HTTP 헤더 설정 (H2 콘솔을 위해 Frame Options 설정)
            // disable은 보안 취약점이 발생할 가능성이 크기 때문에, 클릭재킹 공격에 취약할 수 있음
            .headers(headerConfig -> headerConfig
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            )

            // 세션을 사용하지 않음
            .sessionManagement(sessionManagement -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

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

            // URL 인가 설정
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/**").permitAll()
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .requestMatchers("/error").permitAll()
                .anyRequest().authenticated()
            )

            // JwtFilter를 UsernamePasswordAuthenticationFilter 전에 추가
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
