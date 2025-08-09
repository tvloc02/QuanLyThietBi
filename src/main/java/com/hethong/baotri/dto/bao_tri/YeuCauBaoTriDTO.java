package com.hethong.baotri.dto.bao_tri;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YeuCauBaoTriDTO {

    private Long idYeuCau;

    @NotBlank(message = "Mã yêu cầu không được để trống")
    @Size(max = 50, message = "Mã yêu cầu không được vượt quá 50 ký tự")
    private String maYeuCau;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String tieuDe;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    private String moTa;

    @NotBlank(message = "Loại yêu cầu không được để trống")
    private String loaiYeuCau;

    private Integer mucDoUuTien;
    private String trangThai;
    private LocalDateTime ngayYeuCau;
    private LocalDateTime ngayMongMuon;
    private LocalDateTime ngayBatDauThucHien;
    private LocalDateTime ngayHoanThanh;
    private Integer thoiGianDuKien;
    private Integer thoiGianThucTe;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;

    @Size(max = 500, message = "Lý do từ chối không được vượt quá 500 ký tự")
    private String lyDoTuChoi;

    private BigDecimal chiPhiDuKien;
    private BigDecimal chiPhiThucTe;
    private Boolean yeuCauDungMay;
    private Boolean coAnhHuongSanXuat;
    private Boolean canVatTuDacBiet;
    private Boolean canNhanVienChuyenMon;
    private Boolean daThongBaoQuanLy;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;

    @NotNull(message = "Thiết bị không được để trống")
    private Long idThietBi;
    private String maThietBi;
    private String tenThietBi;

    @NotNull(message = "Người yêu cầu không được để trống")
    private Long idNguoiYeuCau;
    private String tenNguoiYeuCau;

    private Long idNguoiDuyet;
    private String tenNguoiDuyet;

    private Long idNguoiThucHien;
    private String tenNguoiThucHien;

    private Long idDoiBaoTri;
    private String tenDoiBaoTri;

    private Long idKeHoach;
    private String tenKeHoach;

    // Các thông số tính toán
    private String mucDoUuTienText;
    private String trangThaiText;
    private String cssClassTrangThai;
    private String cssClassMucDoUuTien;
    private Boolean coTheChinhSua;
    private Boolean coTheHuy;
    private Boolean canDuyet;
    private Long soNgayTuKhiTao;
    private Long soNgayTuKhiDuyet;
    private Boolean quaHan;
}