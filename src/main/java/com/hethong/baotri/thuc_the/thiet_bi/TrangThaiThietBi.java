package com.hethong.baotri.thuc_the.thiet_bi;

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

/**
 * Thực thể trạng thái thiết bị trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "trang_thai_thiet_bi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBiSet"})
public class TrangThaiThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_trang_thai")
    private Long idTrangThai;

    @NotBlank(message = "Mã trạng thái không được để trống")
    @Size(max = 50, message = "Mã trạng thái không được vượt quá 50 ký tự")
    @Column(name = "ma_trang_thai", unique = true, nullable = false, length = 50)
    private String maTrangThai;

    @NotBlank(message = "Tên trạng thái không được để trống")
    @Size(max = 100, message = "Tên trạng thái không được vượt quá 100 ký tự")
    @Column(name = "ten_trang_thai", nullable = false, length = 100)
    private String tenTrangThai;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Size(max = 20, message = "Màu sắc không được vượt quá 20 ký tự")
    @Column(name = "mau_sac", length = 20)
    private String mauSac;

    @Size(max = 50, message = "Biểu tượng không được vượt quá 50 ký tự")
    @Column(name = "bieu_tuong", length = 50)
    private String bieuTuong;

    @Column(name = "muc_do_uu_tien", nullable = false)
    private Integer mucDoUuTien = 1;

    @Column(name = "cho_phep_hoat_dong", nullable = false)
    private Boolean choPhepHoatDong = true;

    @Column(name = "yeu_cau_bao_tri", nullable = false)
    private Boolean yeuCauBaoTri = false;

    @Column(name = "tu_dong_tao_canh_bao", nullable = false)
    private Boolean tuDongTaoCanhBao = false;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "thu_tu_hien_thi", nullable = false)
    private Integer thuTuHienThi = 0;

    // Quan hệ One-to-Many với ThietBi
    @OneToMany(mappedBy = "trangThaiThietBi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ThietBi> thietBiSet = new HashSet<>();

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
     * Constructor với mã trạng thái
     *
     * @param maTrangThai mã trạng thái
     */
    public TrangThaiThietBi(String maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    /**
     * Constructor với mã trạng thái và tên trạng thái
     *
     * @param maTrangThai mã trạng thái
     * @param tenTrangThai tên trạng thái
     */
    public TrangThaiThietBi(String maTrangThai, String tenTrangThai) {
        this.maTrangThai = maTrangThai;
        this.tenTrangThai = tenTrangThai;
    }

    /**
     * Constructor với mã trạng thái, tên trạng thái và màu sắc
     *
     * @param maTrangThai mã trạng thái
     * @param tenTrangThai tên trạng thái
     * @param mauSac màu sắc
     */
    public TrangThaiThietBi(String maTrangThai, String tenTrangThai, String mauSac) {
        this.maTrangThai = maTrangThai;
        this.tenTrangThai = tenTrangThai;
        this.mauSac = mauSac;
    }

    /**
     * Lấy số lượng thiết bị có trạng thái này
     *
     * @return số lượng thiết bị
     */
    public int getSoLuongThietBi() {
        return thietBiSet.size();
    }

    /**
     * Kiểm tra trạng thái có đang được sử dụng không
     *
     * @return true nếu có thiết bị sử dụng, false nếu không
     */
    public boolean dangDuocSuDung() {
        return !thietBiSet.isEmpty();
    }

    /**
     * Kiểm tra trạng thái có cần bảo trì không
     *
     * @return true nếu cần bảo trì, false nếu không
     */
    public boolean canBaoTri() {
        return this.yeuCauBaoTri;
    }

    /**
     * Kiểm tra trạng thái có cho phép thiết bị hoạt động không
     *
     * @return true nếu cho phép, false nếu không
     */
    public boolean choPhepThietBiHoatDong() {
        return this.choPhepHoatDong;
    }

    /**
     * Kiểm tra trạng thái có tự động tạo cảnh báo không
     *
     * @return true nếu tự động tạo cảnh báo, false nếu không
     */
    public boolean tuDongTaoCanhBaoLoi() {
        return this.tuDongTaoCanhBao;
    }

    /**
     * Lấy CSS class cho màu sắc
     *
     * @return CSS class
     */
    public String getCssClass() {
        if (mauSac == null) return "secondary";
        return switch (mauSac.toLowerCase()) {
            case "green", "xanh_la" -> "success";
            case "red", "do" -> "danger";
            case "yellow", "vang" -> "warning";
            case "blue", "xanh_duong" -> "primary";
            case "orange", "cam" -> "warning";
            case "gray", "xam" -> "secondary";
            default -> "secondary";
        };
    }

    /**
     * Lấy icon Bootstrap cho trạng thái
     *
     * @return icon class
     */
    public String getIconClass() {
        if (bieuTuong != null && !bieuTuong.isEmpty()) {
            return bieuTuong;
        }

        return switch (maTrangThai) {
            case HOAT_DONG -> "bi-check-circle-fill";
            case DUNG_HOAT_DONG -> "bi-pause-circle-fill";
            case BAO_TRI -> "bi-tools";
            case SUA_CHUA -> "bi-wrench";
            case HONG -> "bi-exclamation-triangle-fill";
            case NGUNG_SU_DUNG -> "bi-x-circle-fill";
            case CHO_KIEM_TRA -> "bi-clock-fill";
            case DANG_LAP_DAT -> "bi-gear-fill";
            default -> "bi-circle-fill";
        };
    }

    /**
     * Các trạng thái mặc định của thiết bị
     */
    public static final String HOAT_DONG = "HOAT_DONG";
    public static final String DUNG_HOAT_DONG = "DUNG_HOAT_DONG";
    public static final String BAO_TRI = "BAO_TRI";
    public static final String SUA_CHUA = "SUA_CHUA";
    public static final String HONG = "HONG";
    public static final String NGUNG_SU_DUNG = "NGUNG_SU_DUNG";
    public static final String CHO_KIEM_TRA = "CHO_KIEM_TRA";
    public static final String DANG_LAP_DAT = "DANG_LAP_DAT";
    public static final String KIEM_DINH = "KIEM_DINH";
    public static final String BAO_HANH = "BAO_HANH";
}