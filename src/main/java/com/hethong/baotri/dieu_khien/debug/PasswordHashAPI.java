package com.hethong.baotri.dieu_khien.debug;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class PasswordHashAPI {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/hash-password")
    public ResponseEntity<Map<String, Object>> hashPassword(
            @RequestParam(defaultValue = "123456") String password) {

        Map<String, Object> result = new HashMap<>();

        try {
            // Tạo BCrypt hash
            String hashedPassword = passwordEncoder.encode(password);

            result.put("original_password", password);
            result.put("bcrypt_hash", hashedPassword);
            result.put("hash_length", hashedPassword.length());
            result.put("algorithm", "BCrypt");
            result.put("strength", "10 (default)");

            // SQL để update tất cả user
            String sqlUpdate = String.format("""
                UPDATE nguoi_dung 
                SET mat_khau = N'%s'
                WHERE ten_dang_nhap IN (
                    N'admin', N'hieupho.nguyen', N'phong.tran', 
                    N'duc.le', N'mai.pham', N'thanh.vo', 
                    N'hung.dao', N'linh.nguyen', N'minh.tran', N'hoa.le'
                );
                """, hashedPassword);

            result.put("sql_update_all_users", sqlUpdate);

            log.info("🔐 Generated BCrypt hash for password: [{}]", password);
            log.info("📝 Hash: [{}]", hashedPassword);

        } catch (Exception e) {
            log.error("❌ Error hashing password: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/verify-password")
    public ResponseEntity<Map<String, Object>> verifyPassword(
            @RequestParam String password,
            @RequestParam String hash) {

        Map<String, Object> result = new HashMap<>();

        try {
            boolean matches = passwordEncoder.matches(password, hash);

            result.put("password", password);
            result.put("hash_preview", hash.substring(0, Math.min(20, hash.length())) + "...");
            result.put("matches", matches);
            result.put("status", matches ? "✅ CORRECT" : "❌ INCORRECT");
            result.put("is_bcrypt", hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$"));

            log.info("🔍 Password verification: [{}] vs hash -> {}",
                    password, matches ? "MATCH ✅" : "NO MATCH ❌");

        } catch (Exception e) {
            log.error("❌ Error verifying password: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-database-passwords")
    public ResponseEntity<Map<String, Object>> checkDatabasePasswords() {
        Map<String, Object> result = new HashMap<>();

        result.put("message", "Use this API to generate correct BCrypt hashes");
        result.put("steps", new String[]{
                "1. Call /api/debug/hash-password?password=123456",
                "2. Copy the 'bcrypt_hash' from response",
                "3. Copy the 'sql_update_all_users' query",
                "4. Run the SQL in SQL Server Management Studio",
                "5. Test login again"
        });

        result.put("test_hash_url", "/api/debug/hash-password?password=123456");
        result.put("verify_url", "/api/debug/verify-password?password=123456&hash=YOUR_HASH");

        return ResponseEntity.ok(result);
    }
}