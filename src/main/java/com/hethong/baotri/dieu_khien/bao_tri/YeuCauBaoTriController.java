package com.hethong.baotri.dieu_khien.bao_tri;

import com.hethong.baotri.dich_vu.bao_tri.YeuCauBaoTriService;
import com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/yeu-cau-bao-tri")
@RequiredArgsConstructor
public class YeuCauBaoTriController {

    private final YeuCauBaoTriService yeuCauBaoTriService;

    @PostMapping
    @PreAuthorize("hasAuthority('TAO_YEU_CAU_BAO_TRI')")
    public ResponseEntity<YeuCauBaoTri> taoYeuCauBaoTri(@Valid @RequestBody YeuCauBaoTri yeuCau) {
        YeuCauBaoTri yeuCauMoi = yeuCauBaoTriService.taoYeuCauBaoTri(yeuCau);
        return ResponseEntity.ok(yeuCauMoi);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<Page<YeuCauBaoTri>> layDanhSachYeuCau(Pageable pageable) {
        Page<YeuCauBaoTri> danhSach = yeuCauBaoTriService.layDanhSachYeuCau(pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/trang-thai/{trangThai}")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<Page<YeuCauBaoTri>> layYeuCauTheoTrangThai(@PathVariable String trangThai,
                                                                     Pageable pageable) {
        Page<YeuCauBaoTri> danhSach = yeuCauBaoTriService.layYeuCauTheoTrangThai(trangThai, pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/uu-tien")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<YeuCauBaoTri>> layYeuCauUuTien(@RequestParam String trangThai,
                                                              @RequestParam Integer mucDoUuTien) {
        List<YeuCauBaoTri> danhSach = yeuCauBaoTriService.layYeuCauUuTien(trangThai, mucDoUuTien);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/qua-han")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<YeuCauBaoTri>> layYeuCauQuaHan() {
        List<YeuCauBaoTri> danhSach = yeuCauBaoTriService.layYeuCauQuaHan();
        return ResponseEntity.ok(danhSach);
    }

    @PostMapping("/{id}/duyet")
    @PreAuthorize("hasAuthority('DUYET_YEU_CAU_BAO_TRI')")
    public ResponseEntity<YeuCauBaoTri> duyetYeuCau(@PathVariable Long id,
                                                    @RequestParam Long idNguoiDuyet) {
        YeuCauBaoTri yeuCau = yeuCauBaoTriService.duyetYeuCau(id, idNguoiDuyet);
        return ResponseEntity.ok(yeuCau);
    }

    @PostMapping("/{id}/tu-choi")
    @PreAuthorize("hasAuthority('DUYET_YEU_CAU_BAO_TRI')")
    public ResponseEntity<YeuCauBaoTri> tuChoiYeuCau(@PathVariable Long id,
                                                     @RequestParam Long idNguoiDuyet,
                                                     @RequestParam String lyDo) {
        YeuCauBaoTri yeuCau = yeuCauBaoTriService.tuChoiYeuCau(id, idNguoiDuyet, lyDo);
        return ResponseEntity.ok(yeuCau);
    }

    @PostMapping("/{id}/bat-dau")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<YeuCauBaoTri> batDauThucHien(@PathVariable Long id,
                                                       @RequestParam Long idNguoiThucHien) {
        YeuCauBaoTri yeuCau = yeuCauBaoTriService.batDauThucHien(id, idNguoiThucHien);
        return ResponseEntity.ok(yeuCau);
    }

    @PostMapping("/{id}/hoan-thanh")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<YeuCauBaoTri> hoanThanh(@PathVariable Long id,
                                                  @RequestParam Integer thoiGianThucTe,
                                                  @RequestParam BigDecimal chiPhiThucTe) {
        YeuCauBaoTri yeuCau = yeuCauBaoTriService.hoanThanhYeuCau(id, thoiGianThucTe, chiPhiThucTe);
        return ResponseEntity.ok(yeuCau);
    }

    @GetMapping("/thong-ke/trang-thai")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<List<Object[]>> thongKeTheoTrangThai() {
        List<Object[]> thongKe = yeuCauBaoTriService.thongKeYeuCauTheoTrangThai();
        return ResponseEntity.ok(thongKe);
    }

    @GetMapping("/thong-ke/dem/{trangThai}")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Long> demYeuCauTheoTrangThai(@PathVariable String trangThai) {
        Long soLuong = yeuCauBaoTriService.demYeuCauTheoTrangThai(trangThai);
        return ResponseEntity.ok(soLuong);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<YeuCauBaoTri> layYeuCau(@PathVariable Long id) {
        return yeuCauBaoTriService.timYeuCauTheoId(id)
                .map(yeuCau -> ResponseEntity.ok(yeuCau))
                .orElse(ResponseEntity.notFound().build());
    }
}