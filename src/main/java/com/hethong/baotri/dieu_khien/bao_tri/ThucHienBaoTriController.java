package com.hethong.baotri.dieu_khien.bao_tri;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/thuc-hien-bao-tri")
@RequiredArgsConstructor
public class ThucHienBaoTriController {

    @GetMapping("/cong-viec")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<List<Map<String, Object>>> layCongViecBaoTri(@RequestParam(required = false) Long idNguoiThucHien) {
        // TODO: Implement get maintenance tasks
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/cong-viec/{id}/bat-dau")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> batDauCongViec(@PathVariable Long id) {
        // TODO: Implement start maintenance task
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cong-viec/{id}/hoan-thanh")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> hoanThanhCongViec(@PathVariable Long id,
                                                  @RequestParam String ketQua,
                                                  @RequestParam Integer thoiGianThucTe) {
        // TODO: Implement complete maintenance task
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cong-viec/{id}/tam-dung")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> tamDungCongViec(@PathVariable Long id,
                                                @RequestParam String lyDo) {
        // TODO: Implement pause maintenance task
        return ResponseEntity.ok().build();
    }

    @GetMapping("/lich-su/{idThietBi}")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layLichSuBaoTri(@PathVariable Long idThietBi) {
        // TODO: Implement get maintenance history
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/bao-cao")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> taoBaoCaoBaoTri(@RequestBody Map<String, Object> baoCao) {
        // TODO: Implement create maintenance report
        return ResponseEntity.ok().build();
    }
}