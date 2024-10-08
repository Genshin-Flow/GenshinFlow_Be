package com.next.genshinflow.security.jwt;

import com.next.genshinflow.exception.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 인증 되지 않은 사용자가 보호된 리소스에 접근하려고 할 때 이 엔트리 포인트가 호출됨.
@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {

        log.error("Unauthorized request to URI: {} - Error: {}", request.getRequestURI(), authenticationException.getMessage());
        response.sendError(ExceptionCode.UNAUTHORIZED_USER.getStatus(), ExceptionCode.UNAUTHORIZED_USER.getMessage());
    }
}
