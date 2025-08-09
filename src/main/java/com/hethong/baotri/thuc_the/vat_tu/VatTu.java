package com.hethong.baotri.thuc_the.vat_tu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Thực thể vật tư trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "vat_tu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"nhomVatTu", "khoVatTuSet", "lichSuNhapXuatSet"})
public class VatTu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vat_tu")
    private Long idVatTu;

    @NotBlank(message = "Mã vật tư không được để trống")
    @Size(max = 50, message = "Mã vật tư không được vượt quá 50 ký tự")
    @Column(name = "ma_vat_tu", unique = true, nullable = false, length = 50)
    private String maVatTu;

    @NotBlank(message = "Tên vật tư không được để trống")
    @Size(max = 200, message = "Tên vật tư không được vượt quá 200 ký tự")
    @Column(name = "ten_vat_tu", nullable = false, length = 200)
    private String tenVatTu;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Size(max = 100, message = "Thông số kỹ thuật không được vượt quá 100 ký tự")
    @Column(name = "thong_so_ky_thuat", length = 100)
    private String thongSoKyThuat;

    @NotBlank(message = "Đơn vị tính không được để trống")
    @Size(max = 20, message = "Đơn vị tính không được vượt quá 20 ký tự")
    @Column(name = "don_vi_tinh", nullable = false, length = 20)
    private String donViTinh;

    @Column(name = "gia_nhap", precision = 15, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "gia_xuat", precision = 15, scale = 2)
    private BigDecimal giaXuat;

    @Column(name = "so_luong_ton_kho", nullable = false)
    private Integer soLuongTonKho = 0;

    @Column(name = "so_luong_ton_toi_thieu", nullable = false)
    private Integer soLuongTonToiThieu = 0;

    @Column(name = "so_luong_ton_toi_da", nullable = false)
    private Integer soLuongTonToiDa = 0;

    @Column(name = "muc_do_quan_trong", nullable = false)
    private Integer mucDoQuanTrong = 1; // 1: Thấp, 2: Trung bình, 3: Cao

    @Column(name = "loai_vat_tu", nullable = false, length = 50)
    private String loaiVatTu = LOAI_VAT_TU_CHUNG;

    @Size(max = 100, message = "Hãng sản xuất không được vượt quá 100 ký tự")
    @Column(name = "hang_san_xuat", length = 100)
    private String hangSanXuat;

    @Size(max = 100, message = "Model không được vượt quá 100 ký tự")
    @Column(name = "model", length = 100)
    private String model;

    @Size(max = 50, message = "Số part không được vượt quá 50 ký tự")
    @Column(name = "so_part", length = 50)
    private String soPart;

    @Column(name = "ngay_san_xuat")
    private LocalDate ngaySanXuat;

    @Column(name = "ngay_het_han")
    private LocalDate ngayHetHan;

    @Column(name = "thoi_gian_bao_quan")
    private Integer thoiGianBaoQuan; // Tính bằng tháng

    @Column(name = "dieu_kien_bao_quan", length = 200)
    private String dieuKienBaoQuan;

    @Column(name = "co_the_thay_the", nullable = false)
    private Boolean coTheThayThe = false;

    @Column(name = "vat_tu_quan_trong", nullable = false)
    private Boolean vatTuQuanTrong = false;

    @Column(name = "yeu_cau_kiem_tra", nullable = false)
    private Boolean yeuCauKiemTra = false;

    @Column(name = "chu_ky_kiem_tra")
    private Integer chuKyKiemTra; // Tính bằng ngày

    @Column(name = "ngay_kiem_tra_cuoi")
    private LocalDate ngayKiemTraCuoi;

    @Column(name = "ngay_kiem_tra_tiep_theo")
    private LocalDate ngayKiemTraTiepTheo;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "so_lan_su_dung", nullable = false)
    private Integer soLanSuDung = 0;

    @Column(name = "tong_gia_tri_nhap", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongGiaTriNhap = BigDecimal.ZERO;

    @Column(name = "tong_gia_tri_xuat", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongGiaTriXuat = BigDecimal.ZERO;

    // Quan hệ với NhomVatTu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhom_vat_tu", nullable = false)
    @NotNull(message = "Nhóm vật tư không được để trống")
    private NhomVatTu nhomVatTu;

    // Quan hệ với NguoiDung (người tạo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    // Quan hệ với NguoiDung (người cập nhật)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_cap_nhat")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiCapNhat;

    // Quan hệ One-to-Many với KhoVatTu
    @OneToMany(mappedBy = "vatTu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<KhoVatTu> khoVatTuSet = new HashSet<>();

    // Quan hệ One-to-Many với LichSuNhapXuat
    @OneToMany(mappedBy = "vatTu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LichSuNhapXuat> lichSuNhapXuatSet = new HashSet<>();

    // Quan hệ Many-to-Many với VatTu (vật tư thay thế)
    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(
            name = "vat_tu_thay_the",
            joinColumns = @JoinColumn(name = "id_vat_tu_chinh"),
            inverseJoinColumns = @JoinColumn(name = "id_vat_tu_thay_the")
    )
    private Set<VatTu> vatTuThayTheSet = new HashSet<>();

    // Quan hệ Many-to-Many với ThietBi
    @ManyToMany(mappedBy = "vatTuLienQuanSet", fetch = FetchType.LAZY)
    private Set<com.hethong.baotri.thuc_the.thiet_bi.ThietBi> thietBiLienQuanSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();

        // Tính toán ngày kiểm tra tiếp theo
        if (yeuCauKiemTra && chuKyKiemTra != null) {
            ngayKiemTraTiepTheo = LocalDate.now().plusDays(chuKyKiemTra);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    /**
     * Constructor với thông tin cơ bản
     *
     * @param maVatTu mã vật tư
     * @param tenVatTu tên vật tư
     * @param donViTinh đơn vị tính
     * @param nhomVatTu nhóm vật tư
     */
    public VatTu(String maVatTu, String tenVatTu, String donViTinh, NhomVatTu nhomVatTu) {
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.donViTinh = donViTinh;
        this.nhomVatTu = nhomVatTu;
    }

    /**
     * Thêm vật tư thay thế
     *
     * @param vatTuThayThe vật tư thay thế
     */
    public void themVatTuThayThe(VatTu vatTuThayThe) {
        this.vatTuThayTheSet.add(vatTuThayThe);
        vatTuThayThe.getVatTuThayTheSet().add(this);
    }

    /**
     * Xóa vật tư thay thế
     *
     * @param vatTuThayThe vật tư thay thế cần xóa
     */
    public void xoaVatTuThayThe(VatTu vatTuThayThe) {
        this.vatTuThayTheSet.remove(vatTuThayThe);
        vatTuThayThe.getVatTuThayTheSet().remove(this);
    }

    /**
     * Nhập vật tư vào kho
     *
     * @param soLuong số lượng nhập
     * @param giaNhap giá nhập
     */
    public void nhapKho(Integer soLuong, BigDecimal giaNhap) {
        this.soLuongTonKho += soLuong;
        this.giaNhap = giaNhap;
        this.tongGiaTriNhap = this.tongGiaTriNhap.add(giaNhap.multiply(new BigDecimal(soLuong)));
    }

    /**
     * Xuất vật tư khỏi kho
     *
     * @param soLuong số lượng xuất
     * @param giaXuat giá xuất
     * @return true nếu xuất thành công, false nếu không đủ hàng
     */
    public boolean xuatKho(Integer soLuong, BigDecimal giaXuat) {
        if (this.soLuongTonKho < soLuong) {
            return false;
        }

        this.soLuongTonKho -= soLuong;
        this.giaXuat = giaXuat;
        this.tongGiaTriXuat = this.tongGiaTriXuat.add(giaXuat.multiply(new BigDecimal(soLuong)));
        this.soLanSuDung++;

        return true;
    }

    /**
     * Kiểm tra vật tư có thiếu hàng không
     *
     * @return true nếu thiếu hàng, false nếu không
     */
    public boolean thieuHang() {
        return this.soLuongTonKho <= this.soLuongTonToiThieu;
    }

    /**
     * Kiểm tra vật tư có dư thừa không
     *
     * @return true nếu dư thừa, false nếu không
     */
    public boolean duThua() {
        return this.soLuongTonKho >= this.soLuongTonToiDa;
    }

    /**
     * Kiểm tra vật tư có hết hạn không
     *
     * @return true nếu hết hạn, false nếu không
     */
    public boolean hetHan() {
        return ngayHetHan != null && ngayHetHan.isBefore(LocalDate.now());
    }

    /**
     * Kiểm tra vật tư có cần kiểm tra không
     *
     * @return true nếu cần kiểm tra, false nếu không
     */
    public boolean canKiemTra() {
        return yeuCauKiemTra && ngayKiemTraTiepTheo != null &&
                ngayKiemTraTiepTheo.isBefore(LocalDate.now().plusDays(7));
    }

    /**
     * Cập nhật sau khi kiểm tra
     *
     * @param ketQuaKiemTra kết quả kiểm tra
     */
    public void capNhatSauKiemTra(boolean ketQuaKiemTra) {
        this.ngayKiemTraCuoi = LocalDate.now();

        if (chuKyKiemTra != null) {
            this.ngayKiemTraTiepTheo = LocalDate.now().plusDays(chuKyKiemTra);
        }

        if (!ketQuaKiemTra) {
            this.trangThaiHoatDong = false;
        }
    }

    /**
     * Tính tỷ lệ quay vòng tồn kho
     *
     * @return tỷ lệ quay vòng
     */
    public double tinhTyLeQuayVongTonKho() {
        if (soLuongTonKho == 0) return 0.0;
        return (double) soLanSuDung / soLuongTonKho;
    }

    /**
     * Tính giá trị tồn kho hiện tại
     *
     * @return giá trị tồn kho
     */
    public BigDecimal tinhGiaTriTonKho() {
        if (giaNhap == null) return BigDecimal.ZERO;
        return giaNhap.multiply(new BigDecimal(soLuongTonKho));
    }

    /**
     * Tính lợi nhuận từ vật tư
     *
     * @return lợi nhuận
     */
    public BigDecimal tinhLoiNhuan() {
        return tongGiaTriXuat.subtract(tongGiaTriNhap);
    }

    /**
     * Lấy mức độ quan trọng dưới dạng text
     *
     * @return mức độ quan trọng
     */
    public String getMucDoQuanTrongText() {
        return switch (mucDoQuanTrong) {
            case 1 -> "Thấp";
            case 2 -> "Trung bình";
            case 3 -> "Cao";
            default -> "Không xác định";
        };
    }

    /**
     * Lấy trạng thái tồn kho
     *
     * @return trạng thái tồn kho
     */
    public String getTrangThaiTonKho() {
        if (thieuHang()) return "Thiếu hàng";
        if (duThua()) return "Dư thừa";
        return "Bình thường";
    }

    /**
     * Lấy số ngày đến hạn kiểm tra
     *
     * @return số ngày
     */
    public long getSoNgayDenHanKiemTra() {
        if (ngayKiemTraTiepTheo == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), ngayKiemTraTiepTheo);
    }

    /**
     * Lấy số ngày đến hạn sử dụng
     *
     * @return số ngày
     */
    public long getSoNgayDenHanSuDung() {
        if (ngayHetHan == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), ngayHetHan);
    }

    /**
     * Kiểm tra vật tư có khả dụng không
     *
     * @return true nếu khả dụng, false nếu không
     */
    public boolean khaDung() {
        return trangThaiHoatDong && soLuongTonKho > 0 && !hetHan();
    }

    /**
     * Lấy thông tin tóm tắt vật tư
     *
     * @return thông tin tóm tắt
     */
    public String getThongTinTomTat() {
        return String.format("%s - %s (%d %s)",
                maVatTu, tenVatTu, soLuongTonKho, donViTinh);
    }

    /**
     * Các loại vật tư
     */
    public static final String LOAI_VAT_TU_CHUNG = "VAT_TU_CHUNG";
    public static final String LOAI_PHU_TUNG = "PHU_TUNG";
    public static final String LOAI_CONG_CU = "CONG_CU";
    public static final String LOAI_NGUYEN_LIEU = "NGUYEN_LIEU";
    public static final String LOAI_HOA_CHAT = "HOA_CHAT";
    public static final String LOAI_DUNG_CU_BAO_HO = "DUNG_CU_BAO_HO";
    public static final String LOAI_THIET_BI_DO_LUONG = "THIET_BI_DO_LUONG";
    public static final String LOAI_VAT_TU_TIEU_HAO = "VAT_TU_TIEU_HAO";

    /**
     * Mức độ quan trọng
     */
    public static final int MUC_DO_THAP = 1;
    public static final int MUC_DO_TRUNG_BINH = 2;
    public static final int MUC_DO_CAO = 3;
}