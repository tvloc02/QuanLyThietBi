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
@Table(name = "doi_bao_tri")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thanhVienDoiSet", "thietBiPhuTrachSet"})
public class DoiBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_doi_bao_tri")
    private Long idDoiBaoTri;

    @NotBlank(message = "Mã đội không được để trống")
    @Size(max = 50, message = "Mã đội không được vượt quá 50 ký tự")
    @Column(name = "ma_doi", unique = true, nullable = false, length = 50)
    private String maDoi;

    @NotBlank(message = "Tên đội không được để trống")
    @Size(max = 200, message = "Tên đội không được vượt quá 200 ký tự")
    @Column(name = "ten_doi", nullable = false, length = 200)
    private String tenDoi;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "chuyen_mon", nullable = false, length = 100)
    private String chuyenMon;

    @Column(name = "khu_vuc_hoat_dong", length = 200)
    private String khuVucHoatDong;

    @Column(name = "ca_lam_viec", nullable = false, length = 50)
    private String caLamViec = CA_HANH_CHINH;

    @Column(name = "so_thanh_vien_toi_da", nullable = false)
    private Integer soThanhVienToiDa = 10;

    @Column(name = "so_thanh_vien_hien_tai", nullable = false)
    private Integer soThanhVienHienTai = 0;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "ngay_thanh_lap", nullable = false)
    private LocalDateTime ngayThanhLap;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "so_cong_viec_hoan_thanh", nullable = false)
    private Integer soCongViecHoanThanh = 0;

    @Column(name = "so_cong_viec_dang_thuc_hien", nullable = false)
    private Integer soCongViecDangThucHien = 0;

    @Column(name = "diem_danh_gia_trung_binh", precision = 3, scale = 2)
    private java.math.BigDecimal diemDanhGiaTrungBinh = java.math.BigDecimal.ZERO;

    @Column(name = "muc_do_ban_ron", nullable = false)
    private Integer mucDoBanRon = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_truong_doi", nullable = false)
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung truongDoi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pho_doi")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung phoDoi;

    @OneToMany(mappedBy = "doiBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ThanhVienDoi> thanhVienDoiSet = new HashSet<>();

    @OneToMany(mappedBy = "doiBaoTriPhuTrach", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<com.hethong.baotri.thuc_the.thiet_bi.ThietBi> thietBiPhuTrachSet = new HashSet<>();

    @OneToMany(mappedBy = "doiBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung> nguoiDungSet = new HashSet<>();

    @OneToMany(mappedBy = "doiBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri> yeuCauBaoTriSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (ngayThanhLap == null) {
            ngayThanhLap = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void themThanhVien(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDung, String chucVu) {
        if (soThanhVienHienTai >= soThanhVienToiDa) {
            throw new IllegalStateException("Đội đã đủ số lượng thành viên");
        }

        ThanhVienDoi thanhVienDoi = new ThanhVienDoi();
        thanhVienDoi.setDoiBaoTri(this);
        thanhVienDoi.setNguoiDung(nguoiDung);
        thanhVienDoi.setChucVu(chucVu);
        thanhVienDoi.setNgayThamGia(LocalDateTime.now());
        thanhVienDoi.setTrangThaiHoatDong(true);

        this.thanhVienDoiSet.add(thanhVienDoi);
        this.soThanhVienHienTai++;
    }

    public void xoaThanhVien(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDung) {
        thanhVienDoiSet.removeIf(tv -> tv.getNguoiDung().equals(nguoiDung));
        if (soThanhVienHienTai > 0) {
            this.soThanhVienHienTai--;
        }
    }

    public void capNhatMucDoBanRon() {
        if (soThanhVienHienTai == 0) {
            mucDoBanRon = 1;
            return;
        }

        int congViecTrungBinh = soCongViecDangThucHien / soThanhVienHienTai;

        if (congViecTrungBinh <= 2) {
            mucDoBanRon = 1;
        } else if (congViecTrungBinh <= 4) {
            mucDoBanRon = 2;
        } else if (congViecTrungBinh <= 6) {
            mucDoBanRon = 3;
        } else if (congViecTrungBinh <= 8) {
            mucDoBanRon = 4;
        } else {
            mucDoBanRon = 5;
        }
    }

    public void tangSoCongViecHoanThanh() {
        this.soCongViecHoanThanh++;
        if (this.soCongViecDangThucHien > 0) {
            this.soCongViecDangThucHien--;
        }
        capNhatMucDoBanRon();
    }

    public void tangSoCongViecDangThucHien() {
        this.soCongViecDangThucHien++;
        capNhatMucDoBanRon();
    }

    public boolean coTheNhanCongViec() {
        return trangThaiHoatDong && soThanhVienHienTai > 0 && mucDoBanRon < 5;
    }

    public String getMucDoBanRonText() {
        return switch (mucDoBanRon) {
            case 1 -> "Rảnh rỗi";
            case 2 -> "Bình thường";
            case 3 -> "Bận";
            case 4 -> "Rất bận";
            case 5 -> "Quá tải";
            default -> "Không xác định";
        };
    }

    public String getCaLamViecText() {
        return switch (caLamViec) {
            case CA_HANH_CHINH -> "Ca hành chính";
            case CA_1 -> "Ca 1 (6:00 - 14:00)";
            case CA_2 -> "Ca 2 (14:00 - 22:00)";
            case CA_3 -> "Ca 3 (22:00 - 6:00)";
            case CA_24_24 -> "24/24";
            default -> "Không xác định";
        };
    }

    public double getTyLeHieuSuat() {
        if (soCongViecHoanThanh + soCongViecDangThucHien == 0) return 0.0;
        return (double) soCongViecHoanThanh / (soCongViecHoanThanh + soCongViecDangThucHien) * 100;
    }

    public int getTongSoCongViec() {
        return soCongViecHoanThanh + soCongViecDangThucHien;
    }

    public boolean laTruongDoi(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDung) {
        return truongDoi != null && truongDoi.equals(nguoiDung);
    }

    public boolean laPhoDoi(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDung) {
        return phoDoi != null && phoDoi.equals(nguoiDung);
    }

    public boolean laThanhVien(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDung) {
        return thanhVienDoiSet.stream()
                .anyMatch(tv -> tv.getNguoiDung().equals(nguoiDung) && tv.getTrangThaiHoatDong());
    }

    public static final String CA_HANH_CHINH = "CA_HANH_CHINH";
    public static final String CA_1 = "CA_1";
    public static final String CA_2 = "CA_2";
    public static final String CA_3 = "CA_3";
    public static final String CA_24_24 = "CA_24_24";

    public static final String CHUYEN_MON_DIEN = "DIEN";
    public static final String CHUYEN_MON_CO_KHI = "CO_KHI";
    public static final String CHUYEN_MON_TU_DONG_HOA = "TU_DONG_HOA";
    public static final String CHUYEN_MON_HANG_THIEN = "HANG_THIEN";
    public static final String CHUYEN_MON_DIEN_TU = "DIEN_TU";
    public static final String CHUYEN_MON_TONG_HOP = "TONG_HOP";
}