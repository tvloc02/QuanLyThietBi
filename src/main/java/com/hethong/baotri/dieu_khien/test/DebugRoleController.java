package com.hethong.baotri.dieu_khien.test;

import com.hethong.baotri.dich_vu.nguoi_dung.CustomUserDetailsService;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.VaiTroRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.nguoi_dung.VaiTro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/debug")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Slf4j
public class DebugRoleController {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/check-roles")
    public ResponseEntity<Map<String, Object>> checkAllUserRoles() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> userRoles = new ArrayList<>();

        try {
            // ✅ Lấy tất cả user và role của họ
            List<NguoiDung> allUsers = nguoiDungRepository.findAll();

            for (NguoiDung user : allUsers) {
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("username", user.getTenDangNhap());
                userInfo.put("fullName", user.getHoVaTen());
                userInfo.put("active", user.getTrangThaiHoatDong());

                // ✅ Lấy roles từ database
                Set<String> dbRoles = new HashSet<>();
                for (VaiTro vaiTro : user.getVaiTroSet()) {
                    dbRoles.add(vaiTro.getTenVaiTro());
                }
                userInfo.put("dbRoles", dbRoles);

                // ✅ Lấy roles từ Spring Security
                try {
                    UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getTenDangNhap());
                    Set<String> securityRoles = new HashSet<>();
                    userDetails.getAuthorities().forEach(auth -> securityRoles.add(auth.getAuthority()));
                    userInfo.put("securityRoles", securityRoles);
                    userInfo.put("rolesMatch", dbRoles.equals(securityRoles));
                } catch (Exception e) {
                    userInfo.put("securityRoles", "ERROR: " + e.getMessage());
                    userInfo.put("rolesMatch", false);
                }

                userRoles.add(userInfo);
            }

            result.put("status", "SUCCESS");
            result.put("totalUsers", allUsers.size());
            result.put("userRoles", userRoles);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            log.error("Error checking user roles: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/check-user-role/{username}")
    public ResponseEntity<Map<String, Object>> checkUserRole(@PathVariable String username) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);

            if (userOpt.isEmpty()) {
                result.put("status", "USER_NOT_FOUND");
                result.put("username", username);
                return ResponseEntity.ok(result);
            }

            NguoiDung user = userOpt.get();

            // ✅ Thông tin user
            result.put("username", user.getTenDangNhap());
            result.put("fullName", user.getHoVaTen());
            result.put("email", user.getEmail());
            result.put("active", user.getTrangThaiHoatDong());
            result.put("accountLocked", !user.getTaiKhoanKhongBiKhoa());

            // ✅ Roles từ database
            List<Map<String, Object>> dbRoles = new ArrayList<>();
            for (VaiTro vaiTro : user.getVaiTroSet()) {
                Map<String, Object> roleInfo = new HashMap<>();
                roleInfo.put("id", vaiTro.getIdVaiTro());
                roleInfo.put("name", vaiTro.getTenVaiTro());
                roleInfo.put("active", vaiTro.getTrangThaiHoatDong());
                roleInfo.put("description", vaiTro.getMoTa());
                dbRoles.add(roleInfo);
            }
            result.put("dbRoles", dbRoles);

            // ✅ Roles từ Spring Security
            try {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
                List<String> securityRoles = new ArrayList<>();
                userDetails.getAuthorities().forEach(auth -> securityRoles.add(auth.getAuthority()));
                result.put("securityRoles", securityRoles);
                result.put("userDetailsEnabled", userDetails.isEnabled());
                result.put("userDetailsLocked", !userDetails.isAccountNonLocked());
            } catch (Exception e) {
                result.put("securityRoles", "ERROR: " + e.getMessage());
            }

            result.put("status", "SUCCESS");

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            log.error("Error checking user role for {}: {}", username, e.getMessage(), e);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/fix-user-roles")
    public ResponseEntity<Map<String, Object>> fixUserRoles() {
        Map<String, Object> result = new HashMap<>();
        List<String> fixedUsers = new ArrayList<>();

        try {
            // ✅ Mapping user -> role như trong migration
            Map<String, String> userRoleMap = Map.of(
                    "admin", "QUAN_TRI_VIEN",
                    "hieupho.nguyen", "HIEU_TRUONG",
                    "phong.tran", "TRUONG_PHONG_CSVC",
                    "duc.le", "NHAN_VIEN_CSVC",
                    "mai.pham", "NHAN_VIEN_CSVC",
                    "thanh.vo", "KY_THUAT_VIEN",
                    "hung.dao", "KY_THUAT_VIEN",
                    "linh.nguyen", "GIAO_VIEN",
                    "minh.tran", "GIAO_VIEN",
                    "hoa.le", "GIAO_VIEN"
            );

            for (Map.Entry<String, String> entry : userRoleMap.entrySet()) {
                String username = entry.getKey();
                String roleName = entry.getValue();

                Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
                Optional<VaiTro> roleOpt = vaiTroRepository.findByTenVaiTro(roleName);

                if (userOpt.isPresent() && roleOpt.isPresent()) {
                    NguoiDung user = userOpt.get();
                    VaiTro role = roleOpt.get();

                    // ✅ Clear existing roles và thêm role mới
                    user.getVaiTroSet().clear();
                    user.getVaiTroSet().add(role);

                    nguoiDungRepository.save(user);
                    fixedUsers.add(username + " -> " + roleName);

                    log.info("✅ Fixed role for {}: {}", username, roleName);
                }
            }

            result.put("status", "SUCCESS");
            result.put("fixedUsers", fixedUsers);
            result.put("totalFixed", fixedUsers.size());

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
            log.error("Error fixing user roles: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/list-all-roles")
    public ResponseEntity<Map<String, Object>> listAllRoles() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<VaiTro> allRoles = vaiTroRepository.findAll();
            List<Map<String, Object>> roleList = new ArrayList<>();

            for (VaiTro role : allRoles) {
                Map<String, Object> roleInfo = new HashMap<>();
                roleInfo.put("id", role.getIdVaiTro());
                roleInfo.put("name", role.getTenVaiTro());
                roleInfo.put("description", role.getMoTa());
                roleInfo.put("active", role.getTrangThaiHoatDong());
                roleList.add(roleInfo);
            }

            result.put("status", "SUCCESS");
            result.put("totalRoles", allRoles.size());
            result.put("roles", roleList);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/fix-roles")
    public ResponseEntity<Map<String, Object>> fixRoles() {
        Map<String, Object> result = new HashMap<>();
        try {
            // Fix role cho admin
            Optional<NguoiDung> adminOpt = nguoiDungRepository.findByTenDangNhap("admin");
            Optional<VaiTro> adminRoleOpt = vaiTroRepository.findByTenVaiTro("QUAN_TRI_VIEN");

            if (adminOpt.isPresent() && adminRoleOpt.isPresent()) {
                NguoiDung admin = adminOpt.get();
                admin.getVaiTroSet().clear();
                admin.getVaiTroSet().add(adminRoleOpt.get());
                nguoiDungRepository.save(admin);
            }

            // Fix role cho hieupho.nguyen
            Optional<NguoiDung> hieuOpt = nguoiDungRepository.findByTenDangNhap("hieupho.nguyen");
            Optional<VaiTro> hieuRoleOpt = vaiTroRepository.findByTenVaiTro("HIEU_TRUONG");

            if (hieuOpt.isPresent() && hieuRoleOpt.isPresent()) {
                NguoiDung hieu = hieuOpt.get();
                hieu.getVaiTroSet().clear();
                hieu.getVaiTroSet().add(hieuRoleOpt.get());
                nguoiDungRepository.save(hieu);
            }

            result.put("status", "SUCCESS");
            result.put("message", "Fixed roles for admin and hieupho.nguyen");

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/create-sample-data")
    public ResponseEntity<Map<String, Object>> createSampleData() {
        Map<String, Object> result = new HashMap<>();
        List<String> created = new ArrayList<>();

        try {
            // 1. Tạo roles
            String[] roleNames = {"QUAN_TRI_VIEN", "HIEU_TRUONG", "TRUONG_PHONG_CSVC",
                    "NHAN_VIEN_CSVC", "KY_THUAT_VIEN", "GIAO_VIEN"};

            for (String roleName : roleNames) {
                if (!vaiTroRepository.findByTenVaiTro(roleName).isPresent()) {
                    VaiTro role = new VaiTro();
                    role.setTenVaiTro(roleName);
                    role.setMoTa("Role " + roleName);
                    role.setTrangThaiHoatDong(true);
                    vaiTroRepository.save(role);
                    created.add("Role: " + roleName);
                }
            }

            // 2. Gán role cho users
            Map<String, String> userRoleMap = Map.of(
                    "admin", "QUAN_TRI_VIEN",
                    "hieupho.nguyen", "HIEU_TRUONG",
                    "phong.tran", "TRUONG_PHONG_CSVC"
            );

            for (Map.Entry<String, String> entry : userRoleMap.entrySet()) {
                Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(entry.getKey());
                Optional<VaiTro> roleOpt = vaiTroRepository.findByTenVaiTro(entry.getValue());

                if (userOpt.isPresent() && roleOpt.isPresent()) {
                    NguoiDung user = userOpt.get();
                    user.getVaiTroSet().clear();
                    user.getVaiTroSet().add(roleOpt.get());
                    nguoiDungRepository.save(user);
                    created.add("User: " + entry.getKey() + " -> " + entry.getValue());
                }
            }

            result.put("status", "SUCCESS");
            result.put("created", created);
            result.put("count", created.size());

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/assign-role/{username}/{roleName}")
    public ResponseEntity<Map<String, Object>> assignRole(@PathVariable String username, @PathVariable String roleName) {
        Map<String, Object> result = new HashMap<>();

        try {
            Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
            Optional<VaiTro> roleOpt = vaiTroRepository.findByTenVaiTro(roleName);

            if (userOpt.isPresent() && roleOpt.isPresent()) {
                NguoiDung user = userOpt.get();
                VaiTro role = roleOpt.get();

                // Clear existing roles first
                user.getVaiTroSet().clear();

                // Add new role
                user.getVaiTroSet().add(role);

                // Save user
                nguoiDungRepository.save(user);

                result.put("status", "SUCCESS");
                result.put("message", "Assigned " + roleName + " to " + username);
            } else {
                result.put("status", "NOT_FOUND");
                result.put("message", "User or role not found");
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/create-basic-users")
    public ResponseEntity<Map<String, Object>> createBasicUsers() {
        Map<String, Object> result = new HashMap<>();
        List<String> created = new ArrayList<>();

        try {
            // Create basic users manually
            String[][] users = {
                    {"admin", "Admin User", "admin@test.com", "QUAN_TRI_VIEN"},
                    {"hieupho.nguyen", "Nguyen Van Hieu", "hieu@test.com", "HIEU_TRUONG"}
            };

            for (String[] userData : users) {
                String username = userData[0];
                String fullName = userData[1];
                String email = userData[2];
                String roleName = userData[3];

                // Check if user exists
                if (!nguoiDungRepository.findByTenDangNhap(username).isPresent()) {
                    // Create user
                    NguoiDung user = new NguoiDung();
                    user.setTenDangNhap(username);
                    user.setHoVaTen(fullName);
                    user.setEmail(email);
                    user.setMatKhau("$2a$10$encrypted_password"); // Replace with actual encoded password
                    user.setTrangThaiHoatDong(true);
                    user.setTaiKhoanKhongBiKhoa(true);
                    user.setTaiKhoanKhongHetHan(true);
                    user.setThongTinDangNhapHopLe(true);

                    nguoiDungRepository.save(user);
                    created.add("Created user: " + username);
                }
            }

            result.put("status", "SUCCESS");
            result.put("created", created);

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }



    @GetMapping("/direct-assign-role/{username}/{roleName}")
    public ResponseEntity<Map<String, Object>> directAssignRole(@PathVariable String username, @PathVariable String roleName) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("Looking for user: {}", username);
            log.info("Looking for role: {}", roleName);

            Optional<NguoiDung> userOpt = nguoiDungRepository.findByTenDangNhap(username);
            Optional<VaiTro> roleOpt = vaiTroRepository.findByTenVaiTro(roleName);

            log.info("User found: {}", userOpt.isPresent());
            log.info("Role found: {}", roleOpt.isPresent());

            if (userOpt.isPresent() && roleOpt.isPresent()) {
                // ... rest of code
            } else {
                result.put("status", "NOT_FOUND");
                result.put("userFound", userOpt.isPresent());
                result.put("roleFound", roleOpt.isPresent());
            }

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }


}