package com.hethong.baotri.thuc_the.vat_tu;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Thực thể nhóm vật tư trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "nhom_vat_tu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"vatTuSet", "nhomVatTuCha", "nhomVatTuConSet"})
public class NhomVatTu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhom_vat_tu")
    private Long idNhomVatTu;

    @NotBlank(message = "Mã nhóm vật tư không được để trống")
    @Size(max = 50, message = "Mã nhóm vật tư không được vượt quá 50 ký tự")
    @Column(name = "ma_nhom", unique = true, nullable = false, length = 50)
    private String maNhom;

    @NotBlank(message = "Tên nhóm vật tư không được để trống")
    @Size(max = 200, message = "Tên nhóm vật tư không được vượt quá 200 ký tự")
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

    @Column(name = "yeu_cau_duyet", nullable = false)
    private Boolean yeuCauDuyet = false;

    @Column(name = "cho_phep_ton_am", nullable = false)
    private Boolean choPhepTonAm = false;

    @Column(name = "tu_dong_tinh_gia", nullable = false)
    private Boolean tuDongTinhGia = false;

    @Column(name = "phuong_phap_tinh_gia", length = 50)
    private String phuongPhapTinhGia = PHUONG_PHAP_FIFO;

    @Column(name = "he_so_an_toan", precision = 5, scale = 2)
    private BigDecimal heSoAnToan = BigDecimal.valueOf(1.2);

    @Column(name = "chu_ky_kiem_ke")
    private Integer chuKyKiemKe; // Tính bằng ngày

    @Column(name = "ngay_kiem_ke_cuoi")
    private LocalDateTime ngayKiemKeCuoi;

    @Column(name = "ngay_kiem_ke_tiep_theo")
    private LocalDateTime ngayKiemKeTiepTheo;

    // Quan hệ tự tham chiếu - nhóm cha
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhom_cha")
    private NhomVatTu nhomVatTuCha;

    // Quan hệ tự tham chiếu - nhóm con
    @OneToMany(mappedBy = "nhomVatTuCha", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<NhomVatTu> nhomVatTuConSet = new HashSet<>();

    // Quan hệ One-to-Many với VatTu
    @OneToMany(mappedBy = "nhomVatTu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<VatTu> vatTuSet = new HashSet<>();

    // Quan hệ với NguoiDung (người quản lý)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_quan_ly")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiQuanLy;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();

        // Tự động tính cấp độ dựa trên nhóm cha
        if (nhomVatTuCha != null) {
            this.capDo = nhomVatTuCha.getCapDo() + 1;
        }

        // Tính toán ngày kiểm kê tiếp theo
        if (chuKyKiemKe != null) {
            ngayKiemKeTiepTheo = LocalDateTime.now().plusDays(chuKyKiemKe);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    /**
     * Constructor với tên nhóm
     *
     * @param tenNhom tên nhóm vật tư
     */
    public NhomVatTu(String tenNhom) {
        this.tenNhom = tenNhom;
    }

    /**
     * Constructor với mã nhóm và tên nhóm
     *
     * @param maNhom mã nhóm vật tư
     * @param tenNhom tên nhóm vật tư
     */
    public NhomVatTu(String maNhom, String tenNhom) {
        this.maNhom = maNhom;
        this.tenNhom = tenNhom;
    }

    /**
     * Constructor với mã nhóm, tên nhóm và mô tả
     *
     * @param maNhom mã nhóm vật tư
     * @param tenNhom tên nhóm vật tư
     * @param moTa mô tả nhóm vật tư
     */
    public NhomVatTu(String maNhom, String tenNhom, String moTa) {
        this.maNhom = maNhom;
        this.tenNhom = tenNhom;
        this.moTa = moTa;
    }

    /**
     * Thêm nhóm vật tư con
     *
     * @param nhomCon nhóm vật tư con
     */
    public void themNhomCon(NhomVatTu nhomCon) {
        this.nhomVatTuConSet.add(nhomCon);
        nhomCon.setNhomVatTuCha(this);
        nhomCon.setCapDo(this.capDo + 1);
    }

    /**
     * Xóa nhóm vật tư con
     *
     * @param nhomCon nhóm vật tư con cần xóa
     */
    public void xoaNhomCon(NhomVatTu nhomCon) {
        this.nhomVatTuConSet.remove(nhomCon);
        nhomCon.setNhomVatTuCha(null);
        nhomCon.setCapDo(1);
    }

    /**
     * Thêm vật tư vào nhóm
     *
     * @param vatTu vật tư cần thêm
     */
    public void themVatTu(VatTu vatTu) {
        this.vatTuSet.add(vatTu);
        vatTu.setNhomVatTu(this);
    }

    /**
     * Xóa vật tư khỏi nhóm
     *
     * @param vatTu vật tư cần xóa
     */
    public void xoaVatTu(VatTu vatTu) {
        this.vatTuSet.remove(vatTu);
        vatTu.setNhomVatTu(null);
    }

    /**
     * Lấy tổng số vật tư trong nhóm (bao gồm cả nhóm con)
     *
     * @return tổng số vật tư
     */
    public int getTongSoVatTu() {
        int tongSo = vatTuSet.size();
        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            tongSo += nhomCon.getTongSoVatTu();
        }
        return tongSo;
    }

    /**
     * Lấy số vật tư trực tiếp trong nhóm
     *
     * @return số vật tư trực tiếp
     */
    public int getSoVatTuTrucTiep() {
        return vatTuSet.size();
    }

    /**
     * Lấy số nhóm con trực tiếp
     *
     * @return số nhóm con
     */
    public int getSoNhomCon() {
        return nhomVatTuConSet.size();
    }

    /**
     * Kiểm tra nhóm có phải là nhóm gốc không
     *
     * @return true nếu là nhóm gốc, false nếu không
     */
    public boolean laNhomGoc() {
        return nhomVatTuCha == null;
    }

    /**
     * Lấy đường dẫn nhóm (từ gốc đến hiện tại)
     *
     * @return đường dẫn nhóm
     */
    public String getDuongDanNhom() {
        if (nhomVatTuCha == null) {
            return tenNhom;
        }
        return nhomVatTuCha.getDuongDanNhom() + " > " + tenNhom;
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
     * Tính tổng giá trị tồn kho của nhóm
     *
     * @return tổng giá trị tồn kho
     */
    public BigDecimal tinhTongGiaTriTonKho() {
        BigDecimal tongGiaTri = BigDecimal.ZERO;

        for (VatTu vatTu : vatTuSet) {
            tongGiaTri = tongGiaTri.add(vatTu.tinhGiaTriTonKho());
        }

        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            tongGiaTri = tongGiaTri.add(nhomCon.tinhTongGiaTriTonKho());
        }

        return tongGiaTri;
    }

    /**
     * Đếm số vật tư thiếu hàng trong nhóm
     *
     * @return số vật tư thiếu hàng
     */
    public int demVatTuThieuHang() {
        int soLuong = 0;

        for (VatTu vatTu : vatTuSet) {
            if (vatTu.thieuHang()) {
                soLuong++;
            }
        }

        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            soLuong += nhomCon.demVatTuThieuHang();
        }

        return soLuong;
    }

    /**
     * Đếm số vật tư hết hạn trong nhóm
     *
     * @return số vật tư hết hạn
     */
    public int demVatTuHetHan() {
        int soLuong = 0;

        for (VatTu vatTu : vatTuSet) {
            if (vatTu.hetHan()) {
                soLuong++;
            }
        }

        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            soLuong += nhomCon.demVatTuHetHan();
        }

        return soLuong;
    }

    /**
     * Đếm số vật tư cần kiểm tra trong nhóm
     *
     * @return số vật tư cần kiểm tra
     */
    public int demVatTuCanKiemTra() {
        int soLuong = 0;

        for (VatTu vatTu : vatTuSet) {
            if (vatTu.canKiemTra()) {
                soLuong++;
            }
        }

        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            soLuong += nhomCon.demVatTuCanKiemTra();
        }

        return soLuong;
    }

    /**
     * Lấy tất cả vật tư trong nhóm và các nhóm con
     *
     * @return danh sách tất cả vật tư
     */
    public Set<VatTu> getTatCaVatTu() {
        Set<VatTu> tatCaVatTu = new HashSet<>(vatTuSet);
        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            tatCaVatTu.addAll(nhomCon.getTatCaVatTu());
        }
        return tatCaVatTu;
    }

    /**
     * Kiểm tra nhóm có vật tư hoạt động không
     *
     * @return true nếu có vật tư hoạt động, false nếu không
     */
    public boolean coVatTuHoatDong() {
        for (VatTu vatTu : vatTuSet) {
            if (vatTu.getTrangThaiHoatDong()) {
                return true;
            }
        }
        for (NhomVatTu nhomCon : nhomVatTuConSet) {
            if (nhomCon.coVatTuHoatDong()) {
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
        return vatTuSet.isEmpty() && nhomVatTuConSet.isEmpty();
    }

    /**
     * Kiểm tra nhóm có cần kiểm kê không
     *
     * @return true nếu cần kiểm kê, false nếu không
     */
    public boolean canKiemKe() {
        return ngayKiemKeTiepTheo != null &&
                ngayKiemKeTiepTheo.isBefore(LocalDateTime.now().plusDays(7));
    }

    /**
     * Cập nhật sau khi kiểm kê
     */
    public void capNhatSauKiemKe() {
        this.ngayKiemKeCuoi = LocalDateTime.now();

        if (chuKyKiemKe != null) {
            this.ngayKiemKeTiepTheo = LocalDateTime.now().plusDays(chuKyKiemKe);
        }
    }

    /**
     * Lấy thông tin thống kê nhóm
     *
     * @return thông tin thống kê
     */
    public String getThongTinThongKe() {
        int tongSoVatTu = getTongSoVatTu();
        int vatTuThieuHang = demVatTuThieuHang();
        int vatTuHetHan = demVatTuHetHan();
        BigDecimal giaTriTonKho = tinhTongGiaTriTonKho();

        return String.format(
                "Tổng số vật tư: %d | Thiếu hàng: %d | Hết hạn: %d | Giá trị tồn kho: %,.0f",
                tongSoVatTu, vatTuThieuHang, vatTuHetHan, giaTriTonKho
        );
    }

    /**
     * Lấy số ngày đến lần kiểm kê tiếp theo
     *
     * @return số ngày
     */
    public long getSoNgayDenKiemKeTiepTheo() {
        if (ngayKiemKeTiepTheo == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), ngayKiemKeTiepTheo);
    }

    /**
     * Lấy CSS class cho màu sắc
     *
     * @return CSS class
     */
    public String getCssClass() {
        if (mauSac == null) return "secondary";
        return switch (mauSac.toLowerCase()) {
            case "green", "xanh_la" -> "success";
            case "red", "do" -> "danger";
            case "yellow", "vang" -> "warning";
            case "blue", "xanh_duong" -> "primary";
            case "orange", "cam" -> "warning";
            case "gray", "xam" -> "secondary";
            default -> "secondary";
        };
    }

    /**
     * Lấy icon Bootstrap cho nhóm
     *
     * @return icon class
     */
    public String getIconClass() {
        if (bieuTuong != null && !bieuTuong.isEmpty()) {
            return bieuTuong;
        }
        return "bi-box-seam";
    }

    /**
     * Các nhóm vật tư mặc định
     */
    public static final String NHOM_PHU_TUNG_MAY = "PHU_TUNG_MAY";
    public static final String NHOM_CONG_CU_DAC_BIET = "CONG_CU_DAC_BIET";
    public static final String NHOM_NGUYEN_LIEU_SAN_XUAT = "NGUYEN_LIEU_SAN_XUAT";
    public static final String NHOM_HOA_CHAT_CONG_NGHIEP = "HOA_CHAT_CONG_NGHIEP";
    public static final String NHOM_DUNG_CU_BAO_HO_LAO_DONG = "DUNG_CU_BAO_HO_LAO_DONG";
    public static final String NHOM_THIET_BI_DO_LUONG_KIEM_TRA = "THIET_BI_DO_LUONG_KIEM_TRA";
    public static final String NHOM_VAT_TU_TIEU_HAO = "VAT_TU_TIEU_HAO";
    public static final String NHOM_VAT_TU_VAN_PHONG = "VAT_TU_VAN_PHONG";

    /**
     * Phương pháp tính giá
     */
    public static final String PHUONG_PHAP_FIFO = "FIFO"; // First In First Out
    public static final String PHUONG_PHAP_LIFO = "LIFO"; // Last In First Out
    public static final String PHUONG_PHAP_BINH_QUAN = "BINH_QUAN"; // Weighted Average
    public static final String PHUONG_PHAP_GIA_CU_THE = "GIA_CU_THE"; // Specific Cost
}