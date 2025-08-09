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
@Table(name = "cong_viec_bao_tri")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"yeuCauBaoTri", "lichSuBaoTriSet"})
public class CongViecBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cong_viec")
    private Long idCongViec;

    @NotBlank(message = "Tên công việc không được để trống")
    @Size(max = 200, message = "Tên công việc không được vượt quá 200 ký tự")
    @Column(name = "ten_cong_viec", nullable = false, length = 200)
    private String tenCongViec;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "thu_tu_thuc_hien", nullable = false)
    private Integer thuTuThucHien = 1;

    @Column(name = "thoi_gian_du_kien")
    private Integer thoiGianDuKien;

    @Column(name = "thoi_gian_thuc_te")
    private Integer thoiGianThucTe;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = TRANG_THAI_CHUA_THUC_HIEN;

    @Column(name = "ngay_bat_dau")
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @Column(name = "ket_qua", length = 1000)
    private String ketQua;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yeu_cau_bao_tri", nullable = false)
    @NotNull(message = "Yêu cầu bảo trì không được để trống")
    private YeuCauBaoTri yeuCauBaoTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_thuc_hien")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiThucHien;

    @OneToMany(mappedBy = "congViecBaoTri", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LichSuBaoTri> lichSuBaoTriSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void batDauThucHien() {
        this.ngayBatDau = LocalDateTime.now();
        this.trangThai = TRANG_THAI_DANG_THUC_HIEN;
    }

    public void hoanThanhCongViec(String ketQua, Integer thoiGianThucTe) {
        this.ketQua = ketQua;
        this.thoiGianThucTe = thoiGianThucTe;
        this.ngayHoanThanh = LocalDateTime.now();
        this.trangThai = TRANG_THAI_HOAN_THANH;
    }

    public static final String TRANG_THAI_CHUA_THUC_HIEN = "CHUA_THUC_HIEN";
    public static final String TRANG_THAI_DANG_THUC_HIEN = "DANG_THUC_HIEN";
    public static final String TRANG_THAI_HOAN_THANH = "HOAN_THANH";
    public static final String TRANG_THAI_TAM_DUNG = "TAM_DUNG";
    public static final String TRANG_THAI_HUY = "HUY";
}