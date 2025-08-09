package com.hethong.baotri.dieu_khien.test;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.VaiTroRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.nguoi_dung.VaiTro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@Slf4j
public class DatabaseTestController {

    private final DataSource dataSource;
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;

    @GetMapping("/db-info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> info = new HashMap<>();

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            info.put("database", metaData.getDatabaseProductName());
            info.put("version", metaData.getDatabaseProductVersion());
            info.put("driver", metaData.getDriverName());
            info.put("url", metaData.getURL());
            info.put("username", metaData.getUserName());
            info.put("status", "connected");

            log.info("✅ Database connection test successful");

        } catch (Exception e) {
            log.error("❌ Database connection test failed: {}", e.getMessage());
            info.put("status", "error");
            info.put("error", e.getMessage());
        }

        return ResponseEntity.ok(info);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<NguoiDung> users = nguoiDungRepository.findAll();
            result.put("total_users", users.size());
            result.put("users", users.stream().map(user -> {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getIdNguoiDung());
                userInfo.put("username", user.getTenDangNhap());
                userInfo.put("fullName", user.getHoVaTen());
                userInfo.put("email", user.getEmail());
                userInfo.put("active", user.getTrangThaiHoatDong());
                userInfo.put("roles", user.getVaiTroSet().stream()
                        .map(VaiTro::getTenVaiTro)
                        .toList());
                return userInfo;
            }).toList());

            log.info("✅ Retrieved {} users from database", users.size());

        } catch (Exception e) {
            log.error("❌ Error retrieving users: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> getRoles() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<VaiTro> roles = vaiTroRepository.findAll();
            result.put("total_roles", roles.size());
            result.put("roles", roles.stream().map(role -> {
                Map<String, Object> roleInfo = new HashMap<>();
                roleInfo.put("id", role.getIdVaiTro());
                roleInfo.put("name", role.getTenVaiTro());
                roleInfo.put("description", role.getMoTa());
                roleInfo.put("active", role.getTrangThaiHoatDong());
                return roleInfo;
            }).toList());

            log.info("✅ Retrieved {} roles from database", roles.size());

        } catch (Exception e) {
            log.error("❌ Error retrieving roles: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/test-login")
    public ResponseEntity<Map<String, Object>> testLogin() {
        Map<String, Object> result = new HashMap<>();

        try {
            // Tìm user admin để test
            var adminUser = nguoiDungRepository.findByTenDangNhap("admin");

            if (adminUser.isPresent()) {
                NguoiDung admin = adminUser.get();
                result.put("admin_found", true);
                result.put("admin_username", admin.getTenDangNhap());
                result.put("admin_active", admin.getTrangThaiHoatDong());
                result.put("admin_unlocked", admin.getTaiKhoanKhongBiKhoa());
                result.put("failed_attempts", admin.getSoLanDangNhapThatBai());
                result.put("roles", admin.getVaiTroSet().stream()
                        .map(VaiTro::getTenVaiTro)
                        .toList());
            } else {
                result.put("admin_found", false);
                result.put("message", "Admin user not found in database");
            }

        } catch (Exception e) {
            log.error("❌ Error testing login: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}