package com.hethong.baotri.thuc_the.doi_bao_tri;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "thanh_vien_doi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"doiBaoTri", "nguoiDung"})
public class ThanhVienDoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thanh_vien_doi")
    private Long idThanhVienDoi;

    @Column(name = "chuc_vu", nullable = false, length = 50)
    private String chucVu;

    @Column(name = "ngay_tham_gia", nullable = false)
    private LocalDateTime ngayThamGia;

    @Column(name = "ngay_roi_doi")
    private LocalDateTime ngayRoiDoi;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "muc_luong_theo_gio", precision = 10, scale = 2)
    private java.math.BigDecimal mucLuongTheoGio;

    @Column(name = "so_gio_lam_viec", nullable = false)
    private Integer soGioLamViec = 0;

    @Column(name = "so_cong_viec_hoan_thanh", nullable = false)
    private Integer soCongViecHoanThanh = 0;

    @Column(name = "diem_danh_gia", precision = 3, scale = 2)
    private java.math.BigDecimal diemDanhGia = java.math.BigDecimal.ZERO;

    @Column(name = "ky_nang_chuyen_mon", length = 500)
    private String kyNangChuyenMon;

    @Column(name = "giay_phep_lao_dong", length = 200)
    private String giayPhepLaoDong;

    @Column(name = "ngay_het_han_giay_phep")
    private LocalDateTime ngayHetHanGiayPhep;

    @Column(name = "muc_do_kinh_nghiem", nullable = false)
    private Integer mucDoKinhNghiem = 1;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doi_bao_tri", nullable = false)
    private DoiBaoTri doiBaoTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_dung", nullable = false)
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDung;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (ngayThamGia == null) {
            ngayThamGia = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void roiDoi(String lyDo) {
        this.ngayRoiDoi = LocalDateTime.now();
        this.trangThaiHoatDong = false;
        this.ghiChu = lyDo;
    }

    public void tangSoCongViecHoanThanh() {
        this.soCongViecHoanThanh++;
    }

    public void tangSoGioLamViec(Integer soGio) {
        this.soGioLamViec += soGio;
    }

    public void capNhatDiemDanhGia(java.math.BigDecimal diemMoi) {
        this.diemDanhGia = diemMoi;
    }

    public long getSoNgayThamGia() {
        LocalDateTime ngayKetThuc = ngayRoiDoi != null ? ngayRoiDoi : LocalDateTime.now();
        return java.time.temporal.ChronoUnit.DAYS.between(ngayThamGia, ngayKetThuc);
    }

    public java.math.BigDecimal tinhLuongTheoGio() {
        if (mucLuongTheoGio == null) return java.math.BigDecimal.ZERO;
        return mucLuongTheoGio.multiply(new java.math.BigDecimal(soGioLamViec));
    }

    public String getChucVuText() {
        return switch (chucVu) {
            case CHUC_VU_TRUONG_DOI -> "Trưởng đội";
            case CHUC_VU_PHO_DOI -> "Phó đội";
            case CHUC_VU_THANH_VIEN -> "Thành viên";
            case CHUC_VU_THO_CHINH -> "Thợ chính";
            case CHUC_VU_THO_PHU -> "Thợ phụ";
            case CHUC_VU_HOCVIEC -> "Học việc";
            default -> "Không xác định";
        };
    }

    public String getMucDoKinhNghiemText() {
        return switch (mucDoKinhNghiem) {
            case 1 -> "Mới vào nghề";
            case 2 -> "Có kinh nghiệm";
            case 3 -> "Thành thạo";
            case 4 -> "Chuyên nghiệp";
            case 5 -> "Chuyên gia";
            default -> "Không xác định";
        };
    }

    public boolean giayPhepConHan() {
        return ngayHetHanGiayPhep != null && ngayHetHanGiayPhep.isAfter(LocalDateTime.now());
    }

    public long getSoNgayConHanGiayPhep() {
        if (ngayHetHanGiayPhep == null) return 0;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), ngayHetHanGiayPhep);
    }

    public boolean canCapNhatGiayPhep() {
        return ngayHetHanGiayPhep != null &&
                getSoNgayConHanGiayPhep() <= 30;
    }

    public double getTyLeHieuSuat() {
        if (soGioLamViec == 0) return 0.0;
        return (double) soCongViecHoanThanh / soGioLamViec * 100;
    }

    public boolean coTheNhanCongViec() {
        return trangThaiHoatDong && giayPhepConHan();
    }

    public static final String CHUC_VU_TRUONG_DOI = "TRUONG_DOI";
    public static final String CHUC_VU_PHO_DOI = "PHO_DOI";
    public static final String CHUC_VU_THANH_VIEN = "THANH_VIEN";
    public static final String CHUC_VU_THO_CHINH = "THO_CHINH";
    public static final String CHUC_VU_THO_PHU = "THO_PHU";
    public static final String CHUC_VU_HOCVIEC = "HOCVIEC";

    public static final int KINH_NGHIEM_MOI = 1;
    public static final int KINH_NGHIEM_CO_KINH_NGHIEM = 2;
    public static final int KINH_NGHIEM_THANH_THAO = 3;
    public static final int KINH_NGHIEM_CHUYEN_NGHIEP = 4;
    public static final int KINH_NGHIEM_CHUYEN_GIA = 5;
}