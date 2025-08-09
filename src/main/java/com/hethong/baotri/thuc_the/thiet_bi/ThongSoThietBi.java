package com.hethong.baotri.thuc_the.thiet_bi;

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
 * Thực thể thông số thiết bị trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "thong_so_thiet_bi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi"})
public class ThongSoThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thong_so")
    private Long idThongSo;

    @NotBlank(message = "Tên thông số không được để trống")
    @Size(max = 100, message = "Tên thông số không được vượt quá 100 ký tự")
    @Column(name = "ten_thong_so", nullable = false, length = 100)
    private String tenThongSo;

    @Column(name = "gia_tri_so", precision = 15, scale = 6)
    private BigDecimal giaTriSo;

    @Size(max = 500, message = "Giá trị văn bản không được vượt quá 500 ký tự")
    @Column(name = "gia_tri_van_ban", length = 500)
    private String giaTriVanBan;

    @Column(name = "gia_tri_logic")
    private Boolean giaTriLogic;

    @Column(name = "gia_tri_ngay_thang")
    private LocalDateTime giaTriNgayThang;

    @Size(max = 20, message = "Đơn vị không được vượt quá 20 ký tự")
    @Column(name = "don_vi", length = 20)
    private String donVi;

    @Size(max = 50, message = "Loại thông số không được vượt quá 50 ký tự")
    @Column(name = "loai_thong_so", nullable = false, length = 50)
    private String loaiThongSo;

    @Column(name = "gia_tri_min", precision = 15, scale = 6)
    private BigDecimal giaTriMin;

    @Column(name = "gia_tri_max", precision = 15, scale = 6)
    private BigDecimal giaTriMax;

    @Column(name = "gia_tri_mac_dinh", precision = 15, scale = 6)
    private BigDecimal giaTriMacDinh;

    @Column(name = "bat_buoc", nullable = false)
    private Boolean batBuoc = false;

    @Column(name = "hien_thi_tren_giao_dien", nullable = false)
    private Boolean hienThiTrenGiaoDien = true;

    @Column(name = "co_the_chinh_sua", nullable = false)
    private Boolean coTheChinhSua = true;

    @Column(name = "thu_tu_hien_thi", nullable = false)
    private Integer thuTuHienThi = 0;

    @Size(max = 200, message = "Mô tả không được vượt quá 200 ký tự")
    @Column(name = "mo_ta", length = 200)
    private String moTa;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    // Quan hệ với ThietBi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi", nullable = false)
    @NotNull(message = "Thiết bị không được để trống")
    private ThietBi thietBi;

    // Quan hệ với NguoiDung (người tạo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    // Quan hệ với NguoiDung (người cập nhật)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_cap_nhat")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiCapNhat;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    /**
     * Constructor với thông tin cơ bản
     *
     * @param tenThongSo tên thông số
     * @param loaiThongSo loại thông số
     * @param thietBi thiết bị
     */
    public ThongSoThietBi(String tenThongSo, String loaiThongSo, ThietBi thietBi) {
        this.tenThongSo = tenThongSo;
        this.loaiThongSo = loaiThongSo;
        this.thietBi = thietBi;
    }

    /**
     * Lấy giá trị thông số dưới dạng chuỗi
     *
     * @return giá trị thông số
     */
    public String getGiaTriString() {
        return switch (loaiThongSo) {
            case LOAI_SO -> {
                if (giaTriSo != null) {
                    yield giaTriSo.toString() + (donVi != null ? " " + donVi : "");
                }
                yield "";
            }
            case LOAI_VAN_BAN -> giaTriVanBan != null ? giaTriVanBan : "";
            case LOAI_LOGIC -> giaTriLogic != null ? (giaTriLogic ? "Có" : "Không") : "";
            case LOAI_NGAY_THANG -> giaTriNgayThang != null ? giaTriNgayThang.toString() : "";
            default -> "";
        };
    }

    /**
     * Thiết lập giá trị thông số từ chuỗi
     *
     * @param giaTriString giá trị chuỗi
     */
    public void setGiaTriString(String giaTriString) {
        if (giaTriString == null || giaTriString.trim().isEmpty()) {
            return;
        }

        switch (loaiThongSo) {
            case LOAI_SO -> {
                try {
                    this.giaTriSo = new BigDecimal(giaTriString);
                } catch (NumberFormatException e) {
                    // Xử lý lỗi chuyển đổi
                    this.giaTriSo = null;
                }
            }
            case LOAI_VAN_BAN -> this.giaTriVanBan = giaTriString;
            case LOAI_LOGIC -> this.giaTriLogic = Boolean.parseBoolean(giaTriString);
            case LOAI_NGAY_THANG -> {
                try {
                    this.giaTriNgayThang = LocalDateTime.parse(giaTriString);
                } catch (Exception e) {
                    // Xử lý lỗi chuyển đổi
                    this.giaTriNgayThang = null;
                }
            }
        }
    }

    /**
     * Kiểm tra giá trị có hợp lệ không
     *
     * @return true nếu hợp lệ, false nếu không
     */
    public boolean giaTriHopLe() {
        return switch (loaiThongSo) {
            case LOAI_SO -> {
                if (giaTriSo == null) yield !batBuoc;
                if (giaTriMin != null && giaTriSo.compareTo(giaTriMin) < 0) yield false;
                if (giaTriMax != null && giaTriSo.compareTo(giaTriMax) > 0) yield false;
                yield true;
            }
            case LOAI_VAN_BAN -> giaTriVanBan != null && !giaTriVanBan.trim().isEmpty();
            case LOAI_LOGIC -> giaTriLogic != null;
            case LOAI_NGAY_THANG -> giaTriNgayThang != null;
            default -> !batBuoc;
        };
    }

    /**
     * Kiểm tra giá trị có nằm trong khoảng cho phép không
     *
     * @return true nếu trong khoảng, false nếu không
     */
    public boolean giaTriTrongKhoang() {
        if (loaiThongSo.equals(LOAI_SO) && giaTriSo != null) {
            if (giaTriMin != null && giaTriSo.compareTo(giaTriMin) < 0) return false;
            if (giaTriMax != null && giaTriSo.compareTo(giaTriMax) > 0) return false;
        }
        return true;
    }

    /**
     * Lấy giá trị mặc định
     *
     * @return giá trị mặc định
     */
    public String getGiaTriMacDinhString() {
        return switch (loaiThongSo) {
            case LOAI_SO -> giaTriMacDinh != null ? giaTriMacDinh.toString() : "";
            case LOAI_VAN_BAN -> "";
            case LOAI_LOGIC -> "false";
            case LOAI_NGAY_THANG -> "";
            default -> "";
        };
    }

    /**
     * Áp dụng giá trị mặc định
     */
    public void apDungGiaTriMacDinh() {
        switch (loaiThongSo) {
            case LOAI_SO -> this.giaTriSo = this.giaTriMacDinh;
            case LOAI_VAN_BAN -> this.giaTriVanBan = "";
            case LOAI_LOGIC -> this.giaTriLogic = false;
            case LOAI_NGAY_THANG -> this.giaTriNgayThang = LocalDateTime.now();
        }
    }

    /**
     * Sao chép thông số cho thiết bị khác
     *
     * @param thietBiMoi thiết bị mới
     * @return thông số mới
     */
    public ThongSoThietBi saoChepChoThietBi(ThietBi thietBiMoi) {
        ThongSoThietBi thongSoMoi = new ThongSoThietBi();
        thongSoMoi.setTenThongSo(this.tenThongSo);
        thongSoMoi.setLoaiThongSo(this.loaiThongSo);
        thongSoMoi.setDonVi(this.donVi);
        thongSoMoi.setGiaTriMin(this.giaTriMin);
        thongSoMoi.setGiaTriMax(this.giaTriMax);
        thongSoMoi.setGiaTriMacDinh(this.giaTriMacDinh);
        thongSoMoi.setBatBuoc(this.batBuoc);
        thongSoMoi.setHienThiTrenGiaoDien(this.hienThiTrenGiaoDien);
        thongSoMoi.setCoTheChinhSua(this.coTheChinhSua);
        thongSoMoi.setThuTuHienThi(this.thuTuHienThi);
        thongSoMoi.setMoTa(this.moTa);
        thongSoMoi.setThietBi(thietBiMoi);
        thongSoMoi.apDungGiaTriMacDinh();
        return thongSoMoi;
    }

    /**
     * Kiểm tra thông số có cần cập nhật không
     *
     * @return true nếu cần cập nhật, false nếu không
     */
    public boolean canCapNhat() {
        return this.coTheChinhSua && this.trangThaiHoatDong;
    }

    /**
     * Lấy thông tin chi tiết thông số
     *
     * @return thông tin chi tiết
     */
    public String getThongTinChiTiet() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tên: ").append(tenThongSo).append("\n");
        sb.append("Loại: ").append(layTenLoaiThongSo()).append("\n");
        sb.append("Giá trị: ").append(getGiaTriString()).append("\n");
        if (giaTriMin != null || giaTriMax != null) {
            sb.append("Khoảng giá trị: ");
            if (giaTriMin != null) sb.append(giaTriMin);
            sb.append(" - ");
            if (giaTriMax != null) sb.append(giaTriMax);
            sb.append("\n");
        }
        if (moTa != null && !moTa.trim().isEmpty()) {
            sb.append("Mô tả: ").append(moTa).append("\n");
        }
        return sb.toString();
    }

    /**
     * Lấy tên loại thông số
     *
     * @return tên loại thông số
     */
    public String layTenLoaiThongSo() {
        return switch (loaiThongSo) {
            case LOAI_SO -> "Số";
            case LOAI_VAN_BAN -> "Văn bản";
            case LOAI_LOGIC -> "Đúng/Sai";
            case LOAI_NGAY_THANG -> "Ngày tháng";
            default -> "Không xác định";
        };
    }

    /**
     * Các loại thông số
     */
    public static final String LOAI_SO = "SO";
    public static final String LOAI_VAN_BAN = "VAN_BAN";
    public static final String LOAI_LOGIC = "LOGIC";
    public static final String LOAI_NGAY_THANG = "NGAY_THANG";

    /**
     * Các thông số thiết bị mặc định
     */
    public static final String CONG_SUAT = "CONG_SUAT";
    public static final String DIEN_AP = "DIEN_AP";
    public static final String DONG_DIEN = "DONG_DIEN";
    public static final String NHIET_DO = "NHIET_DO";
    public static final String DO_AM = "DO_AM";
    public static final String AP_SUAT = "AP_SUAT";
    public static final String TOC_DO = "TOC_DO";
    public static final String RUNG_DONG = "RUNG_DONG";
    public static final String TIENG_ON = "TIENG_ON";
    public static final String LUAN_LUONG = "LUAN_LUONG";
    public static final String MUC_DICH = "MUC_DICH";
    public static final String THOI_GIAN_HOAT_DONG = "THOI_GIAN_HOAT_DONG";
}