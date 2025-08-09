package com.hethong.baotri.dieu_khien.nguoi_dung;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import com.hethong.baotri.dto.nguoi_dung.NguoiDungDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ WEB CONTROLLER - Xử lý giao diện HTML cho quản lý người dùng
 */
@Controller
@RequestMapping("/nguoi-dung/nguoi-dung")
@RequiredArgsConstructor
@Slf4j
public class NguoiDungWebController {

    private final NguoiDungService nguoiDungService;

    /**
     * ✅ Hiển thị danh sách người dùng - CHỈ ADMIN MỚI VÀO ĐƯỢC
     */
    @GetMapping("/danh-sach-nguoi-dung")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public String danhSachNguoiDung(Model model,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String search) {

        log.info("🔍 Truy cập danh sách người dùng - page: {}, size: {}, search: {}", page, size, search);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<NguoiDungDTO> danhSachNguoiDung;

            if (search != null && !search.trim().isEmpty()) {
                danhSachNguoiDung = nguoiDungService.timKiemNguoiDung(search.trim(), pageable);
                model.addAttribute("search", search.trim());
                log.info("🔍 Tìm kiếm người dùng với từ khóa: [{}]", search.trim());
            } else {
                danhSachNguoiDung = nguoiDungService.layDanhSachNguoiDung(pageable);
            }

            // ✅ Thêm thông tin vai trò cho từng người dùng
            danhSachNguoiDung.getContent().forEach(nguoiDung -> {
                try {
                    // Lấy danh sách vai trò từ database
                    String tenDangNhap = nguoiDung.getTenDangNhap();
                    var danhSachVaiTro = nguoiDungService.layDanhSachVaiTro(tenDangNhap);

                    // Tạo chuỗi hiển thị vai trò
                    String vaiTroDisplay = danhSachVaiTro.isEmpty() ?
                            "Chưa có vai trò" : String.join(", ", danhSachVaiTro);

                    // Thêm thuộc tính tạm thời (nếu DTO có setter)
                    // nguoiDung.setVaiTroDisplay(vaiTroDisplay);

                } catch (Exception e) {
                    log.warn("⚠️ Không thể lấy vai trò cho user: {}", nguoiDung.getTenDangNhap());
                }
            });

            model.addAttribute("danhSachNguoiDung", danhSachNguoiDung);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", danhSachNguoiDung.getTotalPages());
            model.addAttribute("totalItems", danhSachNguoiDung.getTotalElements());
            model.addAttribute("title", "Danh sách người dùng");

            log.info("✅ Load thành công {} người dùng", danhSachNguoiDung.getTotalElements());
            return "nguoi-dung/danh-sach-nguoi-dung";

        } catch (Exception e) {
            log.error("❌ Lỗi khi load danh sách người dùng: {}", e.getMessage(), e);
            model.addAttribute("errorMessage", "Không thể tải danh sách người dùng: " + e.getMessage());
            model.addAttribute("title", "Lỗi hệ thống");
            return "error/500";
        }
    }

    /**
     * ✅ Hiển thị form thêm người dùng mới
     */
    @GetMapping("/them-moi")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_THEM')")
    public String formThemNguoiDung(Model model) {
        log.info("📝 Hiển thị form thêm người dùng mới");

        model.addAttribute("nguoiDung", new NguoiDungDTO());
        model.addAttribute("title", "Thêm người dùng mới");
        model.addAttribute("action", "/nguoi-dung/them-moi");
        model.addAttribute("method", "POST");

        return "nguoi-dung/form";
    }

    /**
     * ✅ Xử lý form thêm người dùng
     */
    @PostMapping("/them-moi")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_THEM')")
    public String xuLyThemNguoiDung(@ModelAttribute NguoiDungDTO nguoiDungDTO, Model model) {
        log.info("➕ Xử lý thêm người dùng mới: {}", nguoiDungDTO.getTenDangNhap());

        try {
            nguoiDungService.taoNguoiDung(nguoiDungDTO);
            log.info("✅ Thêm người dùng thành công: {}", nguoiDungDTO.getTenDangNhap());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?success=created";
        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm người dùng: {}", e.getMessage());
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            model.addAttribute("nguoiDung", nguoiDungDTO);
            model.addAttribute("title", "Thêm người dùng mới");
            return "nguoi-dung/form";
        }
    }

    /**
     * ✅ Hiển thị form chỉnh sửa người dùng
     */
    @GetMapping("/chinh-sua/{id}")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public String formChinhSuaNguoiDung(@PathVariable Long id, Model model) {
        log.info("✏️ Hiển thị form chỉnh sửa người dùng ID: {}", id);

        try {
            NguoiDungDTO nguoiDung = nguoiDungService.timNguoiDungTheoId(id);
            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("title", "Chỉnh sửa người dùng");
            model.addAttribute("action", "/nguoi-dung/chinh-sua/" + id);
            model.addAttribute("method", "POST");

            return "nguoi-dung/form";
        } catch (Exception e) {
            log.error("❌ Lỗi khi load người dùng để chỉnh sửa: {}", e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=user_not_found";
        }
    }

    /**
     * ✅ Xử lý form chỉnh sửa người dùng
     */
    @PostMapping("/chinh-sua/{id}")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public String xuLyChinhSuaNguoiDung(@PathVariable Long id,
                                        @ModelAttribute NguoiDungDTO nguoiDungDTO,
                                        Model model) {
        log.info("💾 Xử lý chỉnh sửa người dùng ID: {}", id);

        try {
            nguoiDungService.capNhatNguoiDung(id, nguoiDungDTO);
            log.info("✅ Cập nhật người dùng thành công ID: {}", id);
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?success=updated";
        } catch (Exception e) {
            log.error("❌ Lỗi khi cập nhật người dùng: {}", e.getMessage());
            model.addAttribute("errorMessage", "Lỗi: " + e.getMessage());
            model.addAttribute("nguoiDung", nguoiDungDTO);
            model.addAttribute("title", "Chỉnh sửa người dùng");
            return "nguoi-dung/form";
        }
    }

    /**
     * ✅ Xóa người dùng
     */
    @PostMapping("/xoa/{id}")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_XOA')")
    public String xoaNguoiDung(@PathVariable Long id) {
        log.info("🗑️ Xóa người dùng ID: {}", id);

        try {
            nguoiDungService.xoaNguoiDung(id);
            log.info("✅ Xóa người dùng thành công ID: {}", id);
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?success=deleted";
        } catch (Exception e) {
            log.error("❌ Lỗi khi xóa người dùng: {}", e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=delete_failed";
        }
    }

    /**
     * ✅ Chi tiết người dùng (view only)
     */
    @GetMapping("/chi-tiet/{id}")
    @PreAuthorize("hasAuthority('QUAN_TRI_VIEN') or hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public String chiTietNguoiDung(@PathVariable Long id, Model model) {
        log.info("👁️ Xem chi tiết người dùng ID: {}", id);

        try {
            NguoiDungDTO nguoiDung = nguoiDungService.timNguoiDungTheoId(id);

            // Lấy danh sách vai trò
            var danhSachVaiTro = nguoiDungService.layDanhSachVaiTro(nguoiDung.getTenDangNhap());

            model.addAttribute("nguoiDung", nguoiDung);
            model.addAttribute("danhSachVaiTro", danhSachVaiTro);
            model.addAttribute("title", "Chi tiết người dùng");

            return "chi-tiet-nguoi-dung";
        } catch (Exception e) {
            log.error("❌ Lỗi khi load chi tiết người dùng: {}", e.getMessage());
            return "redirect:/nguoi-dung/danh-sach-nguoi-dung?error=user_not_found";
        }
    }
}