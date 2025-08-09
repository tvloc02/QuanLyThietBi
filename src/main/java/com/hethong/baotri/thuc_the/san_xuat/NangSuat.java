package com.hethong.baotri.thuc_the.san_xuat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Năng suất thiết bị
@Entity
@Table(name = "nang_suat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi"})
public class NangSuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nang_suat")
    private Long idNangSuat;

    @Column(name = "ngay_do", nullable = false)
    private LocalDateTime ngayDo;

    @Column(name = "nang_suat_thiet_ke", precision = 15, scale = 6)
    private BigDecimal nangSuatThietKe;

    @Column(name = "nang_suat_thuc_te", precision = 15, scale = 6)
    private BigDecimal nangSuatThucTe;

    @Column(name = "don_vi", length = 50)
    private String donVi;

    @Column(name = "hieu_suat", precision = 5, scale = 2)
    private BigDecimal hieuSuat;

    @Column(name = "dieu_kien_hoat_dong", length = 200)
    private String dieuKienHoatDong;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi", nullable = false)
    @NotNull(message = "Thiết bị không được để trống")
    private com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_do")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDo;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (ngayDo == null) {
            ngayDo = LocalDateTime.now();
        }
        tinhHieuSuat();
    }

    @PreUpdate
    protected void onUpdate() {
        tinhHieuSuat();
    }

    private void tinhHieuSuat() {
        if (nangSuatThietKe != null && nangSuatThietKe.compareTo(BigDecimal.ZERO) > 0) {
            this.hieuSuat = nangSuatThucTe
                    .divide(nangSuatThietKe, 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }
    }
}

