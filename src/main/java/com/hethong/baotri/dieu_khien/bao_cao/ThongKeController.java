package com.hethong.baotri.dieu_khien.bao_cao;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
public class ThongKeController {

    @GetMapping("/bao-tri")
    @PreAuthorize("hasAuthority('XEM_THONG_KE_BAO_TRI')")
    public ResponseEntity<Map<String, Object>> thongKeBaoTri(@RequestParam(required = false) LocalDateTime tuNgay,
                                                             @RequestParam(required = false) LocalDateTime denNgay) {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongYeuCau", 150);
        thongKe.put("daHoanThanh", 120);
        thongKe.put("dangThucHien", 20);
        thongKe.put("choDuyet", 10);
        return ResponseEntity.ok(thongKe);
    }

    @GetMapping("/thiet-bi")
    @PreAuthorize("hasAuthority('QUAN_LY_THIET_BI_XEM')")
    public ResponseEntity<Map<String, Object>> thongKeThietBi() {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("tongThietBi", 250);
        thongKe.put("hoatDong", 200);
        thongKe.put("baoTri", 30);
        thongKe.put("hong", 20);
        return ResponseEntity.ok(thongKe);
    }

    @GetMapping("/hieu-suat")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Map<String, Object>> thongKeHieuSuat(@RequestParam(required = false) LocalDateTime tuNgay,
                                                               @RequestParam(required = false) LocalDateTime denNgay) {
        Map<String, Object> thongKe = new HashMap<>();
        thongKe.put("oeetrungBinh", 75.5);
        thongKe.put("mtbfTrungBinh", 320.8);
        thongKe.put("mttrTrungBinh", 4.2);
        return ResponseEntity.ok(thongKe);
    }
}

