package com.hethong.baotri.dieu_khien.nguoi_dung;

import com.hethong.baotri.dich_vu.nguoi_dung.VaiTroService;
import com.hethong.baotri.dto.nguoi_dung.VaiTroDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/vai-tro")
@RequiredArgsConstructor
public class VaiTroController {

    private final VaiTroService vaiTroService;

    @PostMapping
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<VaiTroDTO> taoVaiTro(@Valid @RequestBody VaiTroDTO vaiTroDTO) {
        VaiTroDTO vaiTroMoi = vaiTroService.taoVaiTro(vaiTroDTO);
        return ResponseEntity.ok(vaiTroMoi);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<VaiTroDTO> capNhatVaiTro(@PathVariable Long id,
                                                   @Valid @RequestBody VaiTroDTO vaiTroDTO) {
        VaiTroDTO vaiTroCapNhat = vaiTroService.capNhatVaiTro(id, vaiTroDTO);
        return ResponseEntity.ok(vaiTroCapNhat);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Void> xoaVaiTro(@PathVariable Long id) {
        vaiTroService.xoaVaiTro(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<VaiTroDTO> layVaiTro(@PathVariable Long id) {
        VaiTroDTO vaiTro = vaiTroService.timVaiTroTheoId(id);
        return ResponseEntity.ok(vaiTro);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Page<VaiTroDTO>> layDanhSachVaiTro(Pageable pageable) {
        Page<VaiTroDTO> danhSach = vaiTroService.layDanhSachVaiTro(pageable);
        return ResponseEntity.ok(danhSach);
    }

    @GetMapping("/tim-kiem")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Page<VaiTroDTO>> timKiemVaiTro(@RequestParam String tuKhoa,
                                                         Pageable pageable) {
        Page<VaiTroDTO> ketQua = vaiTroService.timKiemVaiTro(tuKhoa, pageable);
        return ResponseEntity.ok(ketQua);
    }

    @PostMapping("/{id}/them-quyen")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Void> themQuyen(@PathVariable Long id, @RequestParam Long idQuyen) {
        vaiTroService.themQuyenVaoVaiTro(id, idQuyen);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/xoa-quyen")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Void> xoaQuyen(@PathVariable Long id, @RequestParam Long idQuyen) {
        vaiTroService.xoaQuyenKhoiVaiTro(id, idQuyen);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/theo-quyen/{tenQuyen}")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<List<VaiTroDTO>> layVaiTroTheoQuyen(@PathVariable String tenQuyen) {
        List<VaiTroDTO> danhSach = vaiTroService.layVaiTroTheoQuyen(tenQuyen);
        return ResponseEntity.ok(danhSach);
    }

    @PutMapping("/{id}/trang-thai")
    @PreAuthorize("hasAuthority('QUAN_LY_VAI_TRO_QUYEN')")
    public ResponseEntity<Void> capNhatTrangThai(@PathVariable Long id,
                                                 @RequestParam Boolean trangThaiHoatDong) {
        vaiTroService.capNhatTrangThaiHoatDong(id, trangThaiHoatDong);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/thong-ke/hoat-dong")
    @PreAuthorize("hasAuthority('XEM_BAO_CAO_TONG_HOP')")
    public ResponseEntity<Long> demVaiTroHoatDong() {
        Long soLuong = vaiTroService.demVaiTroHoatDong();
        return ResponseEntity.ok(soLuong);
    }
}