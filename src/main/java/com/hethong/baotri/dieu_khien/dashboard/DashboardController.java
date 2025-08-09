package com.hethong.baotri.dieu_khien.dashboard;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final NguoiDungService nguoiDungService;

    /**
     * ✅ DASHBOARD CHÍNH với PROPER TEMPLATE ROUTING
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String dashboard(Authentication authentication, Model model) {
        try {
            // ✅ KIỂM TRA AUTHENTICATION
            if (authentication == null || !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getName())) {
                log.warn("⚠️ Unauthorized access to dashboard");
                return "redirect:/login";
            }

            String username = authentication.getName();
            log.info("🏠 Dashboard access by user: [{}]", username);

            // ✅ Lấy thông tin user từ database
            NguoiDung currentUser = nguoiDungService.timNguoiDungTheoTenDangNhap(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // ✅ Lấy danh sách quyền của user
            List<String> userAuthorities = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // ✅ Xác định vai trò chính từ database hoặc authorities
            String primaryRole = determinePrimaryRole(currentUser, userAuthorities);
            log.info("👤 User: [{}], Primary Role: [{}], Authorities: {}", username, primaryRole, userAuthorities);

            // ✅ Thiết lập model attributes cơ bản
            setupBasicModelAttributes(model, currentUser, primaryRole, userAuthorities);

            // ✅ Route tới dashboard phù hợp dựa trên vai trò
            return routeToDashboard(primaryRole, model, currentUser);

        } catch (Exception e) {
            log.error("❌ Error loading dashboard for user: {}", authentication != null ? authentication.getName() : "unknown", e);
            return handleDashboardError(model, authentication, e);
        }
    }

    /**
     * ✅ Xác định vai trò chính của user
     */
    private String determinePrimaryRole(NguoiDung user, List<String> authorities) {
        // ✅ Ưu tiên vai trò từ database
        if (user.getVaiTroSet() != null && !user.getVaiTroSet().isEmpty()) {
            String dbRole = user.getVaiTroSet().iterator().next().getTenVaiTro();
            log.debug("🎭 Role from DB: [{}]", dbRole);
            return dbRole;
        }

        // ✅ Fallback: Xác định từ authorities
        if (authorities.contains("QUAN_TRI_VIEN") || authorities.contains("ADMIN")) {
            return "QUAN_TRI_VIEN";
        } else if (authorities.contains("TRUONG_PHONG_CSVC")) {
            return "TRUONG_PHONG_CSVC";
        } else if (authorities.contains("NHAN_VIEN_CSVC")) {
            return "NHAN_VIEN_CSVC";
        } else if (authorities.contains("KY_THUAT_VIEN")) {
            return "KY_THUAT_VIEN";
        } else if (authorities.contains("GIAO_VIEN")) {
            return "GIAO_VIEN";
        } else if (authorities.contains("HIEU_TRUONG")) {
            return "HIEU_TRUONG";
        }

        log.warn("⚠️ No specific role found for user [{}], using DEFAULT", user.getTenDangNhap());
        return "DEFAULT";
    }

    /**
     * ✅ Thiết lập attributes cơ bản cho model
     */
    private void setupBasicModelAttributes(Model model, NguoiDung user, String primaryRole, List<String> authorities) {
        // Thông tin user
        model.addAttribute("currentUser", user);
        model.addAttribute("primaryRole", primaryRole);
        model.addAttribute("userAuthorities", authorities);
        model.addAttribute("hoVaTen", user.getHoVaTen() != null ? user.getHoVaTen() : user.getTenDangNhap());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("tenDangNhap", user.getTenDangNhap());

        // Thời gian hiện tại
        model.addAttribute("currentTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        model.addAttribute("lastLogin", user.getLanDangNhapCuoi() != null ?
                user.getLanDangNhapCuoi().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "Chưa có");

        // Quyền truy cập
        model.addAttribute("canManageUsers", authorities.contains("QUAN_LY_NGUOI_DUNG_XEM") || authorities.contains("QUAN_TRI_VIEN"));
        model.addAttribute("canManageDevices", authorities.contains("QUAN_LY_THIET_BI_XEM") || authorities.contains("QUAN_TRI_VIEN"));
        model.addAttribute("canViewReports", authorities.contains("XEM_BAO_CAO_TONG_HOP") || authorities.contains("QUAN_TRI_VIEN"));
        model.addAttribute("canManageSystem", authorities.contains("QUAN_TRI_VIEN"));

        // Role title cho display
        model.addAttribute("roleTitle", getRoleDisplayName(primaryRole));
    }

    /**
     * ✅ Route tới dashboard tương ứng với vai trò
     */
    private String routeToDashboard(String primaryRole, Model model, NguoiDung user) {
        try {
            switch (primaryRole) {
                case "QUAN_TRI_VIEN":
                    return loadAdminDashboard(model, user);
                case "HIEU_TRUONG":
                    return loadHieuTruongDashboard(model, user);
                case "TRUONG_PHONG_CSVC":
                    return loadTruongPhongCSVCDashboard(model, user);
                case "NHAN_VIEN_CSVC":
                    return loadNhanVienCSVCDashboard(model, user);
                case "KY_THUAT_VIEN":
                    return loadKyThuatVienDashboard(model, user);
                case "GIAO_VIEN":
                    return loadGiaoVienDashboard(model, user);
                default:
                    return loadDefaultDashboard(model, user);
            }
        } catch (Exception e) {
            log.error("❌ Error routing to dashboard for role [{}]: {}", primaryRole, e.getMessage());
            return loadDefaultDashboard(model, user);
        }
    }

    /**
     * 🔧 ADMIN DASHBOARD
     */
    private String loadAdminDashboard(Model model, NguoiDung user) {
        log.info("🔧 Loading ADMIN Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard Quản trị viên");
        model.addAttribute("dashboardType", "admin");

        // Admin statistics
        try {
            model.addAttribute("tongNguoiDung", nguoiDungService.demTongNguoiDung());
            model.addAttribute("nguoiDungHoatDong", 0); // TODO: implement
            model.addAttribute("tongThietBi", 0);
            model.addAttribute("tongYeuCauBaoTri", 0);
            model.addAttribute("yeuCauChoDuyet", 0);
            model.addAttribute("tongVatTu", 0);
            model.addAttribute("vatTuThieuHang", 0);
            model.addAttribute("tongCanhBao", 0);
            model.addAttribute("tongDoiBaoTri", 0);
            model.addAttribute("doiBaoTriHoatDong", 0);
        } catch (Exception e) {
            log.warn("⚠️ Could not load admin statistics: {}", e.getMessage());
        }

        return "dashboard/admin";
    }

    /**
     * 🏫 HIỆU TRƯỞNG DASHBOARD
     */
    private String loadHieuTruongDashboard(Model model, NguoiDung user) {
        log.info("🏫 Loading HIỆU TRƯỞNG Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard Hiệu trưởng");
        model.addAttribute("dashboardType", "hieu_truong");

        return "dashboard/hieu-truong";
    }

    /**
     * 🏢 TRƯỞNG PHÒNG CSVC DASHBOARD
     */
    private String loadTruongPhongCSVCDashboard(Model model, NguoiDung user) {
        log.info("🏢 Loading TRƯỞNG PHÒNG CSVC Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard Trưởng phòng CSVC");
        model.addAttribute("dashboardType", "truong_phong_csvc");

        return "dashboard/truong-phong-csvc";
    }

    /**
     * 👷 NHÂN VIÊN CSVC DASHBOARD
     */
    private String loadNhanVienCSVCDashboard(Model model, NguoiDung user) {
        log.info("👷 Loading NHÂN VIÊN CSVC Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard Nhân viên CSVC");
        model.addAttribute("dashboardType", "nhan_vien_csvc");

        return "dashboard/nhan-vien-csvc";
    }

    /**
     * 🔧 KỸ THUẬT VIÊN DASHBOARD
     */
    private String loadKyThuatVienDashboard(Model model, NguoiDung user) {
        log.info("🔧 Loading KỸ THUẬT VIÊN Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard Kỹ thuật viên");
        model.addAttribute("dashboardType", "ky_thuat_vien");

        return "dashboard/ky-thuat-vien";
    }

    /**
     * 👨‍🏫 GIÁO VIÊN DASHBOARD
     */
    private String loadGiaoVienDashboard(Model model, NguoiDung user) {
        log.info("👨‍🏫 Loading GIÁO VIÊN Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard Giáo viên");
        model.addAttribute("dashboardType", "giao_vien");

        return "dashboard/giao-vien";
    }

    /**
     * ❓ DEFAULT DASHBOARD
     */
    private String loadDefaultDashboard(Model model, NguoiDung user) {
        log.info("❓ Loading DEFAULT Dashboard for: [{}]", user.getTenDangNhap());

        model.addAttribute("title", "Dashboard");
        model.addAttribute("dashboardType", "default");

        return "dashboard/default";
    }

    /**
     * ✅ Xử lý lỗi dashboard
     */
    private String handleDashboardError(Model model, Authentication authentication, Exception e) {
        log.error("🚨 Dashboard error fallback");

        model.addAttribute("title", "Lỗi Dashboard");
        model.addAttribute("errorMessage", "Không thể tải dashboard: " + e.getMessage());
        model.addAttribute("username", authentication != null ? authentication.getName() : "Unknown");

        return "error/dashboard-error";
    }

    /**
     * ✅ Lấy tên hiển thị của vai trò
     */
    private String getRoleDisplayName(String role) {
        return switch (role) {
            case "QUAN_TRI_VIEN" -> "Quản trị viên";
            case "HIEU_TRUONG" -> "Hiệu trưởng";
            case "TRUONG_PHONG_CSVC" -> "Trưởng phòng CSVC";
            case "NHAN_VIEN_CSVC" -> "Nhân viên CSVC";
            case "KY_THUAT_VIEN" -> "Kỹ thuật viên";
            case "GIAO_VIEN" -> "Giáo viên";
            default -> "Người dùng";
        };
    }
}