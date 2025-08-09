package com.hethong.baotri.thuc_the.san_xuat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Hiệu năng thiết bị
@Entity
@Table(name = "hieu_nang_thiet_bi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi"})
public class HieuNangThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hieu_nang")
    private Long idHieuNang;

    @Column(name = "ngay_do", nullable = false)
    private LocalDateTime ngayDo;

    @Column(name = "thoi_gian_hoat_dong", nullable = false)
    private Integer thoiGianHoatDong; // Phút

    @Column(name = "thoi_gian_dung_may", nullable = false)
    private Integer thoiGianDungMay; // Phút

    @Column(name = "thoi_gian_bao_tri", nullable = false)
    private Integer thoiGianBaoTri; // Phút

    @Column(name = "ty_le_kha_dung", precision = 5, scale = 2)
    private BigDecimal tyLeKhaDung;

    @Column(name = "ty_le_hieu_suat", precision = 5, scale = 2)
    private BigDecimal tyLeHieuSuat;

    @Column(name = "ty_le_chat_luong", precision = 5, scale = 2)
    private BigDecimal tyLeChatLuong;

    @Column(name = "oee", precision = 5, scale = 2)
    private BigDecimal oee;

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
        tinhToanChiSo();
    }

    @PreUpdate
    protected void onUpdate() {
        tinhToanChiSo();
    }

    private void tinhToanChiSo() {
        int tongThoiGian = thoiGianHoatDong + thoiGianDungMay + thoiGianBaoTri;

        if (tongThoiGian > 0) {
            // Tính tỷ lệ khả dụng
            this.tyLeKhaDung = BigDecimal.valueOf(thoiGianHoatDong)
                    .divide(BigDecimal.valueOf(tongThoiGian), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        // Tính OEE (giả sử ty_le_hieu_suat và ty_le_chat_luong đã được set)
        if (tyLeKhaDung != null && tyLeHieuSuat != null && tyLeChatLuong != null) {
            this.oee = tyLeKhaDung.multiply(tyLeHieuSuat).multiply(tyLeChatLuong)
                    .divide(BigDecimal.valueOf(10000), 2, BigDecimal.ROUND_HALF_UP);
        }
    }
}