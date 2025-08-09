package com.hethong.baotri.tien_ich;

import com.hethong.baotri.dich_vu.nguoi_dung.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && jwtTokenUtil.validateToken(jwt)) {
                String username = jwtTokenUtil.getUsernameFromToken(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                    if (jwtTokenUtil.validateToken(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        log.debug("✅ JWT Authentication set for user: {}", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ JWT Authentication error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // ✅ SỬA: Hoàn toàn bỏ qua JWT filter cho form-based authentication
        boolean shouldSkip =
                // Auth endpoints
                path.startsWith("/api/auth/") ||
                        path.startsWith("/api/public/") ||
                        path.startsWith("/api/debug/") ||
                        path.startsWith("/api/test/") ||

                        // Swagger
                        path.startsWith("/swagger-ui/") ||
                        path.startsWith("/v3/api-docs/") ||

                        // Health check
                        path.equals("/actuator/health") ||

                        // ✅ QUAN TRỌNG: Bỏ qua tất cả form login processing
                        path.equals("/login") ||
                        path.equals("/logout") ||
                        path.equals("/perform_login") ||  // Form login processing URL
                        path.equals("/login-success") ||
                        path.equals("/access-denied") ||
                        path.equals("/error") ||
                        path.equals("/") ||

                        // Static resources
                        path.startsWith("/static/css/") ||
                        path.startsWith("/js/") ||
                        path.startsWith("/images/") ||
                        path.startsWith("/webjars/") ||
                        path.startsWith("/static/") ||
                        path.equals("/favicon.ico") ||

                        // ✅ THÊM: Ignore browser extension requests
                        path.startsWith("/hybridaction/") ||

                        // H2 Console
                        path.startsWith("/h2-console/") ||

                        // ✅ THÊM: Bỏ qua tất cả POST requests đến form login
                        ("POST".equals(method) && path.equals("/perform_login"));

        if (shouldSkip) {
            log.debug("🚫 JWT Filter skipped for: {} {}", method, path);
        }

        return shouldSkip;
    }
}