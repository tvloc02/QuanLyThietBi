package com.hethong.baotri.dto.thiet_bi;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrangThaiThietBiDTO {
    private Long idTrangThai;
    private String maTrangThai;
    private String tenTrangThai;
    private String moTa;
    private String mauSac;
    private String bieuTuong;
    private Integer mucDoUuTien;
    private Boolean choPhepHoatDong;
    private Boolean yeuCauBaoTri;
    private Boolean tuDongTaoCanhBao;
    private Boolean trangThaiHoatDong;
    private Integer thuTuHienThi;
    private Integer soLuongThietBi;
}