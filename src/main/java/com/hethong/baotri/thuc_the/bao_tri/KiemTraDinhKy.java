package com.hethong.baotri.thuc_the.bao_tri;

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
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "kiem_tra_dinh_ky")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi", "nguoiKiemTra"})
public class KiemTraDinhKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kiem_tra")
    private Long idKiemTra;

    @NotBlank(message = "Mã kiểm tra không được để trống")
    @Size(max = 50, message = "Mã kiểm tra không được vượt quá 50 ký tự")
    @Column(name = "ma_kiem_tra", unique = true, nullable = false, length = 50)
    private String maKiemTra;

    @NotBlank(message = "Tên kiểm tra không được để trống")
    @Size(max = 200, message = "Tên kiểm tra không được vượt quá 200 ký tự")
    @Column(name = "ten_kiem_tra", nullable = false, length = 200)
    private String tenKiemTra;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "loai_kiem_tra", nullable = false, length = 50)
    private String loaiKiemTra;

    @Column(name = "chu_ky_kiem_tra", nullable = false)
    private Integer chuKyKiemTra; // Số ngày

    @Column(name = "ngay_kiem_tra", nullable = false)
    private LocalDateTime ngayKiemTra;

    @Column(name = "ngay_kiem_tra_tiep_theo")
    private LocalDateTime ngayKiemTraTiepTheo;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = TRANG_THAI_CHUA_THUC_HIEN;

    @Column(name = "ket_qua_kiem_tra", length = 1000)
    private String ketQuaKiemTra;

    @Column(name = "danh_gia_tong_the", length = 500)
    private String danhGiaTongThe;

    @Column(name = "kien_nghi", length = 1000)
    private String kienNghi;

    @Column(name = "thoi_gian_thuc_hien")
    private Integer thoiGianThucHien; // Phút

    @Column(name = "chi_phi", precision = 15, scale = 2)
    private BigDecimal chiPhi;

    @Column(name = "yeu_cau_bao_tri", nullable = false)
    private Boolean yeuCauBaoTri = false;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi", nullable = false)
    @NotNull(message = "Thiết bị không được để trống")
    private com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_kiem_tra", nullable = false)
    @NotNull(message = "Người kiểm tra không được để trống")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiKiemTra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_phe_duyet")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiPheDuyet;

    @OneToMany(mappedBy = "kiemTraDinhKy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ChiTietKiemTra> chiTietKiemTraSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (ngayKiemTra == null) {
            ngayKiemTra = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void batDauKiemTra() {
        this.trangThai = TRANG_THAI_DANG_THUC_HIEN;
        this.ngayKiemTra = LocalDateTime.now();
    }

    public void hoanThanhKiemTra(String ketQua, String danhGia) {
        this.ketQuaKiemTra = ketQua;
        this.danhGiaTongThe = danhGia;
        this.trangThai = TRANG_THAI_HOAN_THANH;

        // Tính toán ngày kiểm tra tiếp theo
        this.ngayKiemTraTiepTheo = LocalDateTime.now().plusDays(chuKyKiemTra);
    }

    public static final String TRANG_THAI_CHUA_THUC_HIEN = "CHUA_THUC_HIEN";
    public static final String TRANG_THAI_DANG_THUC_HIEN = "DANG_THUC_HIEN";
    public static final String TRANG_THAI_HOAN_THANH = "HOAN_THANH";
    public static final String TRANG_THAI_HUY = "HUY";

    public static final String LOAI_KIEM_TRA_AN_TOAN = "KIEM_TRA_AN_TOAN";
    public static final String LOAI_KIEM_TRA_KY_THUAT = "KIEM_TRA_KY_THUAT";
    public static final String LOAI_KIEM_TRA_CHAT_LUONG = "KIEM_TRA_CHAT_LUONG";
    public static final String LOAI_KIEM_TRA_HIEU_SUAT = "KIEM_TRA_HIEU_SUAT";
}

// Entity Chi tiết kiểm tra
@Entity
@Table(name = "chi_tiet_kiem_tra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"kiemTraDinhKy"})
class ChiTietKiemTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chi_tiet")
    private Long idChiTiet;

    @Column(name = "ten_hang_muc", nullable = false, length = 200)
    private String tenHangMuc;

    @Column(name = "tieu_chi", length = 500)
    private String tieuChi;

    @Column(name = "ket_qua", length = 500)
    private String ketQua;

    @Column(name = "dat_chuan", nullable = false)
    private Boolean datChuan = false;

    @Column(name = "diem_so")
    private Integer diemSo;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kiem_tra", nullable = false)
    private KiemTraDinhKy kiemTraDinhKy;
}