package com.sudal.lclink.sercurity;

import com.sudal.lclink.entity.User;
import com.sudal.lclink.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j  // 로그 추가
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final com.sudal.lclink.sercurity.JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        log.info("=== JWT Filter 시작 ===");
        log.info("Request URI: {}", request.getRequestURI());

        String token = resolveToken(request);
        log.info("추출된 토큰: {}", token);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            log.info("토큰 검증 성공");
            String userId = jwtTokenProvider.getUserId(token);
            log.info("토큰에서 추출한 userId: {}", userId);

            User user = userRepository.findByUserId(userId).orElse(null);

            if (user != null) {
                log.info("사용자 조회 성공: {}", userId);
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("인증 정보 설정 완료");
            } else {
                log.warn("사용자를 찾을 수 없음: {}", userId);
            }
        } else {
            log.warn("토큰이 없거나 유효하지 않음");
        }

        log.info("=== JWT Filter 종료 ===");
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("Authorization 헤더: {}", bearerToken);

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
