package com.hethong.baotri.dieu_khien.bao_tri;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/canh-bao-loi")
@RequiredArgsConstructor
public class CanhBaoLoiController {

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_BAO_TRI_XEM')")
    public ResponseEntity<List<Map<String, Object>>> layDanhSachCanhBao() {
        // TODO: Implement get alerts list
        return ResponseEntity.ok(new ArrayList<>());
    }

    @PostMapping("/{id}/xu-ly")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> xuLyCanhBao(@PathVariable Long id,
                                            @RequestParam String cachXuLy) {
        // TODO: Implement handle alert
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/dong")
    @PreAuthorize("hasAuthority('THUC_HIEN_BAO_TRI')")
    public ResponseEntity<Void> dongCanhBao(@PathVariable Long id) {
        // TODO: Implement close alert
        return ResponseEntity.ok().build();
    }
}