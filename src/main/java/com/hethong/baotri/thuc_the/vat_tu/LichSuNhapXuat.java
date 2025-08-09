package com.hethong.baotri.thuc_the.vat_tu;

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

/**
 * Thực thể lịch sử nhập xuất vật tư
 */
@Entity
@Table(name = "lich_su_nhap_xuat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"vatTu", "khoVatTu"})
public class LichSuNhapXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich_su")
    private Long idLichSu;

    @Column(name = "loai_giao_dich", nullable = false, length = 20)
    private String loaiGiaoDich; // NHAP, XUAT

    @Column(name = "so_luong", nullable = false)
    private Integer soLuong;

    @Column(name = "don_gia", precision = 15, scale = 2)
    private BigDecimal donGia;

    @Column(name = "thanh_tien", precision = 15, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "so_luong_ton_truoc", nullable = false)
    private Integer soLuongTonTruoc;

    @Column(name = "so_luong_ton_sau", nullable = false)
    private Integer soLuongTonSau;

    @Column(name = "ngay_giao_dich", nullable = false)
    private LocalDateTime ngayGiaoDich;

    @Size(max = 500)
    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Size(max = 500)
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "so_chung_tu", length = 50)
    private String soChungTu;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vat_tu", nullable = false)
    @NotNull(message = "Vật tư không được để trống")
    private VatTu vatTu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kho_vat_tu", nullable = false)
    @NotNull(message = "Kho vật tư không được để trống")
    private KhoVatTu khoVatTu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_thuc_hien")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiThucHien;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (ngayGiaoDich == null) {
            ngayGiaoDich = LocalDateTime.now();
        }
        if (thanhTien == null && donGia != null) {
            thanhTien = donGia.multiply(new BigDecimal(soLuong));
        }
    }

    public static final String LOAI_NHAP = "NHAP";
    public static final String LOAI_XUAT = "XUAT";
    public static final String LOAI_DIEU_CHINH = "DIEU_CHINH";
    public static final String LOAI_KIEM_KE = "KIEM_KE";
}