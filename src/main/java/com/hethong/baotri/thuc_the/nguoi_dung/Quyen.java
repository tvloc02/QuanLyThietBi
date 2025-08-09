package com.hethong.baotri.thuc_the.nguoi_dung;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Thực thể quyền trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "quyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ CHỈ SỬ DỤNG ID
@ToString(exclude = {"vaiTroSet"}) // ✅ LOẠI BỎ circular references
public class Quyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quyen")
    @EqualsAndHashCode.Include // ✅ CHỈ SỬ DỤNG ID cho equals/hashCode
    private Long idQuyen;

    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 50, message = "Tên quyền không được vượt quá 50 ký tự")
    @Column(name = "ten_quyen", unique = true, nullable = false, length = 50)
    private String tenQuyen;

    @Size(max = 200, message = "Mô tả quyền không được vượt quá 200 ký tự")
    @Column(name = "mo_ta", length = 200)
    private String moTa;

    @NotBlank(message = "Nhóm quyền không được để trống")
    @Size(max = 50, message = "Nhóm quyền không được vượt quá 50 ký tự")
    @Column(name = "nhom_quyen", nullable = false, length = 50)
    private String nhomQuyen;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    // ✅ Quan hệ Many-to-Many với VaiTro - TRÁNH CIRCULAR REFERENCE
    @ManyToMany(mappedBy = "quyenSet", fetch = FetchType.LAZY)
    @JsonIgnore // ✅ QUAN TRỌNG: Tránh JSON serialization loop
    private Set<VaiTro> vaiTroSet = new HashSet<>();

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
     * Constructor với tên quyền
     *
     * @param tenQuyen tên quyền
     */
    public Quyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    /**
     * Constructor với tên quyền và nhóm quyền
     *
     * @param tenQuyen tên quyền
     * @param nhomQuyen nhóm quyền
     */
    public Quyen(String tenQuyen, String nhomQuyen) {
        this.tenQuyen = tenQuyen;
        this.nhomQuyen = nhomQuyen;
    }

    /**
     * Constructor với tên quyền, nhóm quyền và mô tả
     *
     * @param tenQuyen tên quyền
     * @param nhomQuyen nhóm quyền
     * @param moTa mô tả quyền
     */
    public Quyen(String tenQuyen, String nhomQuyen, String moTa) {
        this.tenQuyen = tenQuyen;
        this.nhomQuyen = nhomQuyen;
        this.moTa = moTa;
    }

    /**
     * ✅ SỬA: Lấy số lượng vai trò có quyền này - TRÁNH LAZY LOADING
     *
     * @return số lượng vai trò
     */
    public int getSoLuongVaiTro() {
        try {
            return vaiTroSet != null ? vaiTroSet.size() : 0;
        } catch (Exception e) {
            // Tránh lazy loading exception
            return 0;
        }
    }

    /**
     * ✅ SỬA: Kiểm tra quyền có đang được sử dụng hay không - TRÁNH LAZY LOADING
     *
     * @return true nếu có vai trò sử dụng, false nếu không
     */
    public boolean dangDuocSuDung() {
        try {
            return vaiTroSet != null && !vaiTroSet.isEmpty();
        } catch (Exception e) {
            // Tránh lazy loading exception
            return false;
        }
    }

    // Các quyền mặc định của hệ thống

    // Quyền quản lý người dùng
    public static final String QUAN_LY_NGUOI_DUNG_XEM = "QUAN_LY_NGUOI_DUNG_XEM";
    public static final String QUAN_LY_NGUOI_DUNG_THEM = "QUAN_LY_NGUOI_DUNG_THEM";
    public static final String QUAN_LY_NGUOI_DUNG_SUA = "QUAN_LY_NGUOI_DUNG_SUA";
    public static final String QUAN_LY_NGUOI_DUNG_XOA = "QUAN_LY_NGUOI_DUNG_XOA";
    public static final String QUAN_LY_VAI_TRO_QUYEN = "QUAN_LY_VAI_TRO_QUYEN";

    // Quyền quản lý thiết bị
    public static final String QUAN_LY_THIET_BI_XEM = "QUAN_LY_THIET_BI_XEM";
    public static final String QUAN_LY_THIET_BI_THEM = "QUAN_LY_THIET_BI_THEM";
    public static final String QUAN_LY_THIET_BI_SUA = "QUAN_LY_THIET_BI_SUA";
    public static final String QUAN_LY_THIET_BI_XOA = "QUAN_LY_THIET_BI_XOA";
    public static final String PHAN_CONG_THIET_BI = "PHAN_CONG_THIET_BI";

    // Quyền quản lý vật tư
    public static final String QUAN_LY_VAT_TU_XEM = "QUAN_LY_VAT_TU_XEM";
    public static final String QUAN_LY_VAT_TU_THEM = "QUAN_LY_VAT_TU_THEM";
    public static final String QUAN_LY_VAT_TU_SUA = "QUAN_LY_VAT_TU_SUA";
    public static final String QUAN_LY_VAT_TU_XOA = "QUAN_LY_VAT_TU_XOA";
    public static final String QUAN_LY_KHO_VAT_TU = "QUAN_LY_KHO_VAT_TU";

    // Quyền quản lý bảo trì
    public static final String QUAN_LY_BAO_TRI_XEM = "QUAN_LY_BAO_TRI_XEM";
    public static final String TAO_KE_HOACH_BAO_TRI = "TAO_KE_HOACH_BAO_TRI";
    public static final String TAO_YEU_CAU_BAO_TRI = "TAO_YEU_CAU_BAO_TRI";
    public static final String THUC_HIEN_BAO_TRI = "THUC_HIEN_BAO_TRI";
    public static final String DUYET_YEU_CAU_BAO_TRI = "DUYET_YEU_CAU_BAO_TRI";
    public static final String KIEM_TRA_DINH_KY = "KIEM_TRA_DINH_KY";

    // Quyền quản lý đội bảo trì
    public static final String QUAN_LY_DOI_BAO_TRI_XEM = "QUAN_LY_DOI_BAO_TRI_XEM";
    public static final String QUAN_LY_DOI_BAO_TRI_THEM = "QUAN_LY_DOI_BAO_TRI_THEM";
    public static final String QUAN_LY_DOI_BAO_TRI_SUA = "QUAN_LY_DOI_BAO_TRI_SUA";
    public static final String PHAN_CONG_CONG_VIEC = "PHAN_CONG_CONG_VIEC";

    // Quyền quản lý báo cáo
    public static final String XEM_BAO_CAO_TONG_HOP = "XEM_BAO_CAO_TONG_HOP";
    public static final String XEM_BAO_CAO_OEE = "XEM_BAO_CAO_OEE";
    public static final String XEM_BAO_CAO_MTBF = "XEM_BAO_CAO_MTBF";
    public static final String XEM_THONG_KE_BAO_TRI = "XEM_THONG_KE_BAO_TRI";
    public static final String XUAT_BAO_CAO = "XUAT_BAO_CAO";

    // Quyền cài đặt hệ thống
    public static final String CAI_DAT_HE_THONG = "CAI_DAT_HE_THONG";
    public static final String CAI_DAT_THONG_SO = "CAI_DAT_THONG_SO";
    public static final String QUAN_LY_BACKUP = "QUAN_LY_BACKUP";

    // Nhóm quyền
    public static final String NHOM_NGUOI_DUNG = "NGUOI_DUNG";
    public static final String NHOM_THIET_BI = "THIET_BI";
    public static final String NHOM_VAT_TU = "VAT_TU";
    public static final String NHOM_BAO_TRI = "BAO_TRI";
    public static final String NHOM_DOI_BAO_TRI = "DOI_BAO_TRI";
    public static final String NHOM_BAO_CAO = "BAO_CAO";
    public static final String NHOM_HE_THONG = "HE_THONG";
}