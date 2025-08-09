package com.hethong.baotri.dto.thiet_bi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanCongThietBiDTO {
    private Long idPhanCong;
    private Long idThietBi;
    private String maThietBi;
    private String tenThietBi;
    private Long idNguoiPhuTrach;
    private String tenNguoiPhuTrach;
    private Long idDoiBaoTri;
    private String tenDoiBaoTri;
    private LocalDateTime ngayPhanCong;
    private String trangThai;
    private String ghiChu;
}