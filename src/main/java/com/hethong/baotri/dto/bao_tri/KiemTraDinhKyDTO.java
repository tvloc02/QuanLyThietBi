package com.hethong.baotri.dto.bao_tri;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KiemTraDinhKyDTO {
    private Long idKiemTra;
    private String maKiemTra;
    private String tenKiemTra;
    private String moTa;
    private String loaiKiemTra;
    private Integer chuKyKiemTra;
    private LocalDateTime ngayKiemTra;
    private LocalDateTime ngayKiemTraTiepTheo;
    private String trangThai;
    private String ketQuaKiemTra;
    private String danhGiaTongThe;
    private String kienNghi;
    private Integer thoiGianThucHien;
    private BigDecimal chiPhi;
    private Boolean yeuCauBaoTri;
    private Long idThietBi;
    private String tenThietBi;
    private Long idNguoiKiemTra;
    private String tenNguoiKiemTra;
}