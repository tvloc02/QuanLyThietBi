package com.hethong.baotri.dto.vat_tu;

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
public class VatTuDTO {

    private Long idVatTu;

    @NotBlank(message = "Mã vật tư không được để trống")
    @Size(max = 50, message = "Mã vật tư không được vượt quá 50 ký tự")
    private String maVatTu;

    @NotBlank(message = "Tên vật tư không được để trống")
    @Size(max = 200, message = "Tên vật tư không được vượt quá 200 ký tự")
    private String tenVatTu;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String moTa;

    @Size(max = 100, message = "Thông số kỹ thuật không được vượt quá 100 ký tự")
    private String thongSoKyThuat;

    @NotBlank(message = "Đơn vị tính không được để trống")
    @Size(max = 20, message = "Đơn vị tính không được vượt quá 20 ký tự")
    private String donViTinh;

    private BigDecimal giaNhap;
    private BigDecimal giaXuat;
    private Integer soLuongTonKho;
    private Integer soLuongTonToiThieu;
    private Integer soLuongTonToiDa;
    private Integer mucDoQuanTrong;
    private String loaiVatTu;

    @Size(max = 100, message = "Hãng sản xuất không được vượt quá 100 ký tự")
    private String hangSanXuat;

    @Size(max = 100, message = "Model không được vượt quá 100 ký tự")
    private String model;

    @Size(max = 50, message = "Số part không được vượt quá 50 ký tự")
    private String soPart;

    private LocalDate ngaySanXuat;
    private LocalDate ngayHetHan;
    private Integer thoiGianBaoQuan;
    private String dieuKienBaoQuan;

    private Boolean coTheThayThe;
    private Boolean vatTuQuanTrong;
    private Boolean yeuCauKiemTra;
    private Integer chuKyKiemTra;
    private LocalDate ngayKiemTraCuoi;
    private LocalDate ngayKiemTraTiepTheo;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Boolean trangThaiHoatDong;
    private Integer soLanSuDung;
    private BigDecimal tongGiaTriNhap;
    private BigDecimal tongGiaTriXuat;

    @NotNull(message = "Nhóm vật tư không được để trống")
    private Long idNhomVatTu;
    private String tenNhomVatTu;

    private Long idNguoiTao;
    private String tenNguoiTao;

    private Long idNguoiCapNhat;
    private String tenNguoiCapNhat;

    // Các thông số tính toán
    private Double tyLeQuayVongTonKho;
    private BigDecimal giaTriTonKho;
    private BigDecimal loiNhuan;
    private Boolean thieuHang;
    private Boolean duThua;
    private Boolean hetHan;
    private Boolean canKiemTra;
    private String mucDoQuanTrongText;
    private String trangThaiTonKho;
    private Long soNgayDenHanKiemTra;
    private Long soNgayDenHanSuDung;
    private Boolean khaDung;
    private String thongTinTomTat;
}