package com.hethong.baotri.dto.vat_tu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhuTungDTO {
    private Long idPhuTung;
    private String maPhuTung;
    private String tenPhuTung;
    private String moTa;
    private String chucNang;
    private String viTriLapDat;
    private String thongSoKyThuat;
    private String hangSanXuat;
    private String model;
    private String soPart;
    private Integer thoiGianSuDungBinhQuan;
    private Integer chuKyThayThe;
    private Integer mucDoQuanTrong;
    private Boolean phuTungQuanTrong;
    private Boolean coTheThayThe;
    private Boolean yeuCauKyThuatVien;
    private Integer thoiGianThayTheDuKien;
    private BigDecimal chiPhiThayTheDuKien;
    private LocalDate ngayLapDatCuoi;
    private LocalDate ngayThayTheTiepTheo;
    private Integer soLanThayThe;
    private BigDecimal tongChiPhiThayThe;
    private String ghiChu;
    private Boolean trangThaiHoatDong;
    private Long idVatTu;
    private String tenVatTu;
    private Boolean canThayThe;
    private Boolean quaHanThayThe;
    private String trangThaiPhuTung;
}