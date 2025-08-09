package com.hethong.baotri.dieu_khien.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class DirectTestController {

    @GetMapping("/force-login")
    public String forceLogin(Model model) {
        log.info("🚀 Force login for testing...");

        // Tạo fake authentication
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin",
                null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        log.info("✅ Forced authentication set for admin");
        return "redirect:/dashboard";
    }

    @GetMapping("/check-dashboard")
    public String checkDashboard(Model model) {
        log.info("🔍 Checking dashboard access...");

        // Bypass security check
        model.addAttribute("tenDangNhap", "test-admin");
        model.addAttribute("totalUsers", "1,234");
        model.addAttribute("totalOrders", "5,678");
        model.addAttribute("totalSales", "$99,999");
        model.addAttribute("totalPending", "42");

        return "dashboard"; // Direct template access
    }
}