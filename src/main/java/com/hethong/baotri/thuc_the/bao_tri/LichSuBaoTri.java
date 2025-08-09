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
@Table(name = "lich_su_bao_tri")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi", "congViecBaoTri"})
class LichSuBaoTri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich_su")
    private Long idLichSu;

    @Column(name = "ngay_bao_tri", nullable = false)
    private LocalDateTime ngayBaoTri;

    @Column(name = "loai_bao_tri", nullable = false, length = 50)
    private String loaiBaoTri;

    @Column(name = "noi_dung", length = 1000)
    private String noiDung;

    @Column(name = "ket_qua", length = 1000)
    private String ketQua;

    @Column(name = "thoi_gian_thuc_hien")
    private Integer thoiGianThucHien;

    @Column(name = "chi_phi", precision = 15, scale = 2)
    private BigDecimal chiPhi;

    @Column(name = "vat_tu_su_dung", length = 500)
    private String vatTuSuDung;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi", nullable = false)
    @NotNull(message = "Thiết bị không được để trống")
    private com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_bao_tri")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiBaoTri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cong_viec_bao_tri")
    private CongViecBaoTri congViecBaoTri;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (ngayBaoTri == null) {
            ngayBaoTri = LocalDateTime.now();
        }
    }

    public static final String LOAI_BAO_TRI_DINH_KY = "BAO_TRI_DINH_KY";
    public static final String LOAI_SUA_CHUA_KHAN_CAP = "SUA_CHUA_KHAN_CAP";
    public static final String LOAI_BAO_DUONG_PHONG_NGUA = "BAO_DUONG_PHONG_NGUA";
    public static final String LOAI_THAY_THE_LINH_KIEN = "THAY_THE_LINH_KIEN";
    public static final String LOAI_KIEM_TRA_AN_TOAN = "KIEM_TRA_AN_TOAN";
}