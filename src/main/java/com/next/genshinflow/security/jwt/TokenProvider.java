package com.next.genshinflow.security.jwt;

import com.next.genshinflow.exception.BusinessLogicException;
import com.next.genshinflow.exception.ExceptionCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final long tokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private Key key;

    public TokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds,
        @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInMilliseconds
    ) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            this.key = Keys.hmacShaKeyFor(keyBytes);
        }
        catch (IllegalArgumentException e) {
            logger.error("Error decoding secret key: ", e);
            throw new IllegalArgumentException("Invalid Base64 Key", e);
        }
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return Jwts.builder()
            .setSubject(authentication.getName())
            .claim(AUTHORITIES_KEY, authorities)
            // 보안이 크게 중요하지 않아 성능을 위주로 HS256 알고리즘을 채택했음.
            // 보안 이슈 발생 시 HS512로 변경 요망
            .signWith(key, SignatureAlgorithm.HS256)
            .setExpiration(validity)
            .compact();
    }

    // 리프레시 토큰은 인증 정보 재발급에만 사용이 되므로 권한을 포함하고 있지 않아도 됨.
    public String createRefreshToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
            .setSubject(authentication.getName())
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        Collection<? extends GrantedAuthority> authorities =
            Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // User(member) Entity가 아닌 security.core.userdetails의 User임
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    // 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        }
        catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            logger.warn("잘못된 JWT 서명입니다.");
            throw new BusinessLogicException(ExceptionCode.JWT_SIGNATURE_INVALID);
        }
        catch (ExpiredJwtException e) {
            logger.warn("만료된 JWT 토큰입니다.");
            throw new BusinessLogicException(ExceptionCode.JWT_TOKEN_EXPIRED);
        }
        catch (UnsupportedJwtException e) {
            logger.warn("지원되지 않는 JWT 토큰입니다.");
            throw new BusinessLogicException(ExceptionCode.JWT_TOKEN_UNSUPPORTED);
        }
        catch (IllegalArgumentException e) {
            logger.error("잘못된 JWT 입력값이 제공되었습니다.");
            throw new BusinessLogicException(ExceptionCode.JWT_TOKEN_MALFORMED);
        }
    }
}
