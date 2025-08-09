package com.hethong.baotri.dich_vu.migration;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "app.password-migration.enabled",
        havingValue = "true",
        matchIfMissing = false
)
public class PasswordMigrationService implements CommandLineRunner {

    private final NguoiDungRepository nguoiDungRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ DEFAULT PASSWORDS MAP - CONFIGURE AS NEEDED
    private final Map<String, String> defaultPasswords = Map.of(
            "admin", "admin123",
            "hieu_truong", "ht123",
            "truong_phong", "tp123",
            "nhan_vien", "nv123",
            "ky_thuat_vien", "ktv123",
            "giao_vien", "gv123"
    );

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("🔐 Starting Password Migration...");

        try {
            List<NguoiDung> allUsers = nguoiDungRepository.findAll();
            log.info("👥 Found {} users to process", allUsers.size());

            int updatedCount = 0;
            int skippedCount = 0;

            for (NguoiDung user : allUsers) {
                String currentPassword = user.getMatKhau();

                // Check if password is already BCrypt encoded
                if (currentPassword != null && currentPassword.startsWith("$2a$")) {
                    log.debug("⏭️ Skipping user {} - password already BCrypt encoded", user.getTenDangNhap());
                    skippedCount++;
                    continue;
                }

                String newPassword = determineNewPassword(user, currentPassword);
                if (newPassword != null) {
                    String encodedPassword = passwordEncoder.encode(newPassword);
                    user.setMatKhau(encodedPassword);
                    nguoiDungRepository.save(user);

                    log.info("✅ Updated password for user: {} (new password: {})",
                            user.getTenDangNhap(), newPassword);
                    updatedCount++;
                } else {
                    log.warn("⚠️ Could not determine password for user: {}", user.getTenDangNhap());
                    skippedCount++;
                }
            }

            log.info("🎉 Password Migration completed!");
            log.info("✅ Updated: {} users", updatedCount);
            log.info("⏭️ Skipped: {} users", skippedCount);

            // Print summary for easy login
            printLoginSummary();

        } catch (Exception e) {
            log.error("❌ Password Migration failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    private String determineNewPassword(NguoiDung user, String currentPassword) {
        String username = user.getTenDangNhap();

        // Strategy 1: Use default password map
        if (defaultPasswords.containsKey(username)) {
            return defaultPasswords.get(username);
        }

        // Strategy 2: Use current password if it looks like plain text
        if (currentPassword != null &&
                !currentPassword.startsWith("$") &&
                currentPassword.length() >= 3 &&
                currentPassword.length() <= 20) {
            return currentPassword;
        }

        // Strategy 3: Generate default password based on username
        return generateDefaultPassword(username);
    }

    private String generateDefaultPassword(String username) {
        // Simple default: username + "123"
        if (username.length() <= 7) {
            return username + "123";
        } else {
            return username.substring(0, 6) + "123";
        }
    }

    private void printLoginSummary() {
        log.info("");
        log.info("📋 ============ LOGIN CREDENTIALS SUMMARY ============");
        log.info("🔑 Default credentials (if no existing password found):");

        defaultPasswords.forEach((username, password) -> {
            log.info("   👤 {} / {}", username, password);
        });

        log.info("");
        log.info("💡 For other users: [username]123 (e.g., john123)");
        log.info("🌐 Login URL: http://localhost:8081/login");
        log.info("📋 ================================================");
        log.info("");
    }
}