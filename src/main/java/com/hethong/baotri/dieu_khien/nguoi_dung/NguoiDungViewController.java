package com.hethong.baotri.dieu_khien.nguoi_dung;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import com.hethong.baotri.dich_vu.nguoi_dung.VaiTroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/nguoi-dung") // ✅ ĐÚNG URL cho view pages
@RequiredArgsConstructor
@Slf4j
public class NguoiDungViewController {

    private final NguoiDungService nguoiDungService;
    private final VaiTroService vaiTroService;

    /**
     * Hiển thị trang danh sách người dùng
     * URL: /nguoi-dung/danh-sach-nguoi-dung
     */
    @GetMapping("/danh-sach-nguoi-dung")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public String danhSachNguoiDung(Model model,
                                    @RequestParam(required = false) String success,
                                    @RequestParam(required = false) String error) {
        log.info("📥 Hiển thị trang danh sách người dùng");

        // Thêm các thông tin cần thiết vào model
        model.addAttribute("pageTitle", "Danh sách người dùng");
        model.addAttribute("breadcrumb", "Người dùng > Danh sách");

        // ✅ THÊM: Xử lý success/error parameters
        if ("created".equals(success)) {
            model.addAttribute("successMessage", "Thêm người dùng thành công!");
        } else if ("updated".equals(success)) {
            model.addAttribute("successMessage", "Cập nhật người dùng thành công!");
        } else if ("deleted".equals(success)) {
            model.addAttribute("successMessage", "Xóa người dùng thành công!");
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Đã xảy ra lỗi: " + error);
        }

        // ✅ SỬA: Return đúng tên file template bạn có
        return "nguoi-dung/danh-sach-nguoi-dung"; // ➡️ templates/nguoi-dung/danh-sach-nguoi-dung.html
    }

    /**
     * Hiển thị trang thêm người dùng
     * URL: /nguoi-dung/them-nguoi-dung
     */
    @GetMapping("/them-nguoi-dung")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_THEM')")
    public String themNguoiDung(Model model) {
        log.info("📥 Hiển thị trang thêm người dùng");

        // ✅ THÊM: Lấy danh sách vai trò nếu VaiTroService có method này
        try {
            // model.addAttribute("danhSachVaiTro", vaiTroService.layTatCaVaiTro());
        } catch (Exception e) {
            log.warn("⚠️ Không thể lấy danh sách vai trò: {}", e.getMessage());
        }

        model.addAttribute("pageTitle", "Thêm người dùng");
        model.addAttribute("breadcrumb", "Người dùng > Thêm mới");
        model.addAttribute("isEdit", false);

        return "nguoi-dung/them-nguoi-dung"; // ➡️ templates/nguoi-dung/them-nguoi-dung.html
    }

    /**
     * Hiển thị trang sửa người dùng
     * URL: /nguoi-dung/sua-nguoi-dung/{id}
     */
    @GetMapping("/sua-nguoi-dung/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public String suaNguoiDung(@PathVariable Long id, Model model) {
        log.info("📥 Hiển thị trang sửa người dùng ID: {}", id);

        try {
            // Lấy thông tin người dùng
            var nguoiDung = nguoiDungService.timNguoiDungTheoId(id);

            // ✅ THÊM: Lấy danh sách vai trò
            try {
                // model.addAttribute("danhSachVaiTro", vaiTroService.layTatCaVaiTro());
            } catch (Exception e) {
                log.warn("⚠️ Không thể lấy danh sách vai trò: {}", e.getMessage());
            }

            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("pageTitle", "Sửa người dùng");
            model.addAttribute("breadcrumb", "Người dùng > Sửa > " + nguoiDung.getHoVaTen());
            model.addAttribute("isEdit", true);
            model.addAttribute("userId", id); // ✅ THÊM: ID để form biết đang edit

            return "nguoi-dung/them-nguoi-dung"; // ➡️ Dùng chung template với thêm mới
        } catch (Exception e) {
            log.error("❌ Lỗi khi hiển thị trang sửa người dùng ID {}: {}", id, e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=khong-tim-thay";
        }
    }

    /**
     * Hiển thị chi tiết người dùng
     * URL: /nguoi-dung/chi-tiet/{id}
     */
    @GetMapping("/chi-tiet/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public String chiTietNguoiDung(@PathVariable Long id, Model model) {
        log.info("📥 Hiển thị chi tiết người dùng ID: {}", id);

        try {
            var nguoiDung = nguoiDungService.timNguoiDungTheoId(id);

            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("pageTitle", "Chi tiết người dùng");
            model.addAttribute("breadcrumb", "Người dùng > Chi tiết > " + nguoiDung.getHoVaTen());

            // ✅ SỬA: Return đúng tên template (giả sử bạn có file này)
            return "nguoi-dung/chi-tiet-nguoi-dung"; // ➡️ templates/nguoi-dung/chi-tiet-nguoi-dung.html
        } catch (Exception e) {
            log.error("❌ Lỗi khi hiển thị chi tiết người dùng ID {}: {}", id, e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=khong-tim-thay";
        }
    }

    /**
     * Hiển thị trang phân quyền người dùng
     * URL: /nguoi-dung/phan-quyen/{id}
     */
    @GetMapping("/phan-quyen/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public String phanQuyenNguoiDung(@PathVariable Long id, Model model) {
        log.info("📥 Hiển thị trang phân quyền người dùng ID: {}", id);

        try {
            var nguoiDung = nguoiDungService.timNguoiDungTheoId(id);

            // ✅ THÊM: Lấy danh sách vai trò để phân quyền
            try {
                // model.addAttribute("danhSachVaiTro", vaiTroService.layTatCaVaiTro());
            } catch (Exception e) {
                log.warn("⚠️ Không thể lấy danh sách vai trò: {}", e.getMessage());
            }

            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("pageTitle", "Phân quyền người dùng");
            model.addAttribute("breadcrumb", "Người dùng > Phân quyền > " + nguoiDung.getHoVaTen());
            model.addAttribute("userId", id); // ✅ THÊM: ID để form biết đang phân quyền cho ai

            // ✅ SỬA: Return đúng tên template (giả sử bạn có file này)
            return "nguoi-dung/phan-quyen-nguoi-dung"; // ➡️ templates/nguoi-dung/phan-quyen-nguoi-dung.html
        } catch (Exception e) {
            log.error("❌ Lỗi khi hiển thị trang phân quyền người dùng ID {}: {}", id, e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=khong-tim-thay";
        }
    }

    /**
     * ✅ THÊM: Xử lý xóa người dùng (confirmation page)
     */
    @GetMapping("/xoa-nguoi-dung/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XOA')")
    public String xoaNguoiDung(@PathVariable Long id, Model model) {
        log.info("📥 Hiển thị trang xác nhận xóa người dùng ID: {}", id);

        try {
            var nguoiDung = nguoiDungService.timNguoiDungTheoId(id);

            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("pageTitle", "Xóa người dùng");
            model.addAttribute("breadcrumb", "Người dùng > Xóa > " + nguoiDung.getHoVaTen());
            model.addAttribute("userId", id);

            return "nguoi-dung/xoa-nguoi-dung"; // ➡️ templates/nguoi-dung/xoa-nguoi-dung.html
        } catch (Exception e) {
            log.error("❌ Lỗi khi hiển thị trang xóa người dùng ID {}: {}", id, e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=khong-tim-thay";
        }
    }

    /**
     * Xử lý redirect sau khi thao tác thành công
     */
    @GetMapping("/success")
    public String success(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("successMessage", message != null ? message : "Thao tác thành công!");
        return "redirect:/nguoi-dung/danh-sach-nguoi-dung?success=true";
    }

    /**
     * Xử lý lỗi
     */
    @GetMapping("/error")
    public String error(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message != null ? message : "Đã xảy ra lỗi!");
        return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=true";
    }

    /**
     * ✅ THÊM: Hiển thị modal hoặc form import người dùng
     */
    @GetMapping("/import-nguoi-dung")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_THEM')")
    public String importNguoiDung(Model model) {
        log.info("📥 Hiển thị trang import người dùng");

        model.addAttribute("pageTitle", "Import người dùng");
        model.addAttribute("breadcrumb", "Người dùng > Import");

        return "nguoi-dung/import-nguoi-dung"; // ➡️ templates/nguoi-dung/import-nguoi-dung.html
    }

    /**
     * ✅ THÊM: Hiển thị trang export/báo cáo người dùng
     */
    @GetMapping("/bao-cao-nguoi-dung")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public String baoCaoNguoiDung(Model model) {
        log.info("📥 Hiển thị trang báo cáo người dùng");

        model.addAttribute("pageTitle", "Báo cáo người dùng");
        model.addAttribute("breadcrumb", "Người dùng > Báo cáo");

        return "nguoi-dung/bao-cao-nguoi-dung"; // ➡️ templates/nguoi-dung/bao-cao-nguoi-dung.html
    }
}