package com.hethong.baotri.dieu_khien.debug;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class DebugController {

    private final NguoiDungService nguoiDungService;
    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ TEST DATABASE CONNECTION
     */
    @GetMapping("/db-test")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();

        try {
            long userCount = nguoiDungRepository.count();
            List<NguoiDung> allUsers = nguoiDungRepository.findAll();

            result.put("success", true);
            result.put("totalUsers", userCount);
            result.put("users", allUsers.stream().map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("tenDangNhap", user.getTenDangNhap());
                userInfo.put("hoVaTen", user.getHoVaTen());
                userInfo.put("email", user.getEmail());
                userInfo.put("trangThaiHoatDong", user.getTrangThaiHoatDong());
                userInfo.put("passwordLength", user.getMatKhau() != null ? user.getMatKhau().length() : 0);
                return userInfo;
            }).toList());

            log.info("✅ Database test successful - Found {} users", userCount);

        } catch (Exception e) {
            log.error("❌ Database test failed: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * ✅ TEST USER LOGIN CREDENTIALS
     */
    @PostMapping("/test-login")
    public Map<String, Object> testLogin(@RequestParam String username,
                                         @RequestParam String password) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("🔍 Testing login for user: {}", username);

            // 1. Check if user exists
            var userOptional = nguoiDungService.timNguoiDungTheoTenDangNhap(username);
            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("error", "User not found");
                result.put("step", "USER_NOT_FOUND");
                return result;
            }

            NguoiDung user = userOptional.get();
            log.info("✅ User found: {}", user.getHoVaTen());

            // 2. Check if user is active
            if (!user.getTrangThaiHoatDong()) {
                result.put("success", false);
                result.put("error", "User is inactive");
                result.put("step", "USER_INACTIVE");
                return result;
            }

            // 3. Test password
            boolean passwordMatches = passwordEncoder.matches(password, user.getMatKhau());
            log.info("🔐 Password test result: {}", passwordMatches);

            result.put("success", passwordMatches);
            result.put("userInfo", Map.of(
                    "id", user.getId(),
                    "tenDangNhap", user.getTenDangNhap(),
                    "hoVaTen", user.getHoVaTen(),
                    "email", user.getEmail(),
                    "trangThaiHoatDong", user.getTrangThaiHoatDong()
            ));
            result.put("passwordMatches", passwordMatches);
            result.put("step", passwordMatches ? "SUCCESS" : "PASSWORD_MISMATCH");

            if (!passwordMatches) {
                result.put("error", "Password does not match");
            }

        } catch (Exception e) {
            log.error("❌ Login test failed: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("step", "EXCEPTION");
        }

        return result;
    }

    /**
     * ✅ CREATE TEST USER WITH KNOWN PASSWORD
     */
    @PostMapping("/create-test-user")
    public Map<String, Object> createTestUser() {
        Map<String, Object> result = new HashMap<>();

        try {
            String testUsername = "testuser";
            String testPassword = "123456";

            // Check if test user already exists
            var existingUser = nguoiDungService.timNguoiDungTheoTenDangNhap(testUsername);
            if (existingUser.isPresent()) {
                result.put("success", false);
                result.put("error", "Test user already exists");
                return result;
            }

            // Create test user
            NguoiDung testUser = new NguoiDung();
            testUser.setTenDangNhap(testUsername);
            testUser.setMatKhau(passwordEncoder.encode(testPassword));
            testUser.setHoVaTen("Test User");
            testUser.setEmail("test@example.com");
            testUser.setTrangThaiHoatDong(true);

            NguoiDung savedUser = nguoiDungRepository.save(testUser);

            result.put("success", true);
            result.put("message", "Test user created successfully");
            result.put("credentials", Map.of(
                    "username", testUsername,
                    "password", testPassword
            ));
            result.put("userId", savedUser.getId());

            log.info("✅ Test user created: {} / {}", testUsername, testPassword);

        } catch (Exception e) {
            log.error("❌ Failed to create test user: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * ✅ RESET PASSWORD FOR EXISTING USER
     */
    @PostMapping("/reset-password")
    public Map<String, Object> resetPassword(@RequestParam String username,
                                             @RequestParam String newPassword) {
        Map<String, Object> result = new HashMap<>();

        try {
            var userOptional = nguoiDungService.timNguoiDungTheoTenDangNhap(username);
            if (userOptional.isEmpty()) {
                result.put("success", false);
                result.put("error", "User not found");
                return result;
            }

            NguoiDung user = userOptional.get();
            user.setMatKhau(passwordEncoder.encode(newPassword));
            nguoiDungRepository.save(user);

            result.put("success", true);
            result.put("message", "Password reset successfully");
            result.put("username", username);
            result.put("newPassword", newPassword);

            log.info("✅ Password reset for user: {}", username);

        } catch (Exception e) {
            log.error("❌ Failed to reset password: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        return result;
    }
}