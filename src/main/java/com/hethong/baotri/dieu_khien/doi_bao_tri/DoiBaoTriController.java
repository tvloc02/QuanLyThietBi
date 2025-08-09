package com.hethong.baotri.dieu_khien.doi_bao_tri;

import com.hethong.baotri.dich_vu.doi_bao_tri.DoiBaoTriService;
import com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/doi-bao-tri")
@RequiredArgsConstructor
public class DoiBaoTriController {

    private final DoiBaoTriService doiBaoTriService;

    @PostMapping
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_THEM')")
    public ResponseEntity<DoiBaoTri> taoDoiBaoTri(@Valid @RequestBody DoiBaoTri doiBaoTri) {
        DoiBaoTri doiMoi = doiBaoTriService.taoDoiBaoTri(doiBaoTri);
        return ResponseEntity.ok(doiMoi);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_SUA')")
    public ResponseEntity<DoiBaoTri> capNhatDoiBaoTri(@PathVariable Long id,
                                                      @Valid @RequestBody DoiBaoTri doiBaoTri) {
        DoiBaoTri doiCapNhat = doiBaoTriService.capNhatDoiBaoTri(id, doiBaoTri);
        return ResponseEntity.ok(doiCapNhat);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_XEM')")
    public ResponseEntity<Page<DoiBaoTri>> layDanhSachDoiBaoTri(Pageable pageable) {
        Page<DoiBaoTri> danhSach = doiBaoTriService.layDanhSachDoiBaoTri(pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/hoat-dong")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_XEM')")
    public ResponseEntity<Page<DoiBaoTri>> layDoiBaoTriHoatDong(Pageable pageable) {
        Page<DoiBaoTri> danhSach = doiBaoTriService.layDoiBaoTriTheoTrangThai(true, pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/chuyen-mon/{chuyenMon}")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_XEM')")
    public ResponseEntity<List<DoiBaoTri>> layDoiBaoTriTheoChuyenMon(@PathVariable String chuyenMon) {
        List<DoiBaoTri> danhSach = doiBaoTriService.layDoiBaoTriTheoChuyenMon(chuyenMon);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/co-the-nhan-cong-viec")
    @PreAuthorize("hasAuthority('PHAN_CONG_CONG_VIEC')")
    public ResponseEntity<List<DoiBaoTri>> layDoiBaoTriCoTheNhanCongViec() {
        List<DoiBaoTri> danhSach = doiBaoTriService.layDoiBaoTriCoTheNhanCongViec();
        return ResponseEntity.ok(danhSach);
    }

    @PostMapping("/{id}/them-thanh-vien")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_SUA')")
    public ResponseEntity<Void> themThanhVien(@PathVariable Long id,
                                              @RequestParam Long idNguoiDung,
                                              @RequestParam String chucVu) {
        doiBaoTriService.themThanhVienVaoDoi(id, idNguoiDung, chucVu);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/xoa-thanh-vien")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_SUA')")
    public ResponseEntity<Void> xoaThanhVien(@PathVariable Long id,
                                             @RequestParam Long idNguoiDung) {
        doiBaoTriService.xoaThanhVienKhoiDoi(id, idNguoiDung);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_XEM')")
    public ResponseEntity<DoiBaoTri> layDoiBaoTri(@PathVariable Long id) {
        return doiBaoTriService.timDoiBaoTriTheoId(id)
                .map(doi -> ResponseEntity.ok(doi))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/ma/{maDoi}")
    @PreAuthorize("hasAuthority('QUAN_LY_DOI_BAO_TRI_XEM')")
    public ResponseEntity<DoiBaoTri> layDoiBaoTriTheoMa(@PathVariable String maDoi) {
        return doiBaoTriService.timDoiBaoTriTheoMa(maDoi)
                .map(doi -> ResponseEntity.ok(doi))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/thong-ke/tong-so")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Long> demTongSoDoiBaoTri() {
        Long soLuong = doiBaoTriService.demTongSoDoiBaoTri();
        return ResponseEntity.ok(soLuong);
    }

    @GetMapping("/thong-ke/hoat-dong")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Long> demDoiBaoTriHoatDong() {
        Long soLuong = doiBaoTriService.demDoiBaoTriHoatDong();
        return ResponseEntity.ok(soLuong);
    }
}