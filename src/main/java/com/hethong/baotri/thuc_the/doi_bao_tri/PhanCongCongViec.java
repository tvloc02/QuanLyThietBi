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

@Entity // THÊM ANNOTATION @Entity
@Table(name = "phan_cong_cong_viec")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"yeuCauBaoTri", "nguoiDuocPhanCong"})
public class PhanCongCongViec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phan_cong")
    private Long idPhanCong;

    @Column(name = "ngay_phan_cong", nullable = false)
    private LocalDateTime ngayPhanCong;

    @Column(name = "ngay_bat_dau_mong_muon")
    private LocalDateTime ngayBatDauMongMuon;

    @Column(name = "ngay_hoan_thanh_mong_muon")
    private LocalDateTime ngayHoanThanhMongMuon;

    @Column(name = "muc_do_uu_tien", nullable = false)
    private Integer mucDoUuTien = 1;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = TRANG_THAI_CHUA_BAT_DAU;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "yeu_cau_dac_biet", length = 500)
    private String yeuCauDacBiet;

    @Column(name = "thoi_gian_du_kien")
    private Integer thoiGianDuKien;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yeu_cau_bao_tri", nullable = false)
    private com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri yeuCauBaoTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_duoc_phan_cong", nullable = false)
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDuocPhanCong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_phan_cong", nullable = false)
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiPhanCong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doi_bao_tri")
    private DoiBaoTri doiBaoTri;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (ngayPhanCong == null) {
            ngayPhanCong = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void batDauThucHien() {
        this.trangThai = TRANG_THAI_DANG_THUC_HIEN;
    }

    public void tamDung(String lyDo) {
        this.trangThai = TRANG_THAI_TAM_DUNG;
        this.ghiChu = lyDo;
    }

    public void tiepTuc() {
        this.trangThai = TRANG_THAI_DANG_THUC_HIEN;
    }

    public void hoanThanh() {
        this.trangThai = TRANG_THAI_HOAN_THANH;
    }

    public void huy(String lyDo) {
        this.trangThai = TRANG_THAI_HUY;
        this.ghiChu = lyDo;
    }

    public long getSoNgayTuKhiPhanCong() {
        return java.time.temporal.ChronoUnit.DAYS.between(ngayPhanCong, LocalDateTime.now());
    }

    public boolean quaHanMongMuon() {
        return ngayHoanThanhMongMuon != null &&
                ngayHoanThanhMongMuon.isBefore(LocalDateTime.now()) &&
                !TRANG_THAI_HOAN_THANH.equals(trangThai);
    }

    public String getMucDoUuTienText() {
        return switch (mucDoUuTien) {
            case 1 -> "Thấp";
            case 2 -> "Trung bình";
            case 3 -> "Cao";
            case 4 -> "Rất cao";
            case 5 -> "Khẩn cấp";
            default -> "Không xác định";
        };
    }

    public String getTrangThaiText() {
        return switch (trangThai) {
            case TRANG_THAI_CHUA_BAT_DAU -> "Chưa bắt đầu";
            case TRANG_THAI_DANG_THUC_HIEN -> "Đang thực hiện";
            case TRANG_THAI_TAM_DUNG -> "Tạm dừng";
            case TRANG_THAI_HOAN_THANH -> "Hoàn thành";
            case TRANG_THAI_HUY -> "Hủy";
            default -> "Không xác định";
        };
    }

    public static final String TRANG_THAI_CHUA_BAT_DAU = "CHUA_BAT_DAU";
    public static final String TRANG_THAI_DANG_THUC_HIEN = "DANG_THUC_HIEN";
    public static final String TRANG_THAI_TAM_DUNG = "TAM_DUNG";
    public static final String TRANG_THAI_HOAN_THANH = "HOAN_THANH";
    public static final String TRANG_THAI_HUY = "HUY";
}