package com.hethong.baotri.dieu_khien.thiet_bi;
/*
import com.hethong.baotri.dich_vu.thiet_bi.ThietBiService;
import com.hethong.baotri.thuc_the.thiet_bi.ThietBi;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/thiet-bi")
@RequiredArgsConstructor
public class ThietBiController {

    private final ThietBiService thietBiService;

    @PostMapping
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_THEM')")
    public ResponseEntity<ThietBi> taoThietBi(@Valid @RequestBody ThietBi thietBi) {
        ThietBi thietBiMoi = thietBiService.taoThietBi(thietBi);
        return ResponseEntity.ok(thietBiMoi);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_SUA')")
    public ResponseEntity<ThietBi> capNhatThietBi(@PathVariable Long id,
                                                  @Valid @RequestBody ThietBi thietBi) {
        ThietBi thietBiCapNhat = thietBiService.capNhatThietBi(id, thietBi);
        return ResponseEntity.ok(thietBiCapNhat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XOA')")
    public ResponseEntity<Void> xoaThietBi(@PathVariable Long id) {
        thietBiService.xoaThietBi(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XEM')")
    public ResponseEntity<ThietBi> layThietBi(@PathVariable Long id) {
        return thietBiService.timThietBiTheoId(id)
                .map(thietBi -> ResponseEntity.ok(thietBi))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XEM')")
    public ResponseEntity<Page<ThietBi>> layDanhSachThietBi(Pageable pageable) {
        Page<ThietBi> danhSach = thietBiService.layDanhSachThietBi(pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/tim-kiem")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XEM')")
    public ResponseEntity<Page<ThietBi>> timKiemThietBi(@RequestParam String tuKhoa,
                                                        Pageable pageable) {
        Page<ThietBi> ketQua = thietBiService.timKiemThietBi(tuKhoa, pageable);
        return ResponseEntity.ok(ketQua);
    }

    @GetMapping("/can-bao-tri")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<ThietBi>> layThietBiCanBaoTri() {
        List<ThietBi> danhSach = thietBiService.layThietBiCanBaoTri();
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/can-bao-tri/khoang-thoi-gian")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<ThietBi>> layThietBiCanBaoTriTrongKhoang(
            @RequestParam LocalDateTime tuNgay,
            @RequestParam LocalDateTime denNgay) {
        List<ThietBi> danhSach = thietBiService.layThietBiCanBaoTriTrongKhoang(tuNgay, denNgay);
        return ResponseEntity.ok(danhSach);
    }

    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_SUA')")
    public ResponseEntity<Void> capNhatTrangThaiThietBi(@PathVariable Long id,
                                                        @RequestParam Long idTrangThaiMoi) {
        thietBiService.capNhatTrangThaiThietBi(id, idTrangThaiMoi);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cap-nhat-sau-bao-tri")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> capNhatSauBaoTri(@PathVariable Long id,
                                                 @RequestParam Integer thoiGianBaoTri) {
        thietBiService.capNhatSauBaoTri(id, thoiGianBaoTri);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/thong-ke/hoat-dong")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Long> demThietBiHoatDong() {
        Long soLuong = thietBiService.demThietBiHoatDong();
        return ResponseEntity.ok(soLuong);
    }

    @GetMapping("/thong-ke/gio-hoat-dong-trung-binh")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Double> tinhTrungBinhGioHoatDong() {
        Double trungBinh = thietBiService.tinhTrungBinhGioHoatDong();
        return ResponseEntity.ok(trungBinh);
    }
}

 */