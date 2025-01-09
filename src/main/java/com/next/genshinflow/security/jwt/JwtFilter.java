package com.next.genshinflow.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ErrorResponse;
import com.next.genshinflow.exception.ExceptionCode;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@AllArgsConstructor
@Slf4j
public class JwtFilter extends GenericFilterBean {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String jwt = resolveToken(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();

        try {
            if (StringUtils.hasText(jwt)) {
                tokenProvider.validateToken(jwt);
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
            }
            else {
                log.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
            }
            filterChain.doFilter(request, response);
        }
        catch (BusinessLogicException e) {
            log.error("JWT 인증 에러: {}", e.getMessage());
            setErrorResponse(httpServletResponse, HttpServletResponse.SC_UNAUTHORIZED, e.getExceptionCode());
        }
        catch (Exception e) {
            log.error("예기치 못한 에러 발생: {}", e.getMessage());
            setErrorResponse(httpServletResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void setErrorResponse(HttpServletResponse response, int status, Object errorDetail) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = new ErrorResponse(status, errorDetail.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        log.debug("JWT 토큰이 제공되지 않았습니다. Authorization 헤더: {}", bearerToken);
        return null;
    }
}
