package com.hethong.baotri.dto.bao_tri;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeHoachBaoTriDTO {
    private Long idKeHoach;
    private String maKeHoach;
    private String tenKeHoach;
    private String moTa;
    private String loaiKeHoach;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer chuKyLapLai;
    private String trangThai;
    private Integer tyLeHoanThanh;
    private Long idNguoiTao;
    private String tenNguoiTao;
}