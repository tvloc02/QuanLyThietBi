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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "phu_tung")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"vatTu", "thietBiApDungSet"})
public class PhuTung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phu_tung")
    private Long idPhuTung;

    @NotBlank(message = "Mã phụ tùng không được để trống")
    @Size(max = 50, message = "Mã phụ tùng không được vượt quá 50 ký tự")
    @Column(name = "ma_phu_tung", unique = true, nullable = false, length = 50)
    private String maPhuTung;

    @NotBlank(message = "Tên phụ tùng không được để trống")
    @Size(max = 200, message = "Tên phụ tùng không được vượt quá 200 ký tự")
    @Column(name = "ten_phu_tung", nullable = false, length = 200)
    private String tenPhuTung;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Size(max = 100, message = "Chức năng không được vượt quá 100 ký tự")
    @Column(name = "chuc_nang", length = 100)
    private String chucNang;

    @Size(max = 100, message = "Vị trí lắp đặt không được vượt quá 100 ký tự")
    @Column(name = "vi_tri_lap_dat", length = 100)
    private String viTriLapDat;

    @Size(max = 100, message = "Thông số kỹ thuật không được vượt quá 100 ký tự")
    @Column(name = "thong_so_ky_thuat", length = 100)
    private String thongSoKyThuat;

    @Size(max = 100, message = "Hãng sản xuất không được vượt quá 100 ký tự")
    @Column(name = "hang_san_xuat", length = 100)
    private String hangSanXuat;

    @Size(max = 100, message = "Model không được vượt quá 100 ký tự")
    @Column(name = "model", length = 100)
    private String model;

    @Size(max = 50, message = "Số part không được vượt quá 50 ký tự")
    @Column(name = "so_part", length = 50)
    private String soPart;

    @Column(name = "thoi_gian_su_dung_binh_quan")
    private Integer thoiGianSuDungBinhQuan; // Tháng

    @Column(name = "chu_ky_thay_the")
    private Integer chuKyThayThe; // Tháng

    @Column(name = "muc_do_quan_trong", nullable = false)
    private Integer mucDoQuanTrong = 1; // 1-5

    @Column(name = "phu_tung_quan_trong", nullable = false)
    private Boolean phuTungQuanTrong = false;

    @Column(name = "co_the_thay_the", nullable = false)
    private Boolean coTheThayThe = false;

    @Column(name = "yeu_cau_ky_thuat_vien", nullable = false)
    private Boolean yeuCauKyThuatVien = false;

    @Column(name = "thoi_gian_thay_the_du_kien")
    private Integer thoiGianThayTheDuKien; // Phút

    @Column(name = "chi_phi_thay_the_du_kien", precision = 15, scale = 2)
    private BigDecimal chiPhiThayTheDuKien;

    @Column(name = "ngay_lap_dat_cuoi")
    private LocalDate ngayLapDatCuoi;

    @Column(name = "ngay_thay_the_tiep_theo")
    private LocalDate ngayThayTheTiepTheo;

    @Column(name = "so_lan_thay_the", nullable = false)
    private Integer soLanThayThe = 0;

    @Column(name = "tong_chi_phi_thay_the", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongChiPhiThayThe = BigDecimal.ZERO;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    // Quan hệ với VatTu (One-to-One)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vat_tu", nullable = false)
    @NotNull(message = "Vật tư không được để trống")
    private VatTu vatTu;

    // Quan hệ với ThietBi (Many-to-Many)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "phu_tung_thiet_bi",
            joinColumns = @JoinColumn(name = "id_phu_tung"),
            inverseJoinColumns = @JoinColumn(name = "id_thiet_bi")
    )
    private Set<com.hethong.baotri.thuc_the.thiet_bi.ThietBi> thietBiApDungSet = new HashSet<>();

    // Quan hệ tự tham chiếu - phụ tùng thay thế
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "phu_tung_thay_the",
            joinColumns = @JoinColumn(name = "id_phu_tung_chinh"),
            inverseJoinColumns = @JoinColumn(name = "id_phu_tung_thay_the")
    )
    private Set<PhuTung> phuTungThayTheSet = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();

        // Tính toán ngày thay thế tiếp theo
        if (ngayLapDatCuoi != null && chuKyThayThe != null) {
            ngayThayTheTiepTheo = ngayLapDatCuoi.plusMonths(chuKyThayThe);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    /**
     * Thêm thiết bị áp dụng
     */
    public void themThietBiApDung(com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi) {
        this.thietBiApDungSet.add(thietBi);
    }

    /**
     * Xóa thiết bị áp dụng
     */
    public void xoaThietBiApDung(com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi) {
        this.thietBiApDungSet.remove(thietBi);
    }

    /**
     * Thêm phụ tùng thay thế
     */
    public void themPhuTungThayThe(PhuTung phuTungThayThe) {
        this.phuTungThayTheSet.add(phuTungThayThe);
        phuTungThayThe.getPhuTungThayTheSet().add(this);
    }

    /**
     * Xóa phụ tùng thay thế
     */
    public void xoaPhuTungThayThe(PhuTung phuTungThayThe) {
        this.phuTungThayTheSet.remove(phuTungThayThe);
        phuTungThayThe.getPhuTungThayTheSet().remove(this);
    }

    /**
     * Thực hiện thay thế phụ tùng
     */
    public void thucHienThayThe(BigDecimal chiPhi, String ghiChu) {
        this.soLanThayThe++;
        this.tongChiPhiThayThe = this.tongChiPhiThayThe.add(chiPhi);
        this.ngayLapDatCuoi = LocalDate.now();

        if (chuKyThayThe != null) {
            this.ngayThayTheTiepTheo = LocalDate.now().plusMonths(chuKyThayThe);
        }

        if (ghiChu != null && !ghiChu.trim().isEmpty()) {
            this.ghiChu = ghiChu;
        }
    }

    /**
     * Kiểm tra phụ tùng có cần thay thế không
     */
    public boolean canThayThe() {
        return ngayThayTheTiepTheo != null &&
                ngayThayTheTiepTheo.isBefore(LocalDate.now().plusDays(30));
    }

    /**
     * Kiểm tra phụ tùng có quá hạn thay thế không
     */
    public boolean quaHanThayThe() {
        return ngayThayTheTiepTheo != null &&
                ngayThayTheTiepTheo.isBefore(LocalDate.now());
    }

    /**
     * Lấy số ngày đến hạn thay thế
     */
    public long getSoNgayDenHanThayThe() {
        if (ngayThayTheTiepTheo == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), ngayThayTheTiepTheo);
    }

    /**
     * Tính chi phí trung bình mỗi lần thay thế
     */
    public BigDecimal tinhChiPhiTrungBinh() {
        if (soLanThayThe == 0) return BigDecimal.ZERO;
        return tongChiPhiThayThe.divide(BigDecimal.valueOf(soLanThayThe), 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Lấy mức độ quan trọng dưới dạng text
     */
    public String getMucDoQuanTrongText() {
        return switch (mucDoQuanTrong) {
            case 1 -> "Rất thấp";
            case 2 -> "Thấp";
            case 3 -> "Trung bình";
            case 4 -> "Cao";
            case 5 -> "Rất cao";
            default -> "Không xác định";
        };
    }

    /**
     * Lấy trạng thái phụ tùng
     */
    public String getTrangThaiPhuTung() {
        if (!trangThaiHoatDong) return "Ngừng sử dụng";
        if (quaHanThayThe()) return "Quá hạn thay thế";
        if (canThayThe()) return "Cần thay thế";
        return "Bình thường";
    }

    /**
     * Lấy thông tin tóm tắt
     */
    public String getThongTinTomTat() {
        return String.format("%s - %s (Lần thay thế: %d)",
                maPhuTung, tenPhuTung, soLanThayThe);
    }
}