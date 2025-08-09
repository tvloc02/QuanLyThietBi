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
@Table(name = "yeu_cau_bao_tri")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi", "congViecBaoTriSet", "lopHoc", "hinhAnhYeuCauSet"})
public class YeuCauBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_yeu_cau")
    private Long idYeuCau;

    @NotBlank(message = "Mã yêu cầu không được để trống")
    @Size(max = 50, message = "Mã yêu cầu không được vượt quá 50 ký tự")
    @Column(name = "ma_yeu_cau", unique = true, nullable = false, length = 50)
    private String maYeuCau;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Column(name = "mo_ta_chi_tiet", columnDefinition = "NVARCHAR(MAX)")
    private String moTaChiTiet;

    @Column(name = "muc_do_uu_tien", nullable = false, length = 20)
    private String mucDoUuTien = MUC_DO_BINH_THUONG;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = TRANG_THAI_CHO_DUYET;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "ghi_chu_duyet", length = 500)
    private String ghiChuDuyet;

    @Column(name = "ngay_bat_dau_xu_ly")
    private LocalDateTime ngayBatDauXuLy;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @Column(name = "ket_qua_xu_ly", columnDefinition = "NVARCHAR(MAX)")
    private String ketQuaXuLy;

    @Column(name = "chi_phi_sua_chua", precision = 12, scale = 2)
    private BigDecimal chiPhiSuaChua;

    @Column(name = "danh_gia_cua_nguoi_tao")
    private Integer danhGiaCuaNguoiTao; // 1-5 sao

    @Column(name = "nhan_xet_cua_nguoi_tao", length = 500)
    private String nhanXetCuaNguoiTao;

    // Quan hệ với NguoiDung (người tạo - giáo viên)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao", nullable = false)
    @NotNull(message = "Người tạo không được để trống")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    // Quan hệ với ThietBi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi")
    private com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi;

    // Quan hệ với LopHoc - THÊM MỚI
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lop_hoc")
    private com.hethong.baotri.thuc_the.lop_hoc.LopHoc lopHoc;

    // Quan hệ với NguoiDung (người duyệt)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_duyet")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDuyet;

    // Quan hệ với NguoiDung (người xử lý)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_xu_ly")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiXuLy;

    // Quan hệ với DoiBaoTri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doi_bao_tri")
    private com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri doiBaoTri;

    // Quan hệ với KeHoachBaoTri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ke_hoach")
    private KeHoachBaoTri keHoachBaoTri;

    // Quan hệ One-to-Many với CongViecBaoTri
    @OneToMany(mappedBy = "yeuCauBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CongViecBaoTri> congViecBaoTriSet = new HashSet<>();

    // Quan hệ One-to-Many với HinhAnhYeuCau - THÊM MỚI
    @OneToMany(mappedBy = "yeuCauBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HinhAnhYeuCau> hinhAnhYeuCauSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (maYeuCau == null || maYeuCau.isEmpty()) {
            // Tự động tạo mã yêu cầu
            maYeuCau = "YC" + System.currentTimeMillis();
        }
    }

    /**
     * Duyệt yêu cầu
     */
    public void duyetYeuCau(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDuyet, String ghiChu) {
        this.nguoiDuyet = nguoiDuyet;
        this.ngayDuyet = LocalDateTime.now();
        this.ghiChuDuyet = ghiChu;
        this.trangThai = TRANG_THAI_DA_DUYET;
    }

    /**
     * Từ chối yêu cầu
     */
    public void tuChoiYeuCau(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiDuyet, String lyDo) {
        this.nguoiDuyet = nguoiDuyet;
        this.ngayDuyet = LocalDateTime.now();
        this.ghiChuDuyet = lyDo;
        this.trangThai = TRANG_THAI_TU_CHOI;
    }

    /**
     * Bắt đầu xử lý
     */
    public void batDauXuLy(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiXuLy) {
        this.nguoiXuLy = nguoiXuLy;
        this.ngayBatDauXuLy = LocalDateTime.now();
        this.trangThai = TRANG_THAI_DANG_XU_LY;
    }

    /**
     * Hoàn thành xử lý
     */
    public void hoanThanhXuLy(String ketQua, BigDecimal chiPhi) {
        this.ketQuaXuLy = ketQua;
        this.chiPhiSuaChua = chiPhi;
        this.ngayHoanThanh = LocalDateTime.now();
        this.trangThai = TRANG_THAI_HOAN_THANH;
    }

    /**
     * Đánh giá kết quả
     */
    public void danhGiaKetQua(Integer diem, String nhanXet) {
        this.danhGiaCuaNguoiTao = diem;
        this.nhanXetCuaNguoiTao = nhanXet;
    }

    /**
     * Thêm hình ảnh
     */
    public void themHinhAnh(HinhAnhYeuCau hinhAnh) {
        this.hinhAnhYeuCauSet.add(hinhAnh);
        hinhAnh.setYeuCauBaoTri(this);
    }

    /**
     * Lấy mức độ ưu tiên dạng số
     */
    public int getMucDoUuTienSo() {
        return switch (mucDoUuTien) {
            case MUC_DO_KHONG_KHAN -> 1;
            case MUC_DO_BINH_THUONG -> 2;
            case MUC_DO_KHAN -> 3;
            case MUC_DO_RAT_KHAN -> 4;
            default -> 2;
        };
    }

    /**
     * Lấy thời gian xử lý (giờ)
     */
    public long getThoiGianXuLy() {
        if (ngayBatDauXuLy == null || ngayHoanThanh == null) {
            return 0;
        }
        return java.time.Duration.between(ngayBatDauXuLy, ngayHoanThanh).toHours();
    }

    /**
     * Kiểm tra có phải yêu cầu từ giáo viên không
     */
    public boolean laYeuCauTuGiaoVien() {
        return nguoiTao != null && nguoiTao.coVaiTro("GIAO_VIEN");
    }

    /**
     * Lấy tên lớp học
     */
    public String getTenLopHoc() {
        return lopHoc != null ? lopHoc.getTenLop() : "Không xác định";
    }

    /**
     * Lấy tên thiết bị
     */
    public String getTenThietBi() {
        return thietBi != null ? thietBi.getTenThietBi() : "Không xác định";
    }

    // Các trạng thái
    public static final String TRANG_THAI_CHO_DUYET = "CHO_DUYET";
    public static final String TRANG_THAI_DA_DUYET = "DA_DUYET";
    public static final String TRANG_THAI_DANG_XU_LY = "DANG_XU_LY";
    public static final String TRANG_THAI_HOAN_THANH = "HOAN_THANH";
    public static final String TRANG_THAI_TU_CHOI = "TU_CHOI";

    // Mức độ ưu tiên
    public static final String MUC_DO_KHONG_KHAN = "KHONG_KHAN";
    public static final String MUC_DO_BINH_THUONG = "BINH_THUONG";
    public static final String MUC_DO_KHAN = "KHAN";
    public static final String MUC_DO_RAT_KHAN = "RAT_KHAN";
}