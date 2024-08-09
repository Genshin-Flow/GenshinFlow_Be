package com.next.genshinflow.security.config;

import com.next.genshinflow.enumeration.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

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
    public UserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("user@gmail.com")
                .password("1234")
                .roles(Role.ADMIN.getRole())
                .build();

        UserDetails admin = User.builder()
            .username("admin@gmail.com")
            .password("4321")
            .roles(Role.USER.getRole())
            .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
