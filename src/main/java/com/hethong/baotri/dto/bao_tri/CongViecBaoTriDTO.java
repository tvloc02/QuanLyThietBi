package com.hethong.baotri.dto.bao_tri;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CongViecBaoTriDTO {
    private Long idCongViec;
    private String tenCongViec;
    private String moTa;
    private Integer thuTuThucHien;
    private Integer thoiGianDuKien;
    private Integer thoiGianThucTe;
    private String trangThai;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayHoanThanh;
    private String ketQua;
    private Long idYeuCauBaoTri;
    private Long idNguoiThucHien;
    private String tenNguoiThucHien;
}