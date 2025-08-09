package com.hethong.baotri.dto.thiet_bi;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThietBiDTO {

    private Long idThietBi;

    @NotBlank(message = "Mã thiết bị không được để trống")
    @Size(max = 50, message = "Mã thiết bị không được vượt quá 50 ký tự")
    private String maThietBi;

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(max = 200, message = "Tên thiết bị không được vượt quá 200 ký tự")
    private String tenThietBi;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String moTa;

    @Size(max = 100, message = "Hãng sản xuất không được vượt quá 100 ký tự")
    private String hangSanXuat;

    @Size(max = 100, message = "Model không được vượt quá 100 ký tự")
    private String model;

    @Size(max = 50, message = "Số serial không được vượt quá 50 ký tự")
    private String soSerial;

    private Integer namSanXuat;
    private LocalDate ngayLapDat;
    private LocalDate ngayDuaVaoSuDung;
    private LocalDate ngayBaoHanhHetHan;
    private BigDecimal giaTriBanDau;
    private BigDecimal giaTriHienTai;
    private Integer thoiGianBaoTriDuKien;
    private Integer chuKyBaoTriDinhKy;

    @Size(max = 200, message = "Vị trí lắp đặt không được vượt quá 200 ký tự")
    private String viTriLapDat;

    private BigDecimal congSuatDanhDinh;
    private String donViCongSuat;
    private BigDecimal dienApHoatDong;
    private BigDecimal dongDienHoatDong;
    private BigDecimal nhietDoHoatDongMin;
    private BigDecimal nhietDoHoatDongMax;
    private BigDecimal canNang;
    private String donViCanNang;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Boolean trangThaiHoatDong;
    private LocalDateTime lanBaoTriCuoi;
    private LocalDateTime lanBaoTriTiepTheo;
    private Integer soGioHoatDong;
    private Integer soLanBaoTri;
    private Integer tongThoiGianDungMay;
    private Integer tongThoiGianSuaChua;

    @NotNull(message = "Nhóm thiết bị không được để trống")
    private Long idNhomThietBi;
    private String tenNhomThietBi;

    @NotNull(message = "Trạng thái thiết bị không được để trống")
    private Long idTrangThai;
    private String tenTrangThai;
    private String mauSacTrangThai;

    private Long idDoiBaoTriPhuTrach;
    private String tenDoiBaoTriPhuTrach;

    private Long idNguoiPhuTrach;
    private String tenNguoiPhuTrach;

    // Các thông số tính toán
    private Double mtbf;
    private Double mttr;
    private Double tyLeKhaDung;
    private Double oee;
    private Boolean canBaoTri;
    private Integer tuoiThietBi;
    private Boolean conBaoHanh;
    private Long soNgayDenBaoTriTiepTheo;
}