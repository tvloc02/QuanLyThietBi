package com.hethong.baotri.dieu_khien.nguoi_dung;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import com.hethong.baotri.dto.nguoi_dung.NguoiDungDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/nguoi-dung") // ✅ SỬA: Thêm /api để phân biệt với view controller
@RequiredArgsConstructor
@Slf4j
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;

    @PostMapping("/them-nguoi-dung")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_THEM')")
    public ResponseEntity<NguoiDungDTO> taoNguoiDung(@Valid @RequestBody NguoiDungDTO nguoiDungDTO) {
        log.info("📥 Nhận yêu cầu POST /api/nguoi-dung/them-nguoi-dung với dữ liệu: {}", nguoiDungDTO);
        try {
            NguoiDungDTO nguoiDungMoi = nguoiDungService.taoNguoiDung(nguoiDungDTO);
            log.info("✅ Thêm người dùng thành công: {}", nguoiDungDTO.getTenDangNhap());
            return ResponseEntity.ok(nguoiDungMoi);
        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm người dùng: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public ResponseEntity<NguoiDungDTO> capNhatNguoiDung(@PathVariable Long id,
                                                         @Valid @RequestBody NguoiDungDTO nguoiDungDTO) {
        log.info("📥 Nhận yêu cầu PUT /api/nguoi-dung/{} với dữ liệu: {}", id, nguoiDungDTO);
        NguoiDungDTO nguoiDungCapNhat = nguoiDungService.capNhatNguoiDung(id, nguoiDungDTO);
        log.info("✅ Cập nhật người dùng thành công ID: {}", id);
        return ResponseEntity.ok(nguoiDungCapNhat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XOA')")
    public ResponseEntity<Void> xoaNguoiDung(@PathVariable Long id) {
        log.info("📥 Nhận yêu cầu DELETE /api/nguoi-dung/{}", id);
        nguoiDungService.xoaNguoiDung(id);
        log.info("✅ Xóa người dùng thành công ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public ResponseEntity<NguoiDungDTO> layNguoiDung(@PathVariable Long id) {
        log.info("📥 Nhận yêu cầu GET /api/nguoi-dung/{}", id);
        NguoiDungDTO nguoiDung = nguoiDungService.timNguoiDungTheoId(id);
        log.info("✅ Lấy thông tin người dùng thành công ID: {}", id);
        return ResponseEntity.ok(nguoiDung);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public ResponseEntity<Page<NguoiDungDTO>> layDanhSachNguoiDung(Pageable pageable) {
        log.info("📥 Nhận yêu cầu GET /api/nguoi-dung với page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<NguoiDungDTO> danhSach = nguoiDungService.layDanhSachNguoiDung(pageable);
        log.info("✅ Lấy danh sách người dùng thành công");
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/tim-kiem")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_XEM')")
    public ResponseEntity<Page<NguoiDungDTO>> timKiemNguoiDung(@RequestParam String tuKhoa,
                                                               Pageable pageable) {
        log.info("📥 Nhận yêu cầu GET /api/nguoi-dung/tim-kiem với từ khóa: {}", tuKhoa);
        Page<NguoiDungDTO> ketQua = nguoiDungService.timKiemNguoiDung(tuKhoa, pageable);
        log.info("✅ Tìm kiếm người dùng thành công");
        return ResponseEntity.ok(ketQua);
    }

    @PostMapping("/{id}/gan-vai-tro")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Void> ganVaiTro(@PathVariable Long id, @RequestParam Long idVaiTro) {
        log.info("📥 Nhận yêu cầu POST /api/nguoi-dung/{}/gan-vai-tro với idVaiTro: {}", id, idVaiTro);
        nguoiDungService.ganVaiTro(id, idVaiTro);
        log.info("✅ Gắn vai trò thành công cho user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/go-vai-tro")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Void> goVaiTro(@PathVariable Long id, @RequestParam Long idVaiTro) {
        log.info("📥 Nhận yêu cầu DELETE /api/nguoi-dung/{}/go-vai-tro với idVaiTro: {}", id, idVaiTro);
        nguoiDungService.goVaiTro(id, idVaiTro);
        log.info("✅ Gỡ vai trò thành công khỏi user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public ResponseEntity<Void> capNhatTrangThai(@PathVariable Long id,
                                                 @RequestParam Boolean trangThaiHoatDong) {
        log.info("📥 Nhận yêu cầu PUT /api/nguoi-dung/{}/trang-thai với trạng thái: {}", id, trangThaiHoatDong);
        nguoiDungService.capNhatTrangThaiHoatDong(id, trangThaiHoatDong);
        log.info("✅ Cập nhật trạng thái thành công cho user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/khoa-tai-khoan")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public ResponseEntity<Void> khoaTaiKhoan(@PathVariable Long id) {
        log.info("📥 Nhận yêu cầu POST /api/nguoi-dung/{}/khoa-tai-khoan", id);
        nguoiDungService.khoaTaiKhoan(id);
        log.info("✅ Khóa tài khoản thành công cho user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/mo-khoa-tai-khoan")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public ResponseEntity<Void> moKhoaTaiKhoan(@PathVariable Long id) {
        log.info("📥 Nhận yêu cầu POST /api/nguoi-dung/{}/mo-khoa-tai-khoan", id);
        nguoiDungService.moKhoaTaiKhoan(id);
        log.info("✅ Mở khóa tài khoản thành công cho user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/doi-mat-khau")
    public ResponseEntity<Void> doiMatKhau(@PathVariable Long id,
                                           @RequestParam String matKhauCu,
                                           @RequestParam String matKhauMoi) {
        log.info("📥 Nhận yêu cầu POST /api/nguoi-dung/{}/doi-mat-khau", id);
        nguoiDungService.doiMatKhau(id, matKhauCu, matKhauMoi);
        log.info("✅ Đổi mật khẩu thành công cho user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/reset-mat-khau")
    @PreAuthorize("hasAuthority('QUAN_LY_NGUOI_DUNG_SUA')")
    public ResponseEntity<Void> resetMatKhau(@PathVariable Long id,
                                             @RequestParam String matKhauMoi) {
        log.info("📥 Nhận yêu cầu POST /api/nguoi-dung/{}/reset-mat-khau", id);
        nguoiDungService.resetMatKhau(id, matKhauMoi);
        log.info("✅ Reset mật khẩu thành công cho user ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/phan-cong")
    @PreAuthorize("hasAuthority('PHAN_CONG_CONG_VIEC')")
    public ResponseEntity<List<NguoiDungDTO>> layNguoiDungCoThePhancong(@RequestParam String tenQuyen) {
        log.info("📥 Nhận yêu cầu GET /api/nguoi-dung/phan-cong với quyền: {}", tenQuyen);
        List<NguoiDungDTO> danhSach = nguoiDungService.layNguoiDungCoThePhancong(tenQuyen);
        log.info("✅ Lấy danh sách thành công");
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/thong-ke/vai-tro")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<List<Object[]>> thongKeTheoVaiTro() {
        log.info("📥 Nhận yêu cầu GET /api/nguoi-dung/thong-ke/vai-tro");
        List<Object[]> thongKe = nguoiDungService.thongKeNguoiDungTheoVaiTro();
        log.info("✅ Thống kê thành công");
        return ResponseEntity.ok(thongKe);
    }
}