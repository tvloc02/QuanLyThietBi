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
@RequestMapping("/api/ke-hoach-bao-tri")
@RequiredArgsConstructor
public class KeHoachBaoTriController {

    @PostMapping
    @PreAuthorize("hasAuthority('TAO_KE_HOACH_BAO_TRI')")
    public ResponseEntity<Map<String, Object>> taoKeHoach(@Valid @RequestBody Map<String, Object> keHoach) {
        // TODO: Implement create maintenance plan
        return ResponseEntity.ok(keHoach);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachKeHoach(Pageable pageable) {
        // TODO: Implement get maintenance plans list
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<Map<String, Object>> layKeHoach(@PathVariable Long id) {
        // TODO: Implement get maintenance plan by id
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TAO_KE_HOACH_BAO_TRI')")
    public ResponseEntity<Map<String, Object>> capNhatKeHoach(@PathVariable Long id,
                                                              @Valid @RequestBody Map<String, Object> keHoach) {
        // TODO: Implement update maintenance plan
        return ResponseEntity.ok(keHoach);
    }

    @PostMapping("/{id}/kich-hoat")
    @PreAuthorize("hasAuthority('TAO_KE_HOACH_BAO_TRI')")
    public ResponseEntity<Void> kichHoatKeHoach(@PathVariable Long id) {
        // TODO: Implement activate maintenance plan
        return ResponseEntity.ok().build();
    }
}