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
 * Thực thể nhóm thiết bị trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "nhom_thiet_bi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBiSet", "nhomThietBiCha", "nhomThietBiConSet"})
public class NhomThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhom_thiet_bi")
    private Long idNhomThietBi;

    @NotBlank(message = "Mã nhóm thiết bị không được để trống")
    @Size(max = 50, message = "Mã nhóm thiết bị không được vượt quá 50 ký tự")
    @Column(name = "ma_nhom", unique = true, nullable = false, length = 50)
    private String maNhom;

    @NotBlank(message = "Tên nhóm thiết bị không được để trống")
    @Size(max = 200, message = "Tên nhóm thiết bị không được vượt quá 200 ký tự")
    @Column(name = "ten_nhom", nullable = false, length = 200)
    private String tenNhom;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "cap_do", nullable = false)
    private Integer capDo = 1;

    @Column(name = "thu_tu_hien_thi", nullable = false)
    private Integer thuTuHienThi = 0;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Size(max = 50, message = "Màu sắc không được vượt quá 50 ký tự")
    @Column(name = "mau_sac", length = 50)
    private String mauSac;

    @Size(max = 100, message = "Biểu tượng không được vượt quá 100 ký tự")
    @Column(name = "bieu_tuong", length = 100)
    private String bieuTuong;

    // Quan hệ tự tham chiếu - nhóm cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhom_cha")
    private NhomThietBi nhomThietBiCha;

    // Quan hệ tự tham chiếu - nhóm con
    @OneToMany(mappedBy = "nhomThietBiCha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<NhomThietBi> nhomThietBiConSet = new HashSet<>();

    // Quan hệ One-to-Many với ThietBi
    @OneToMany(mappedBy = "nhomThietBi", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ThietBi> thietBiSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();

        // Tự động tính cấp độ dựa trên nhóm cha
        if (nhomThietBiCha != null) {
            this.capDo = nhomThietBiCha.getCapDo() + 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    /**
     * Constructor với tên nhóm
     *
     * @param tenNhom tên nhóm thiết bị
     */
    public NhomThietBi(String tenNhom) {
        this.tenNhom = tenNhom;
    }

    /**
     * Constructor với mã nhóm và tên nhóm
     *
     * @param maNhom mã nhóm thiết bị
     * @param tenNhom tên nhóm thiết bị
     */
    public NhomThietBi(String maNhom, String tenNhom) {
        this.maNhom = maNhom;
        this.tenNhom = tenNhom;
    }

    /**
     * Constructor với mã nhóm, tên nhóm và mô tả
     *
     * @param maNhom mã nhóm thiết bị
     * @param tenNhom tên nhóm thiết bị
     * @param moTa mô tả nhóm thiết bị
     */
    public NhomThietBi(String maNhom, String tenNhom, String moTa) {
        this.maNhom = maNhom;
        this.tenNhom = tenNhom;
        this.moTa = moTa;
    }

    /**
     * Thêm nhóm thiết bị con
     *
     * @param nhomCon nhóm thiết bị con
     */
    public void themNhomCon(NhomThietBi nhomCon) {
        this.nhomThietBiConSet.add(nhomCon);
        nhomCon.setNhomThietBiCha(this);
        nhomCon.setCapDo(this.capDo + 1);
    }

    /**
     * Xóa nhóm thiết bị con
     *
     * @param nhomCon nhóm thiết bị con cần xóa
     */
    public void xoaNhomCon(NhomThietBi nhomCon) {
        this.nhomThietBiConSet.remove(nhomCon);
        nhomCon.setNhomThietBiCha(null);
        nhomCon.setCapDo(1);
    }

    /**
     * Thêm thiết bị vào nhóm
     *
     * @param thietBi thiết bị cần thêm
     */
    public void themThietBi(ThietBi thietBi) {
        this.thietBiSet.add(thietBi);
        thietBi.setNhomThietBi(this);
    }

    /**
     * Xóa thiết bị khỏi nhóm
     *
     * @param thietBi thiết bị cần xóa
     */
    public void xoaThietBi(ThietBi thietBi) {
        this.thietBiSet.remove(thietBi);
        thietBi.setNhomThietBi(null);
    }

    /**
     * Lấy tổng số thiết bị trong nhóm (bao gồm cả nhóm con)
     *
     * @return tổng số thiết bị
     */
    public int getTongSoThietBi() {
        int tongSo = thietBiSet.size();
        for (NhomThietBi nhomCon : nhomThietBiConSet) {
            tongSo += nhomCon.getTongSoThietBi();
        }
        return tongSo;
    }

    /**
     * Lấy số thiết bị trực tiếp trong nhóm
     *
     * @return số thiết bị trực tiếp
     */
    public int getSoThietBiTrucTiep() {
        return thietBiSet.size();
    }

    /**
     * Lấy số nhóm con trực tiếp
     *
     * @return số nhóm con
     */
    public int getSoNhomCon() {
        return nhomThietBiConSet.size();
    }

    /**
     * Kiểm tra nhóm có phải là nhóm gốc không
     *
     * @return true nếu là nhóm gốc, false nếu không
     */
    public boolean laNhomGoc() {
        return nhomThietBiCha == null;
    }

    /**
     * Lấy đường dẫn nhóm (từ gốc đến hiện tại)
     *
     * @return đường dẫn nhóm
     */
    public String getDuongDanNhom() {
        if (nhomThietBiCha == null) {
            return tenNhom;
        }
        return nhomThietBiCha.getDuongDanNhom() + " > " + tenNhom;
    }

    /**
     * Lấy tên đầy đủ nhóm (bao gồm cấp độ)
     *
     * @return tên đầy đủ nhóm
     */
    public String getTenDayDu() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < capDo; i++) {
            sb.append("  ");
        }
        sb.append(tenNhom);
        return sb.toString();
    }

    /**
     * Kiểm tra nhóm có thiết bị hoạt động không
     *
     * @return true nếu có thiết bị hoạt động, false nếu không
     */
    public boolean coThietBiHoatDong() {
        for (ThietBi thietBi : thietBiSet) {
            if (thietBi.getTrangThaiHoatDong()) {
                return true;
            }
        }
        for (NhomThietBi nhomCon : nhomThietBiConSet) {
            if (nhomCon.coThietBiHoatDong()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Kiểm tra có thể xóa nhóm không
     *
     * @return true nếu có thể xóa, false nếu không
     */
    public boolean coTheXoa() {
        return thietBiSet.isEmpty() && nhomThietBiConSet.isEmpty();
    }

    /**
     * Lấy tất cả thiết bị trong nhóm và các nhóm con
     *
     * @return danh sách tất cả thiết bị
     */
    public Set<ThietBi> getTatCaThietBi() {
        Set<ThietBi> tatCaThietBi = new HashSet<>(thietBiSet);
        for (NhomThietBi nhomCon : nhomThietBiConSet) {
            tatCaThietBi.addAll(nhomCon.getTatCaThietBi());
        }
        return tatCaThietBi;
    }

    /**
     * Các nhóm thiết bị mặc định
     */
    public static final String NHOM_MAY_SAN_XUAT = "MAY_SAN_XUAT";
    public static final String NHOM_MAY_PHU_TRO = "MAY_PHU_TRO";
    public static final String NHOM_THIET_BI_DIEN = "THIET_BI_DIEN";
    public static final String NHOM_THIET_BI_CO_KHI = "THIET_BI_CO_KHI";
    public static final String NHOM_THIET_BI_TU_DONG_HOA = "THIET_BI_TU_DONG_HOA";
    public static final String NHOM_THIET_BI_DO_LUONG = "THIET_BI_DO_LUONG";
    public static final String NHOM_THIET_BI_AN_TOAN = "THIET_BI_AN_TOAN";
    public static final String NHOM_THIET_BI_MONG_TAI = "THIET_BI_MONG_TAI";
}