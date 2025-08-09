package com.hethong.baotri.thuc_the.lop_hoc;

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
 * Thực thể lớp học trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "lop_hoc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"giaoVienChuNhiem", "thietBiTrongLopSet", "yeuCauBaoTriSet"})
public class LopHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lop_hoc")
    private Long idLopHoc;

    @NotBlank(message = "Mã lớp không được để trống")
    @Size(max = 20, message = "Mã lớp không được vượt quá 20 ký tự")
    @Column(name = "ma_lop", unique = true, nullable = false, length = 20)
    private String maLop; // VD: 10A1, 11B2

    @NotBlank(message = "Tên lớp không được để trống")
    @Size(max = 100, message = "Tên lớp không được vượt quá 100 ký tự")
    @Column(name = "ten_lop", nullable = false, length = 100)
    private String tenLop; // Lớp 10A1

    @Size(max = 10, message = "Khối không được vượt quá 10 ký tự")
    @Column(name = "khoi", length = 10)
    private String khoi; // 10, 11, 12

    @Size(max = 50, message = "Phòng học không được vượt quá 50 ký tự")
    @Column(name = "phong_hoc", length = 50)
    private String phongHoc; // Phòng 101, 102

    @Column(name = "si_so")
    private Integer siSo = 0; // Sĩ số học sinh

    @Size(max = 20, message = "Năm học không được vượt quá 20 ký tự")
    @Column(name = "nam_hoc", length = 20)
    private String namHoc; // 2024-2025

    @Column(name = "trang_thai_hoat_dong")
    private Boolean trangThaiHoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    // Quan hệ với NguoiDung (giáo viên chủ nhiệm)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giao_vien_chu_nhiem")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung giaoVienChuNhiem;

    // Quan hệ One-to-Many với ThietBi (thiết bị trong lớp)
    @OneToMany(mappedBy = "lopHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<com.hethong.baotri.thuc_the.thiet_bi.ThietBi> thietBiTrongLopSet = new HashSet<>();

    // Quan hệ One-to-Many với YeuCauBaoTri
    @OneToMany(mappedBy = "lopHoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri> yeuCauBaoTriSet = new HashSet<>();

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
     */
    public LopHoc(String maLop, String tenLop, String khoi) {
        this.maLop = maLop;
        this.tenLop = tenLop;
        this.khoi = khoi;
    }

    /**
     * Thêm thiết bị vào lớp
     */
    public void themThietBi(com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi) {
        this.thietBiTrongLopSet.add(thietBi);
        thietBi.setLopHoc(this);
    }

    /**
     * Xóa thiết bị khỏi lớp
     */
    public void xoaThietBi(com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi) {
        this.thietBiTrongLopSet.remove(thietBi);
        thietBi.setLopHoc(null);
    }

    /**
     * Lấy số lượng thiết bị trong lớp
     */
    public int getSoLuongThietBi() {
        return thietBiTrongLopSet.size();
    }

    /**
     * Lấy số lượng thiết bị hoạt động
     */
    public long getSoLuongThietBiHoatDong() {
        return thietBiTrongLopSet.stream()
                .filter(tb -> tb.getTrangThaiHoatDong())
                .count();
    }

    /**
     * Lấy số lượng yêu cầu bảo trì
     */
    public int getSoLuongYeuCauBaoTri() {
        return yeuCauBaoTriSet.size();
    }

    /**
     * Lấy số lượng yêu cầu bảo trì đang chờ xử lý
     */
    public long getSoLuongYeuCauChoXuLy() {
        return yeuCauBaoTriSet.stream()
                .filter(yc -> "CHO_DUYET".equals(yc.getTrangThai()) ||
                        "DA_DUYET".equals(yc.getTrangThai()) ||
                        "DANG_XU_LY".equals(yc.getTrangThai()))
                .count();
    }

    /**
     * Kiểm tra có phải lớp chủ nhiệm của giáo viên không
     */
    public boolean laLopChuNhiemCua(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung giaoVien) {
        return giaoVienChuNhiem != null && giaoVienChuNhiem.equals(giaoVien);
    }

    /**
     * Lấy thông tin tóm tắt lớp học
     */
    public String getThongTinTomTat() {
        return String.format("%s - %s (%d thiết bị, %d yêu cầu bảo trì)",
                maLop, tenLop, getSoLuongThietBi(), getSoLuongYeuCauBaoTri());
    }

    /**
     * Lấy tên giáo viên chủ nhiệm
     */
    public String getTenGiaoVienChuNhiem() {
        return giaoVienChuNhiem != null ? giaoVienChuNhiem.getHoVaTen() : "Chưa phân công";
    }

    /**
     * Các khối học
     */
    public static final String KHOI_10 = "10";
    public static final String KHOI_11 = "11";
    public static final String KHOI_12 = "12";
}