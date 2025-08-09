package com.hethong.baotri.thuc_the.san_xuat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Thông tin sản xuất
@Entity
@Table(name = "thong_tin_san_xuat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi"})
public class ThongTinSanXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thong_tin")
    private Long idThongTin;

    @Column(name = "ngay_san_xuat", nullable = false)
    private LocalDateTime ngaySanXuat;

    @Column(name = "ca_san_xuat", nullable = false, length = 20)
    private String caSanXuat;

    @Column(name = "san_luong_ke_hoach", nullable = false)
    private Integer sanLuongKeHoach;

    @Column(name = "san_luong_thuc_te", nullable = false)
    private Integer sanLuongThucTe;

    @Column(name = "san_pham_dat", nullable = false)
    private Integer sanPhamDat;

    @Column(name = "san_pham_loi", nullable = false)
    private Integer sanPhamLoi;

    @Column(name = "thoi_gian_hoat_dong", nullable = false)
    private Integer thoiGianHoatDong; // Phút

    @Column(name = "thoi_gian_dung_may", nullable = false)
    private Integer thoiGianDungMay; // Phút

    @Column(name = "ty_le_dat", precision = 5, scale = 2)
    private BigDecimal tyLeDat;

    @Column(name = "hieu_suat", precision = 5, scale = 2)
    private BigDecimal hieuSuat;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi", nullable = false)
    @NotNull(message = "Thiết bị không được để trống")
    private com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_nhap")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiNhap;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (ngaySanXuat == null) {
            ngaySanXuat = LocalDateTime.now();
        }
        tinhToanChiSo();
    }

    @PreUpdate
    protected void onUpdate() {
        tinhToanChiSo();
    }

    private void tinhToanChiSo() {
        // Tính tỷ lệ đạt
        if (sanLuongThucTe > 0) {
            this.tyLeDat = BigDecimal.valueOf(sanPhamDat)
                    .divide(BigDecimal.valueOf(sanLuongThucTe), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // Tính hiệu suất
        if (sanLuongKeHoach > 0) {
            this.hieuSuat = BigDecimal.valueOf(sanLuongThucTe)
                    .divide(BigDecimal.valueOf(sanLuongKeHoach), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }

    public static final String CA_1 = "CA_1";
    public static final String CA_2 = "CA_2";
    public static final String CA_3 = "CA_3";
    public static final String CA_HANH_CHINH = "CA_HANH_CHINH";
}

