package com.hethong.baotri.thuc_the.bao_tri;

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

@Entity
@Table(name = "mau_phieu_kiem_tra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"hangMucKiemTraSet"})
public class MauPhieuKiemTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mau_phieu")
    private Long idMauPhieu;

    @NotBlank(message = "Mã mẫu phiếu không được để trống")
    @Size(max = 50, message = "Mã mẫu phiếu không được vượt quá 50 ký tự")
    @Column(name = "ma_mau_phieu", unique = true, nullable = false, length = 50)
    private String maMauPhieu;

    @NotBlank(message = "Tên mẫu phiếu không được để trống")
    @Size(max = 200, message = "Tên mẫu phiếu không được vượt quá 200 ký tự")
    @Column(name = "ten_mau_phieu", nullable = false, length = 200)
    private String tenMauPhieu;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "loai_thiet_bi", nullable = false, length = 100)
    private String loaiThietBi;

    @Column(name = "loai_kiem_tra", nullable = false, length = 50)
    private String loaiKiemTra;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    @OneToMany(mappedBy = "mauPhieuKiemTra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<HangMucKiemTra> hangMucKiemTraSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void themHangMucKiemTra(HangMucKiemTra hangMuc) {
        this.hangMucKiemTraSet.add(hangMuc);
        hangMuc.setMauPhieuKiemTra(this);
    }

    public void xoaHangMucKiemTra(HangMucKiemTra hangMuc) {
        this.hangMucKiemTraSet.remove(hangMuc);
        hangMuc.setMauPhieuKiemTra(null);
    }

    public int getSoLuongHangMuc() {
        return hangMucKiemTraSet.size();
    }
}

// Entity Hạng mục kiểm tra
@Entity
@Table(name = "hang_muc_kiem_tra")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"mauPhieuKiemTra"})
class HangMucKiemTra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hang_muc")
    private Long idHangMuc;

    @Column(name = "ten_hang_muc", nullable = false, length = 200)
    private String tenHangMuc;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "tieu_chi", length = 500)
    private String tieuChi;

    @Column(name = "thu_tu", nullable = false)
    private Integer thuTu = 1;

    @Column(name = "bat_buoc", nullable = false)
    private Boolean batBuoc = false;

    @Column(name = "loai_kiem_tra", length = 50)
    private String loaiKiemTra;

    @Column(name = "diem_toi_da")
    private Integer diemToiDa = 100;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_phieu", nullable = false)
    private MauPhieuKiemTra mauPhieuKiemTra;
}