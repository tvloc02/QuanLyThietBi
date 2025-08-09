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
@Table(name = "ke_hoach_bao_tri")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"yeuCauBaoTriSet"})
public class KeHoachBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ke_hoach")
    private Long idKeHoach;

    @NotBlank(message = "Mã kế hoạch không được để trống")
    @Size(max = 50, message = "Mã kế hoạch không được vượt quá 50 ký tự")
    @Column(name = "ma_ke_hoach", unique = true, nullable = false, length = 50)
    private String maKeHoach;

    @NotBlank(message = "Tên kế hoạch không được để trống")
    @Size(max = 200, message = "Tên kế hoạch không được vượt quá 200 ký tự")
    @Column(name = "ten_ke_hoach", nullable = false, length = 200)
    private String tenKeHoach;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "loai_ke_hoach", nullable = false, length = 50)
    private String loaiKeHoach;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "chu_ky_lap_lai")
    private Integer chuKyLapLai;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = TRANG_THAI_NHAP;

    @Column(name = "ty_le_hoan_thanh", nullable = false)
    private Integer tyLeHoanThanh = 0;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao", nullable = false)
    @NotNull(message = "Người tạo không được để trống")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    @OneToMany(mappedBy = "keHoachBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<YeuCauBaoTri> yeuCauBaoTriSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public static final String TRANG_THAI_NHAP = "NHAP";
    public static final String TRANG_THAI_DUYET = "DUYET";
    public static final String TRANG_THAI_DANG_THUC_HIEN = "DANG_THUC_HIEN";
    public static final String TRANG_THAI_HOAN_THANH = "HOAN_THANH";
    public static final String TRANG_THAI_HUY = "HUY";

    public static final String LOAI_HANG_TUAN = "HANG_TUAN";
    public static final String LOAI_HANG_THANG = "HANG_THANG";
    public static final String LOAI_HANG_QUY = "HANG_QUY";
    public static final String LOAI_HANG_NAM = "HANG_NAM";
    public static final String LOAI_THEO_GIO_HOAT_DONG = "THEO_GIO_HOAT_DONG";
}