package com.hethong.baotri.dieu_khien.bao_tri;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/kiem-tra-dinh-ky")
@RequiredArgsConstructor
public class KiemTraDinhKyController {

    @PostMapping
    @PreAuthorize("hasAuthority('KIEM_TRA_DINH_KY')")
    public ResponseEntity<Map<String, Object>> taoKiemTra(@Valid @RequestBody Map<String, Object> kiemTra) {
        // TODO: Implement create periodic inspection
        return ResponseEntity.ok(kiemTra);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKiemTra(Pageable pageable) {
        // TODO: Implement get inspections list
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/{id}/bat-dau")
    @PreAuthorize("hasAuthority('KIEM_TRA_DINH_KY')")
    public ResponseEntity<Void> batDauKiemTra(@PathVariable Long id) {
        // TODO: Implement start inspection
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/hoan-thanh")
    @PreAuthorize("hasAuthority('KIEM_TRA_DINH_KY')")
    public ResponseEntity<Void> hoanThanhKiemTra(@PathVariable Long id,
                                                 @RequestParam String ketQua,
                                                 @RequestParam String danhGia) {
        // TODO: Implement complete inspection
        return ResponseEntity.ok().build();
    }

    @GetMapping("/can-kiem-tra")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layThietBiCanKiemTra() {
        // TODO: Implement get devices needing inspection
        return ResponseEntity.ok(new ArrayList<>());
    }
}