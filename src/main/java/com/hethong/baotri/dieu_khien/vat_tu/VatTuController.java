package com.hethong.baotri.dieu_khien.vat_tu;

import com.hethong.baotri.dich_vu.vat_tu.VatTuService;
import com.hethong.baotri.thuc_the.vat_tu.VatTu;
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
@RequestMapping("/api/vat-tu")
@RequiredArgsConstructor
public class VatTuController {

    private final VatTuService vatTuService;

    @PostMapping
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_THEM')")
    public ResponseEntity<VatTu> taoVatTu(@Valid @RequestBody VatTu vatTu) {
        VatTu vatTuMoi = vatTuService.taoVatTu(vatTu);
        return ResponseEntity.ok(vatTuMoi);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_SUA')")
    public ResponseEntity<VatTu> capNhatVatTu(@PathVariable Long id,
                                              @Valid @RequestBody VatTu vatTu) {
        VatTu vatTuCapNhat = vatTuService.capNhatVatTu(id, vatTu);
        return ResponseEntity.ok(vatTuCapNhat);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_XEM')")
    public ResponseEntity<Page<VatTu>> layDanhSachVatTu(Pageable pageable) {
        Page<VatTu> danhSach = vatTuService.layDanhSachVatTu(pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/tim-kiem")
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_XEM')")
    public ResponseEntity<Page<VatTu>> timKiemVatTu(@RequestParam String tuKhoa,
                                                    Pageable pageable) {
        Page<VatTu> ketQua = vatTuService.timKiemVatTu(tuKhoa, pageable);
        return ResponseEntity.ok(ketQua);
    }

    @PostMapping("/{id}/nhap-kho")
    @PreAuthorize("hasAuthority('QUAN_LY_KHO_VAT_TU')")
    public ResponseEntity<Void> nhapKho(@PathVariable Long id,
                                        @RequestParam Integer soLuong,
                                        @RequestParam BigDecimal giaNhap,
                                        @RequestParam String maKho) {
        vatTuService.nhapKhoVatTu(id, soLuong, giaNhap, maKho);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/xuat-kho")
    @PreAuthorize("hasAuthority('QUAN_LY_KHO_VAT_TU')")
    public ResponseEntity<Boolean> xuatKho(@PathVariable Long id,
                                           @RequestParam Integer soLuong,
                                           @RequestParam BigDecimal giaXuat,
                                           @RequestParam String maKho) {
        boolean ketQua = vatTuService.xuatKhoVatTu(id, soLuong, giaXuat, maKho);
        return ResponseEntity.ok(ketQua);
    }

    @GetMapping("/thieu-hang")
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_XEM')")
    public ResponseEntity<List<VatTu>> layVatTuThieuHang() {
        List<VatTu> danhSach = vatTuService.layVatTuThieuHang();
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/het-han")
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_XEM')")
    public ResponseEntity<List<VatTu>> layVatTuHetHan() {
        List<VatTu> danhSach = vatTuService.layVatTuHetHan();
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/can-kiem-tra")
    @PreAuthorize("hasAuthority('QUAN_LY_VAT_TU_XEM')")
    public ResponseEntity<List<VatTu>> layVatTuCanKiemTra() {
        List<VatTu> danhSach = vatTuService.layVatTuCanKiemTra();
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/thong-ke/gia-tri-ton-kho")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<BigDecimal> tinhTongGiaTriTonKho() {
        BigDecimal tongGiaTri = vatTuService.tinhTongGiaTriTonKho();
        return ResponseEntity.ok(tongGiaTri);
    }

    @GetMapping("/thong-ke/so-luong-thieu-hang")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Long> demVatTuThieuHang() {
        Long soLuong = vatTuService.demVatTuThieuHang();
        return ResponseEntity.ok(soLuong);
    }
}