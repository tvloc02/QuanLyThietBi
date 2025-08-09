package com.hethong.baotri.dieu_khien.debug;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ✅ DEBUG Controller để kiểm tra và sửa mật khẩu
 * CHỈ dùng cho development - XÓA ở production
 */
@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class PasswordDebugController {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * ✅ Kiểm tra mật khẩu của user
     */
    @GetMapping("/check-password/{username}")
    public ResponseEntity<Map<String, Object>> checkPassword(
            @PathVariable String username,
            @RequestParam String password) {

        Map<String, Object> result = new HashMap<>();

        try {
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

            if (userOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "User not found: " + username);
                return ResponseEntity.ok(result);
            }

            NguoiDung user = userOpt.get();
            String storedPassword = user.getMatKhau();

            // ✅ Kiểm tra password
            boolean matches = passwordEncoder.matches(password, storedPassword);

            result.put("success", true);
            result.put("username", username);
            result.put("inputPassword", password);
            result.put("storedPasswordPrefix", storedPassword.substring(0, Math.min(20, storedPassword.length())) + "...");
            result.put("passwordMatches", matches);
            result.put("isBCryptFormat", storedPassword.startsWith("$2"));
            result.put("active", user.getTrangThaiHoatDong());

            log.info("🔍 Password check for [{}]: matches={}, BCrypt={}",
                    username, matches, storedPassword.startsWith("$2"));

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error checking password for user: {}", username, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * ✅ Cập nhật mật khẩu cho user (encode BCrypt)
     */
    @RequestMapping(value = "/fix-password/{username}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> fixPassword(
            @PathVariable String username,
            @RequestParam String newPassword) {

        Map<String, Object> result = new HashMap<>();

        try {
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

            if (userOpt.isEmpty()) {
                result.put("success", false);
                result.put("message", "User not found: " + username);
                return ResponseEntity.ok(result);
            }

            NguoiDung user = userOpt.get();
            String oldPassword = user.getMatKhau();

            // ✅ Encode password với BCrypt
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setMatKhau(encodedPassword);

            // ✅ Save user
            nguoiDungRepository.save(user);

            result.put("success", true);
            result.put("username", username);
            result.put("message", "Password updated successfully");
            result.put("oldPasswordPrefix", oldPassword.substring(0, Math.min(10, oldPassword.length())) + "...");
            result.put("newPasswordPrefix", encodedPassword.substring(0, Math.min(20, encodedPassword.length())) + "...");

            log.info("✅ Password updated for user [{}]", username);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error updating password for user: {}", username, e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * ✅ Fix tất cả demo users với password = "123456"
     */
    @RequestMapping(value = "/fix-all-demo-passwords", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> fixAllDemoPasswords() {
        Map<String, Object> result = new HashMap<>();

        try {
            // ✅ THÊM: Tất cả demo users từ log
            String[] demoUsers = {
                    "admin", "hieupho.nguyen", "phong.tran",
                    "duc.le", "thanh.vo", "linh.nguyen",
                    "mai.csvc", "hoa.le"  // Thêm user khác nếu có
            };
            String defaultPassword = "123456";

            Map<String, String> results = new HashMap<>();

            for (String username : demoUsers) {
                Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

                if (userOpt.isPresent()) {
                    NguoiDung user = userOpt.get();

                    // ✅ Encode password với BCrypt
                    String encodedPassword = passwordEncoder.encode(defaultPassword);
                    user.setMatKhau(encodedPassword);

                    // ✅ Đảm bảo user active
                    user.setTrangThaiHoatDong(true);
                    user.setTaiKhoanKhongBiKhoa(true);
                    user.setTaiKhoanKhongHetHan(true);
                    user.setThongTinDangNhapHopLe(true);

                    nguoiDungRepository.save(user);

                    results.put(username, "✅ Updated successfully");
                    log.info("✅ Fixed password for user: {}", username);
                } else {
                    results.put(username, "❌ User not found");
                    log.warn("⚠️ User not found: {}", username);
                }
            }

            result.put("success", true);
            result.put("message", "All demo passwords update completed");
            result.put("results", results);
            result.put("defaultPassword", defaultPassword);
            result.put("totalUsersProcessed", demoUsers.length);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error fixing demo passwords", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }

    /**
     * ✅ Liệt kê tất cả users
     */
    @GetMapping("/list-users")
    public ResponseEntity<Map<String, Object>> listUsers() {
        Map<String, Object> result = new HashMap<>();

        try {
            var users = nguoiDungRepository.findAll();

            Map<String, Object> userList = new HashMap<>();

            for (NguoiDung user : users) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("hoVaTen", user.getHoVaTen());
                userInfo.put("email", user.getEmail());
                userInfo.put("active", user.getTrangThaiHoatDong());
                userInfo.put("passwordPrefix", user.getMatKhau() != null ?
                        user.getMatKhau().substring(0, Math.min(15, user.getMatKhau().length())) + "..." : "null");
                userInfo.put("isBCrypt", user.getMatKhau() != null && user.getMatKhau().startsWith("$2"));

                userList.put(user.getTenDangNhap(), userInfo);
            }

            result.put("success", true);
            result.put("totalUsers", users.size());
            result.put("users", userList);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("❌ Error listing users", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.ok(result);
        }
    }
}