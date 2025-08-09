package com.hethong.baotri.dieu_khien;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TrangChuWebController {

    /**
     * ✅ TRANG CHỦ - LUÔN REDIRECT ĐẾN LOGIN TRƯỚC
     */
    @GetMapping("/")
    public String trangChu() {
        log.info("🏠 Root path accessed - redirecting to login");
        return "redirect:/login";
    }

    /**
     * ✅ TRANG ĐĂNG NHẬP
     */
    @GetMapping("/login")
    public String dangNhap(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           @RequestParam(value = "expired", required = false) String expired,
                           Model model) {

        log.info("🔐 Hiển thị trang đăng nhập");

        // Kiểm tra authentication hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            log.info("🔄 User {} đã đăng nhập, redirect về dashboard", auth.getName());
            return "redirect:/dashboard";
        }

        // Xử lý các message
        if (error != null) {
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng!");
            log.warn("❌ Đăng nhập thất bại");
        }

        if (logout != null) {
            model.addAttribute("successMessage", "Đã đăng xuất thành công!");
            log.info("✅ Đăng xuất thành công");
        }

        if (expired != null) {
            model.addAttribute("warningMessage", "Phiên đăng nhập đã hết hạn!");
            log.warn("⏰ Phiên đăng nhập hết hạn");
        }

        // Thêm thông tin demo users
        model.addAttribute("demoUsers", """
            Demo: admin/123456 |
            hieupho.nguyen/123456 |
            phong.tran/123456
            """);

        model.addAttribute("title", "Đăng nhập");
        return "nguoi-dung/dang-nhap"; // ✅ SỬA: Dùng template có sẵn
    }

    /**
     * ✅ XỬ LÝ SAU KHI ĐĂNG NHẬP THÀNH CÔNG
     */
    @GetMapping("/login-success")
    public String loginSuccess(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("✅ Login success redirect for user: {}", username);
            return "redirect:/dashboard";
        }

        log.warn("⚠️ Login success but no authentication found");
        return "redirect:/login";
    }

    /**
     * ✅ XỬ LÝ ACCESS DENIED
     */
    @GetMapping("/access-denied")
    public String accessDenied(Authentication authentication, Model model) {
        if (authentication != null) {
            log.warn("🚫 Access denied for user: {}", authentication.getName());
            model.addAttribute("username", authentication.getName());
            model.addAttribute("authorities", authentication.getAuthorities());
        }

        model.addAttribute("title", "Truy cập bị từ chối");
        return "error/403";
    }

    /**
     * ✅ ERROR HANDLER
     */
    @GetMapping("/error")
    public String error(Model model) {
        log.error("❌ Application error occurred");
        model.addAttribute("title", "Lỗi hệ thống");
        return "error/500";
    }

    /**
     * 🔧 TEST ENDPOINT - Chỉ để debug
     */
    @GetMapping("/test-auth")
    public String testAuth(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("auth", auth);
        model.addAttribute("isAuth", auth != null && auth.isAuthenticated());
        model.addAttribute("name", auth != null ? auth.getName() : "null");
        model.addAttribute("isAnonymous", auth != null && "anonymousUser".equals(auth.getName()));

        log.info("🔍 Test auth: {}", auth);
        return "debug/auth-test"; // Tạo template đơn giản để test
    }
}