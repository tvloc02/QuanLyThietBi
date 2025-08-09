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
 * Thực thể vai trò trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "vai_tro")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ CHỈ SỬ DỤNG ID
@ToString(exclude = {"nguoiDungSet", "quyenSet"}) // ✅ LOẠI BỎ circular references
public class VaiTro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vai_tro")
    @EqualsAndHashCode.Include // ✅ CHỈ SỬ DỤNG ID cho equals/hashCode
    private Long idVaiTro;

    // Thêm alias getId()
    public Long getId() {
        return getIdVaiTro();
    }

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 50, message = "Tên vai trò không được vượt quá 50 ký tự")
    @Column(name = "ten_vai_tro", unique = true, nullable = false, length = 50)
    private String tenVaiTro;

    @Size(max = 200, message = "Mô tả vai trò không được vượt quá 200 ký tự")
    @Column(name = "mo_ta", length = 200)
    private String moTa;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    // ✅ Quan hệ Many-to-Many với NguoiDung - TRÁNH CIRCULAR REFERENCE
    @ManyToMany(mappedBy = "vaiTroSet", fetch = FetchType.LAZY)
    @JsonIgnore // ✅ QUAN TRỌNG: Tránh JSON serialization loop
    private Set<NguoiDung> nguoiDungSet = new HashSet<>();

    // ✅ Quan hệ Many-to-Many với Quyen - GIỮ EAGER để load quyền cho authentication
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "vai_tro_quyen",
            joinColumns = @JoinColumn(name = "id_vai_tro"),
            inverseJoinColumns = @JoinColumn(name = "id_quyen")
    )
    private Set<Quyen> quyenSet = new HashSet<>();

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
     * Constructor với tên vai trò
     *
     * @param tenVaiTro tên vai trò
     */
    public VaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    /**
     * Constructor với tên vai trò và mô tả
     *
     * @param tenVaiTro tên vai trò
     * @param moTa mô tả vai trò
     */
    public VaiTro(String tenVaiTro, String moTa) {
        this.tenVaiTro = tenVaiTro;
        this.moTa = moTa;
    }

    /**
     * ✅ SỬA: Phương thức thêm quyền cho vai trò - TRÁNH CIRCULAR REFERENCE
     *
     * @param quyen quyền cần thêm
     */
    public void themQuyen(Quyen quyen) {
        if (this.quyenSet == null) {
            this.quyenSet = new HashSet<>();
        }
        this.quyenSet.add(quyen);
        // ✅ KHÔNG gọi quyen.getVaiTroSet().add(this) để tránh circular reference
    }

    /**
     * ✅ SỬA: Phương thức xóa quyền khỏi vai trò - TRÁNH CIRCULAR REFERENCE
     *
     * @param quyen quyền cần xóa
     */
    public void xoaQuyen(Quyen quyen) {
        if (this.quyenSet != null) {
            this.quyenSet.remove(quyen);
        }
        // ✅ KHÔNG gọi quyen.getVaiTroSet().remove(this) để tránh circular reference
    }

    /**
     * Kiểm tra vai trò có quyền cụ thể hay không
     *
     * @param tenQuyen tên quyền cần kiểm tra
     * @return true nếu có quyền, false nếu không
     */
    public boolean coQuyen(String tenQuyen) {
        if (quyenSet == null) return false;
        return quyenSet.stream()
                .anyMatch(quyen -> quyen.getTenQuyen().equals(tenQuyen));
    }

    /**
     * ✅ SỬA: Lấy số lượng người dùng có vai trò này - TRÁNH LAZY LOADING
     *
     * @return số lượng người dùng
     */
    public int getSoLuongNguoiDung() {
        try {
            return nguoiDungSet != null ? nguoiDungSet.size() : 0;
        } catch (Exception e) {
            // Tránh lazy loading exception
            return 0;
        }
    }

    /**
     * Lấy số lượng quyền của vai trò
     *
     * @return số lượng quyền
     */
    public int getSoLuongQuyen() {
        return quyenSet != null ? quyenSet.size() : 0;
    }

    /**
     * ✅ SỬA: Kiểm tra vai trò có đang được sử dụng hay không - TRÁNH LAZY LOADING
     *
     * @return true nếu có người dùng, false nếu không
     */
    public boolean dangDuocSuDung() {
        try {
            return nguoiDungSet != null && !nguoiDungSet.isEmpty();
        } catch (Exception e) {
            // Tránh lazy loading exception
            return false;
        }
    }

    // Các vai trò mặc định của hệ thống
    public static final String QUAN_TRI_VIEN = "QUAN_TRI_VIEN";
    public static final String HIEU_TRUONG = "HIEU_TRUONG";
    public static final String TRUONG_PHONG_CSVC = "TRUONG_PHONG_CSVC";
    public static final String NHAN_VIEN_CSVC = "NHAN_VIEN_CSVC";
    public static final String KY_THUAT_VIEN = "KY_THUAT_VIEN";
    public static final String GIAO_VIEN = "GIAO_VIEN";
    public static final String TRUONG_DOI_BAO_TRI = "TRUONG_DOI_BAO_TRI";
    public static final String NHAN_VIEN_BAO_TRI = "NHAN_VIEN_BAO_TRI";
    public static final String NHAN_VIEN_KY_THUAT = "NHAN_VIEN_KY_THUAT";
    public static final String NHAN_VIEN_QUAN_LY = "NHAN_VIEN_QUAN_LY";
    public static final String NHAN_VIEN_KHO = "NHAN_VIEN_KHO";
    public static final String NGUOI_DUNG_CO_BAN = "NGUOI_DUNG_CO_BAN";
}