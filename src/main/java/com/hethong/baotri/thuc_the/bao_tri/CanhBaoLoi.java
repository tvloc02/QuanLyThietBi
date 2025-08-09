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
@Table(name = "canh_bao_loi")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"thietBi"})
public class CanhBaoLoi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_canh_bao")
    private Long idCanhBao;

    @NotBlank(message = "Mã cảnh báo không được để trống")
    @Size(max = 50, message = "Mã cảnh báo không được vượt quá 50 ký tự")
    @Column(name = "ma_canh_bao", unique = true, nullable = false, length = 50)
    private String maCanhBao;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    @Column(name = "tieu_de", nullable = false, length = 200)
    private String tieuDe;

    @Size(max = 1000, message = "Mô tả không được vượt quá 1000 ký tự")
    @Column(name = "mo_ta", length = 1000)
    private String moTa;

    @Column(name = "loai_canh_bao", nullable = false, length = 50)
    private String loaiCanhBao;

    @Column(name = "muc_do_nghiem_trong", nullable = false)
    private Integer mucDoNghiemTrong = 1;

    @Column(name = "trang_thai", nullable = false, length = 30)
    private String trangThai = TRANG_THAI_CHUA_XU_LY;

    @Column(name = "ngay_phat_sinh", nullable = false)
    private LocalDateTime ngayPhatSinh;

    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @Column(name = "tu_dong_tao", nullable = false)
    private Boolean tuDongTao = false;

    @Column(name = "can_thong_bao_ngay", nullable = false)
    private Boolean canThongBaoNgay = false;

    @Column(name = "da_gui_thong_bao", nullable = false)
    private Boolean daGuiThongBao = false;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Size(max = 500, message = "Cách xử lý không được vượt quá 500 ký tự")
    @Column(name = "cach_xu_ly", length = 500)
    private String cachXuLy;

    @Column(name = "gia_tri_canh_bao", precision = 15, scale = 6)
    private BigDecimal giaTriCanhBao;

    @Column(name = "gia_tri_nguong", precision = 15, scale = 6)
    private BigDecimal giaTriNguong;

    @Column(name = "don_vi_do", length = 20)
    private String donViDo;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thiet_bi", nullable = false)
    @NotNull(message = "Thiết bị không được để trống")
    private com.hethong.baotri.thuc_the.thiet_bi.ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_xu_ly")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiXuLy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yeu_cau_bao_tri")
    private YeuCauBaoTri yeuCauBaoTri;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        if (ngayPhatSinh == null) {
            ngayPhatSinh = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    public void batDauXuLy(com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiXuLy) {
        this.nguoiXuLy = nguoiXuLy;
        this.ngayXuLy = LocalDateTime.now();
        this.trangThai = TRANG_THAI_DANG_XU_LY;
    }

    public void hoanThanhXuLy(String cachXuLy) {
        this.cachXuLy = cachXuLy;
        this.ngayHoanThanh = LocalDateTime.now();
        this.trangThai = TRANG_THAI_DA_XU_LY;
    }

    public void dongCanhBao() {
        this.trangThai = TRANG_THAI_DA_DONG;
    }

    public static final String TRANG_THAI_CHUA_XU_LY = "CHUA_XU_LY";
    public static final String TRANG_THAI_DANG_XU_LY = "DANG_XU_LY";
    public static final String TRANG_THAI_DA_XU_LY = "DA_XU_LY";
    public static final String TRANG_THAI_DA_DONG = "DA_DONG";

    public static final String LOAI_NHIET_DO_CAO = "NHIET_DO_CAO";
    public static final String LOAI_NHIET_DO_THAP = "NHIET_DO_THAP";
    public static final String LOAI_RUNG_DONG_BAT_THUONG = "RUNG_DONG_BAT_THUONG";
    public static final String LOAI_TIENG_ON_CAO = "TIENG_ON_CAO";
    public static final String LOAI_DONG_DIEN_BAT_THUONG = "DONG_DIEN_BAT_THUONG";
    public static final String LOAI_AP_SUAT_BAT_THUONG = "AP_SUAT_BAT_THUONG";
    public static final String LOAI_LOI_HE_THONG = "LOI_HE_THONG";
    public static final String LOAI_THIET_BI_NGUNG_HOAT_DONG = "THIET_BI_NGUNG_HOAT_DONG";
}