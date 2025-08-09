package com.hethong.baotri.dieu_khien.bao_cao;
/*
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
public class BaoCaoController {

    @GetMapping("/mtbf")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_MTBF')")
    public ResponseEntity<Map<String, Object>> layBaoCaoMTBF(@RequestParam(required = false) Long idThietBi,
                                                             @RequestParam(required = false) LocalDateTime tuNgay,
                                                             @RequestParam(required = false) LocalDateTime denNgay) {
        Map<String, Object> baoCao = new HashMap<>();
        baoCao.put("loaiBaoCao", "MTBF");
        baoCao.put("tuNgay", tuNgay);
        baoCao.put("denNgay", denNgay);
        baoCao.put("idThietBi", idThietBi);
        // TODO: Implement MTBF report logic
        return ResponseEntity.ok(baoCao);
    }

    @GetMapping("/oee")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_OEE')")
    public ResponseEntity<Map<String, Object>> layBaoCaoOEE(@RequestParam(required = false) Long idThietBi,
                                                            @RequestParam(required = false) LocalDateTime tuNgay,
                                                            @RequestParam(required = false) LocalDateTime denNgay) {
        Map<String, Object> baoCao = new HashMap<>();
        baoCao.put("loaiBaoCao", "OEE");
        baoCao.put("tuNgay", tuNgay);
        baoCao.put("denNgay", denNgay);
        baoCao.put("idThietBi", idThietBi);
        // TODO: Implement OEE report logic
        return ResponseEntity.ok(baoCao);
    }

    @GetMapping("/tong-hop")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Map<String, Object>> layBaoCaoTongHop(@RequestParam(required = false) LocalDateTime tuNgay,
                                                                @RequestParam(required = false) LocalDateTime denNgay) {
        Map<String, Object> baoCao = new HashMap<>();
        baoCao.put("loaiBaoCao", "TONG_HOP");
        baoCao.put("tuNgay", tuNgay);
        baoCao.put("denNgay", denNgay);
        // TODO: Implement comprehensive report logic
        return ResponseEntity.ok(baoCao);
    }
}

 */