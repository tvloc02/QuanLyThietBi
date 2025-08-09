package com.hethong.baotri.cau_hinh;

import com.hethong.baotri.dich_vu.nguoi_dung.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class CauHinhBaoMat {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    @Primary
    public PasswordEncoder passwordEncoder() {
        log.info("🔐 Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        log.info("🔧 Creating DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        log.info("🔧 Creating AuthenticationManager");
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String username = authentication.getName();
            log.info("✅ LOGIN SUCCESS for: {} with authorities: {}", username, authentication.getAuthorities());
            response.sendRedirect("/dashboard");
        };
    }

    @Bean
    public AuthenticationFailureHandler failureHandler() {
        return (request, response, exception) -> {
            String username = request.getParameter("username");
            log.warn("❌ LOGIN FAILED for: {} - Error: {}", username, exception.getMessage());
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    @Primary
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("🔧 Configuring Spring Security FilterChain...");

        http
                .authorizeHttpRequests(auth -> auth
                        // ✅ PUBLIC ENDPOINTS
                        .requestMatchers(
                                "/login",
                                "/perform_login",
                                "/error",
                                "/api/debug/**",
                                "/static/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico",
                                "/static/**",
                                "/webjars/**",
                                "/access-denied",
                                "/hybridaction/**",
                                "/api/public/**"
                        ).permitAll()

                        // ✅ ROOT PATH
                        .requestMatchers("/").permitAll()

                        // ✅ DASHBOARD - Authenticated users
                        .requestMatchers("/dashboard/**").authenticated()

                        // ✅ ADMIN ONLY endpoints
                        .requestMatchers("/admin/**").hasAnyAuthority("QUAN_TRI_VIEN", "ADMIN")
                        .requestMatchers("/api/admin/**").hasAnyAuthority("QUAN_TRI_VIEN", "ADMIN")

                        // 🔥 FIX: ĐÚNG ENDPOINT PATTERNS - MATCH VỚI CONTROLLER
                        // Người dùng endpoints - Match với @RequestMapping("/nguoi-dung/nguoi-dung")
                        .requestMatchers("/nguoi-dung/nguoi-dung/**").hasAnyAuthority(
                                "QUAN_LY_NGUOI_DUNG_XEM",
                                "QUAN_LY_NGUOI_DUNG_THEM",
                                "QUAN_LY_NGUOI_DUNG_SUA",
                                "QUAN_LY_NGUOI_DUNG_XOA",
                                "QUAN_TRI_VIEN",
                                "ADMIN"
                        )

                        // Vai trò endpoints - Match với @RequestMapping("/api/vai-tro")
                        .requestMatchers("/api/vai-tro/**").hasAnyAuthority(
                                "QUAN_LY_VAI_TRO_QUYEN",
                                "QUAN_TRI_VIEN",
                                "ADMIN"
                        )

                        // ✅ THỐNG KÊ & BÁO CÁO
                        .requestMatchers("/api/thong-ke/**").hasAnyAuthority(
                                "XEM_BAO_CAO_TONG_HOP",
                                "XEM_THONG_KE_BAO_TRI",
                                "QUAN_TRI_VIEN",
                                "ADMIN"
                        )

                        // ✅ PHÂN CÔNG CÔNG VIỆC
                        .requestMatchers("/nguoi-dung/nguoi-dung/phan-cong").hasAnyAuthority(
                                "PHAN_CONG_CONG_VIEC",
                                "QUAN_TRI_VIEN",
                                "ADMIN"
                        )

                        // ✅ BẢO TRÌ ENDPOINTS
                        .requestMatchers("/api/bao-tri/**").hasAnyAuthority(
                                "TAO_YEU_CAU_BAO_TRI",
                                "DUYET_YEU_CAU_BAO_TRI",
                                "THUC_HIEN_BAO_TRI",
                                "KIEM_TRA_DINH_KY",
                                "QUAN_TRI_VIEN",
                                "ADMIN"
                        )

                        // ✅ THIẾT BỊ ENDPOINTS
                        .requestMatchers("/api/thiet-bi/**").hasAnyAuthority(
                                "QUAN_LY_THIET_BI_XEM",
                                "QUAN_LY_THIET_BI_THEM",
                                "QUAN_LY_THIET_BI_SUA",
                                "QUAN_LY_THIET_BI_XOA",
                                "QUAN_TRI_VIEN",
                                "ADMIN"
                        )

                        // ✅ SỬA: Tất cả endpoints khác chỉ cần authenticated
                        .anyRequest().authenticated()
                )

                // ✅ FORM LOGIN CONFIGURATION
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/perform_login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(successHandler())
                        .failureHandler(failureHandler())
                        .permitAll()
                )

                // ✅ LOGOUT CONFIGURATION
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/login?logout=true")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )

                // ✅ EXCEPTION HANDLING
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.warn("🚫 Unauthorized access to: {} from IP: {}",
                                    request.getRequestURI(),
                                    request.getRemoteAddr());
                            response.sendRedirect("/login");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.warn("🚫 Access denied to: {} for user: {} - Required authorities for URL: {}",
                                    request.getRequestURI(),
                                    request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous",
                                    "Check controller @PreAuthorize annotations");
                            response.sendRedirect("/access-denied");
                        })
                )

                // ✅ SESSION MANAGEMENT
                .sessionManagement(session -> session
                        .maximumSessions(10)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/login?expired=true")
                )

                // ✅ CSRF PROTECTION
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/api/**",
                                "/debug/**",
                                "/hybridaction/**"
                        )
                );

        log.info("✅ Spring Security configuration completed with CORRECT endpoint patterns");
        return http.build();
    }
}