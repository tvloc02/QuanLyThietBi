package com.hethong.baotri.dieu_khien.thiet_bi;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phan-cong-thiet-bi")
@RequiredArgsConstructor
public class PhanCongThietBiController {

    @PostMapping
    @PreAuthorize("hasAuthority('PHAN_CONG_THIET_BI')")
    public ResponseEntity<Void> phanCongThietBi(@RequestParam Long idThietBi,
                                                @RequestParam Long idNguoiPhuTrach) {
        // TODO: Implement assign equipment to person
        return ResponseEntity.ok().build();
    }

    @PostMapping("/doi")
    @PreAuthorize("hasAuthority('PHAN_CONG_THIET_BI')")
    public ResponseEntity<Void> phanCongThietBiChoDoi(@RequestParam Long idThietBi,
                                                      @RequestParam Long idDoiBaoTri) {
        // TODO: Implement assign equipment to team
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nguoi-dung/{idNguoiDung}")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layThietBiCuaNguoiDung(@PathVariable Long idNguoiDung) {
        // TODO: Implement get equipment assigned to user
        return ResponseEntity.ok(new ArrayList<>());
    }

    @GetMapping("/doi/{idDoiBaoTri}")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layThietBiCuaDoi(@PathVariable Long idDoiBaoTri) {
        // TODO: Implement get equipment assigned to team
        return ResponseEntity.ok(new ArrayList<>());
    }

    @DeleteMapping("/{idThietBi}/huy-phan-cong")
    @PreAuthorize("hasAuthority('PHAN_CONG_THIET_BI')")
    public ResponseEntity<Void> huyPhanCong(@PathVariable Long idThietBi) {
        // TODO: Implement cancel equipment assignment
        return ResponseEntity.ok().build();
    }

    @GetMapping("/thong-ke")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Map<String, Object>> thongKePhanCong() {
        // TODO: Implement assignment statistics
        return ResponseEntity.ok(Map.of("message", "Thống kê phân công thiết bị"));
    }
}