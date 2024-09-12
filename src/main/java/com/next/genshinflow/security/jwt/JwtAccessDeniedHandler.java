package com.next.genshinflow.security.jwt;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 사용자 인증은 되었지만 해당 리소스에 접근할 권한이 없을때 거부
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        logger.warn("권한 없는 접근 시도: {}", accessDeniedException.getMessage());
        response.sendError(ExceptionCode.NO_PERMISSION.getStatus(), ExceptionCode.NO_PERMISSION.getMessage());
    }
}
