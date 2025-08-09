package com.hethong.baotri.cau_hinh;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationHandlers {

    // ✅ Inject NguoiDungService sau khi context đã được tạo
    private final NguoiDungService nguoiDungService;

    @Component("customSuccessHandler")
    public static class CustomSuccessHandler implements AuthenticationSuccessHandler {

        private NguoiDungService nguoiDungService;

        // ✅ Setter injection để tránh circular dependency
        public void setNguoiDungService(NguoiDungService nguoiDungService) {
            this.nguoiDungService = nguoiDungService;
        }

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication) throws IOException {
            log.info("✅ Đăng nhập thành công cho: {}", authentication.getName());

            // Cập nhật lần đăng nhập cuối (chỉ khi service có sẵn)
            if (nguoiDungService != null) {
                try {
                    nguoiDungService.capNhatLanDangNhapCuoi(authentication.getName());
                } catch (Exception e) {
                    log.warn("⚠️ Không thể cập nhật lần đăng nhập cuối: {}", e.getMessage());
                }
            }

            // Redirect về dashboard
            response.sendRedirect("/dashboard");
        }
    }

    @Component("customFailureHandler")
    public static class CustomFailureHandler implements AuthenticationFailureHandler {

        private NguoiDungService nguoiDungService;

        // ✅ Setter injection để tránh circular dependency
        public void setNguoiDungService(NguoiDungService nguoiDungService) {
            this.nguoiDungService = nguoiDungService;
        }

        @Override
        public void onAuthenticationFailure(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException exception) throws IOException {
            log.warn("❌ Đăng nhập thất bại: {}", exception.getMessage());

            String username = request.getParameter("username");
            if (username != null && nguoiDungService != null) {
                try {
                    nguoiDungService.xuLyDangNhapThatBai(username);
                } catch (Exception e) {
                    log.warn("⚠️ Không thể xử lý đăng nhập thất bại: {}", e.getMessage());
                }
            }

            // Redirect về login với error
            response.sendRedirect("/login?error=true");
        }
    }
}