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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Thực thể kho vật tư trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "kho_vat_tu")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"vatTu", "lichSuNhapXuatSet"})
public class KhoVatTu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kho_vat_tu")
    private Long idKhoVatTu;

    @NotBlank(message = "Mã kho không được để trống")
    @Size(max = 50, message = "Mã kho không được vượt quá 50 ký tự")
    @Column(name = "ma_kho", nullable = false, length = 50)
    private String maKho;

    @NotBlank(message = "Tên kho không được để trống")
    @Size(max = 200, message = "Tên kho không được vượt quá 200 ký tự")
    @Column(name = "ten_kho", nullable = false, length = 200)
    private String tenKho;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Size(max = 200, message = "Vị trí không được vượt quá 200 ký tự")
    @Column(name = "vi_tri", length = 200)
    private String viTri;

    @Column(name = "so_luong_ton", nullable = false)
    private Integer soLuongTon = 0;

    @Column(name = "so_luong_dat", nullable = false)
    private Integer soLuongDat = 0;

    @Column(name = "so_luong_cho_xuat", nullable = false)
    private Integer soLuongChoXuat = 0;

    @Column(name = "so_luong_kha_dung", nullable = false)
    private Integer soLuongKhaDung = 0;

    @Column(name = "gia_nhap_tb", precision = 15, scale = 2)
    private BigDecimal giaNhapTrungBinh = BigDecimal.ZERO;

    @Column(name = "gia_xuat_tb", precision = 15, scale = 2)
    private BigDecimal giaXuatTrungBinh = BigDecimal.ZERO;

    @Column(name = "gia_tri_ton_kho", precision = 15, scale = 2)
    private BigDecimal giaTriTonKho = BigDecimal.ZERO;

    @Column(name = "dien_tich_kho", precision = 10, scale = 2)
    private BigDecimal dienTichKho;

    @Column(name = "dung_tich_kho", precision = 10, scale = 2)
    private BigDecimal dungTichKho;

    @Column(name = "trong_luong_toi_da", precision = 10, scale = 2)
    private BigDecimal trongLuongToiDa;

    @Column(name = "nhiet_do_bao_quan_min", precision = 5, scale = 2)
    private BigDecimal nhietDoBaoQuanMin;

    @Column(name = "nhiet_do_bao_quan_max", precision = 5, scale = 2)
    private BigDecimal nhietDoBaoQuanMax;

    @Column(name = "do_am_bao_quan_min", precision = 5, scale = 2)
    private BigDecimal doAmBaoQuanMin;

    @Column(name = "do_am_bao_quan_max", precision = 5, scale = 2)
    private BigDecimal doAmBaoQuanMax;

    @Column(name = "yeu_cau_bao_quan_dac_biet", length = 500)
    private String yeuCauBaoQuanDacBiet;

    @Column(name = "kho_chinh", nullable = false)
    private Boolean khoChinh = false;

    @Column(name = "kho_tam", nullable = false)
    private Boolean khoTam = false;

    @Column(name = "kho_kiem_dinh", nullable = false)
    private Boolean khoKiemDinh = false;

    @Column(name = "kho_hong", nullable = false)
    private Boolean khoHong = false;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "lan_kiem_ke_cuoi")
    private LocalDateTime lanKiemKeCuoi;

    @Column(name = "nguoi_kiem_ke_cuoi", length = 100)
    private String nguoiKiemKeCuoi;

    @Column(name = "ket_qua_kiem_ke_cuoi", length = 500)
    private String ketQuaKiemKeCuoi;

    @Column(name = "so_lan_nhap", nullable = false)
    private Integer soLanNhap = 0;

    @Column(name = "so_lan_xuat", nullable = false)
    private Integer soLanXuat = 0;

    @Column(name = "tong_gia_tri_nhap", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongGiaTriNhap = BigDecimal.ZERO;

    @Column(name = "tong_gia_tri_xuat", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongGiaTriXuat = BigDecimal.ZERO;

    // Quan hệ với VatTu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vat_tu", nullable = false)
    @NotNull(message = "Vật tư không được để trống")
    private VatTu vatTu;

    // Quan hệ với NguoiDung (thủ kho)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_thu_kho")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung thuKho;

    // Quan hệ với NguoiDung (người tạo)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_tao")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiTao;

    // Quan hệ One-to-Many với LichSuNhapXuat
    @OneToMany(mappedBy = "khoVatTu", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<LichSuNhapXuat> lichSuNhapXuatSet = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
        capNhatSoLuongKhaDung();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
        capNhatSoLuongKhaDung();
    }

    /**
     * Constructor với thông tin cơ bản
     *
     * @param maKho mã kho
     * @param tenKho tên kho
     * @param vatTu vật tư
     */
    public KhoVatTu(String maKho, String tenKho, VatTu vatTu) {
        this.maKho = maKho;
        this.tenKho = tenKho;
        this.vatTu = vatTu;
    }

    /**
     * Nhập vật tư vào kho
     *
     * @param soLuong số lượng nhập
     * @param giaNhap giá nhập
     * @param ghiChu ghi chú
     */
    public void nhapKho(Integer soLuong, BigDecimal giaNhap, String ghiChu) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng nhập phải lớn hơn 0");
        }

        // Cập nhật số lượng tồn
        this.soLuongTon += soLuong;

        // Cập nhật giá nhập trung bình
        BigDecimal giaTriNhapMoi = giaNhap.multiply(new BigDecimal(soLuong));
        this.tongGiaTriNhap = this.tongGiaTriNhap.add(giaTriNhapMoi);
        this.giaTriTonKho = this.giaTriTonKho.add(giaTriNhapMoi);

        if (this.soLuongTon > 0) {
            this.giaNhapTrungBinh = this.tongGiaTriNhap.divide(
                    new BigDecimal(this.soLuongTon), 2, BigDecimal.ROUND_HALF_UP);
        }

        this.soLanNhap++;
        capNhatSoLuongKhaDung();
    }

    /**
     * Xuất vật tư khỏi kho
     *
     * @param soLuong số lượng xuất
     * @param giaXuat giá xuất
     * @param ghiChu ghi chú
     * @return true nếu xuất thành công, false nếu không đủ hàng
     */
    public boolean xuatKho(Integer soLuong, BigDecimal giaXuat, String ghiChu) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng xuất phải lớn hơn 0");
        }

        if (this.soLuongKhaDung < soLuong) {
            return false;
        }

        // Cập nhật số lượng tồn
        this.soLuongTon -= soLuong;

        // Cập nhật giá trị xuất
        BigDecimal giaTriXuatMoi = giaXuat.multiply(new BigDecimal(soLuong));
        this.tongGiaTriXuat = this.tongGiaTriXuat.add(giaTriXuatMoi);
        this.giaTriTonKho = this.giaTriTonKho.subtract(
                this.giaNhapTrungBinh.multiply(new BigDecimal(soLuong)));

        // Cập nhật giá xuất trung bình
        if (this.soLanXuat > 0) {
            this.giaXuatTrungBinh = this.tongGiaTriXuat.divide(
                    new BigDecimal(this.soLanXuat + 1), 2, BigDecimal.ROUND_HALF_UP);
        } else {
            this.giaXuatTrungBinh = giaXuat;
        }

        this.soLanXuat++;
        capNhatSoLuongKhaDung();

        return true;
    }

    /**
     * Đặt hàng (tạm giữ số lượng)
     *
     * @param soLuong số lượng đặt
     * @return true nếu đặt thành công, false nếu không đủ hàng
     */
    public boolean datHang(Integer soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng đặt phải lớn hơn 0");
        }

        if (this.soLuongKhaDung < soLuong) {
            return false;
        }

        this.soLuongDat += soLuong;
        capNhatSoLuongKhaDung();

        return true;
    }

    /**
     * Hủy đặt hàng
     *
     * @param soLuong số lượng hủy đặt
     * @return true nếu hủy thành công, false nếu không hợp lệ
     */
    public boolean huyDatHang(Integer soLuong) {
        if (soLuong <= 0 || soLuong > this.soLuongDat) {
            return false;
        }

        this.soLuongDat -= soLuong;
        capNhatSoLuongKhaDung();

        return true;
    }

    /**
     * Chờ xuất (tạm giữ số lượng chờ xuất)
     *
     * @param soLuong số lượng chờ xuất
     * @return true nếu thành công, false nếu không đủ hàng
     */
    public boolean choXuat(Integer soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng chờ xuất phải lớn hơn 0");
        }

        if (this.soLuongKhaDung < soLuong) {
            return false;
        }

        this.soLuongChoXuat += soLuong;
        capNhatSoLuongKhaDung();

        return true;
    }

    /**
     * Hủy chờ xuất
     *
     * @param soLuong số lượng hủy chờ xuất
     * @return true nếu hủy thành công, false nếu không hợp lệ
     */
    public boolean huyChoXuat(Integer soLuong) {
        if (soLuong <= 0 || soLuong > this.soLuongChoXuat) {
            return false;
        }

        this.soLuongChoXuat -= soLuong;
        capNhatSoLuongKhaDung();

        return true;
    }

    /**
     * Cập nhật số lượng khả dụng
     */
    private void capNhatSoLuongKhaDung() {
        this.soLuongKhaDung = this.soLuongTon - this.soLuongDat - this.soLuongChoXuat;
        if (this.soLuongKhaDung < 0) {
            this.soLuongKhaDung = 0;
        }
    }

    /**
     * Kiểm kê kho
     *
     * @param soLuongThucTe số lượng thực tế
     * @param nguoiKiemKe người kiểm kê
     * @param ketQua kết quả kiểm kê
     */
    public void kiemKe(Integer soLuongThucTe, String nguoiKiemKe, String ketQua) {
        int chenhLech = soLuongThucTe - this.soLuongTon;

        if (chenhLech != 0) {
            // Điều chỉnh số lượng tồn
            this.soLuongTon = soLuongThucTe;

            // Điều chỉnh giá trị tồn kho
            if (this.soLuongTon > 0) {
                this.giaTriTonKho = this.giaNhapTrungBinh.multiply(new BigDecimal(this.soLuongTon));
            } else {
                this.giaTriTonKho = BigDecimal.ZERO;
            }
        }

        this.lanKiemKeCuoi = LocalDateTime.now();
        this.nguoiKiemKeCuoi = nguoiKiemKe;
        this.ketQuaKiemKeCuoi = ketQua;

        capNhatSoLuongKhaDung();
    }

    /**
     * Tính tỷ lệ quay vòng tồn kho
     *
     * @return tỷ lệ quay vòng
     */
    public double tinhTyLeQuayVongTonKho() {
        if (soLuongTon == 0) return 0.0;
        return (double) soLanXuat / soLuongTon;
    }

    /**
     * Tính tỷ lệ sử dụng kho
     *
     * @return tỷ lệ sử dụng (%)
     */
    public double tinhTyLeSuDungKho() {
        if (dienTichKho == null || dienTichKho.equals(BigDecimal.ZERO)) return 0.0;

        // Giả sử mỗi đơn vị vật tư chiếm 1m2 (có thể tùy chỉnh)
        BigDecimal dienTichSuDung = new BigDecimal(soLuongTon);
        return dienTichSuDung.divide(dienTichKho, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100)).doubleValue();
    }

    /**
     * Tính lợi nhuận từ kho
     *
     * @return lợi nhuận
     */
    public BigDecimal tinhLoiNhuan() {
        return tongGiaTriXuat.subtract(tongGiaTriNhap);
    }

    /**
     * Kiểm tra kho có đầy không
     *
     * @return true nếu đầy, false nếu không
     */
    public boolean khoDay() {
        if (dienTichKho == null) return false;
        return tinhTyLeSuDungKho() >= 95.0;
    }

    /**
     * Kiểm tra kho có trống không
     *
     * @return true nếu trống, false nếu không
     */
    public boolean khoTrong() {
        return soLuongTon == 0;
    }

    /**
     * Kiểm tra điều kiện bảo quản
     *
     * @param nhietDoHienTai nhiệt độ hiện tại
     * @param doAmHienTai độ ẩm hiện tại
     * @return true nếu đạt yêu cầu, false nếu không
     */
    public boolean kiemTraDieuKienBaoQuan(BigDecimal nhietDoHienTai, BigDecimal doAmHienTai) {
        boolean nhietDoOK = true;
        boolean doAmOK = true;

        if (nhietDoBaoQuanMin != null && nhietDoHienTai.compareTo(nhietDoBaoQuanMin) < 0) {
            nhietDoOK = false;
        }
        if (nhietDoBaoQuanMax != null && nhietDoHienTai.compareTo(nhietDoBaoQuanMax) > 0) {
            nhietDoOK = false;
        }

        if (doAmBaoQuanMin != null && doAmHienTai.compareTo(doAmBaoQuanMin) < 0) {
            doAmOK = false;
        }
        if (doAmBaoQuanMax != null && doAmHienTai.compareTo(doAmBaoQuanMax) > 0) {
            doAmOK = false;
        }

        return nhietDoOK && doAmOK;
    }

    /**
     * Lấy trạng thái kho
     *
     * @return trạng thái kho
     */
    public String getTrangThaiKho() {
        if (!trangThaiHoatDong) return "Ngưng hoạt động";
        if (khoTrong()) return "Trống";
        if (khoDay()) return "Đầy";
        if (soLuongKhaDung == 0) return "Hết hàng khả dụng";
        return "Hoạt động bình thường";
    }

    /**
     * Lấy loại kho
     *
     * @return loại kho
     */
    public String getLoaiKho() {
        if (khoChinh) return "Kho chính";
        if (khoTam) return "Kho tạm";
        if (khoKiemDinh) return "Kho kiểm định";
        if (khoHong) return "Kho hỏng";
        return "Kho thường";
    }

    /**
     * Lấy thông tin tóm tắt kho
     *
     * @return thông tin tóm tắt
     */
    public String getThongTinTomTat() {
        return String.format("%s - %s: %d/%d (%d khả dụng)",
                maKho, tenKho, soLuongTon,
                vatTu != null ? vatTu.getSoLuongTonToiDa() : 0,
                soLuongKhaDung);
    }

    /**
     * Lấy cảnh báo kho
     *
     * @return danh sách cảnh báo
     */
    public String getCanhBaoKho() {
        StringBuilder canhBao = new StringBuilder();

        if (khoTrong()) {
            canhBao.append("Kho trống; ");
        }
        if (khoDay()) {
            canhBao.append("Kho đầy; ");
        }
        if (soLuongKhaDung == 0 && soLuongTon > 0) {
            canhBao.append("Không có hàng khả dụng; ");
        }
        if (vatTu != null && vatTu.thieuHang()) {
            canhBao.append("Vật tư thiếu hàng; ");
        }

        return canhBao.toString();
    }

    /**
     * Lấy hiệu suất kho
     *
     * @return hiệu suất (%)
     */
    public double getHieuSuatKho() {
        if (soLanNhap + soLanXuat == 0) return 0.0;

        double tyLeQuayVong = tinhTyLeQuayVongTonKho();
        double tyLeSuDung = tinhTyLeSuDungKho();

        // Công thức tính hiệu suất: (tỷ lệ quay vòng * 0.7) + (tỷ lệ sử dụng * 0.3)
        return (tyLeQuayVong * 0.7) + (tyLeSuDung * 0.3);
    }

    /**
     * Lấy thông tin chi tiết kho
     *
     * @return thông tin chi tiết
     */
    public String getThongTinChiTiet() {
        StringBuilder sb = new StringBuilder();
        sb.append("Mã kho: ").append(maKho).append("\n");
        sb.append("Tên kho: ").append(tenKho).append("\n");
        sb.append("Vị trí: ").append(viTri != null ? viTri : "Không xác định").append("\n");
        sb.append("Loại kho: ").append(getLoaiKho()).append("\n");
        sb.append("Trạng thái: ").append(getTrangThaiKho()).append("\n");
        sb.append("Số lượng tồn: ").append(soLuongTon).append("\n");
        sb.append("Số lượng khả dụng: ").append(soLuongKhaDung).append("\n");
        sb.append("Giá trị tồn kho: ").append(String.format("%,.0f", giaTriTonKho)).append("\n");
        sb.append("Hiệu suất kho: ").append(String.format("%.1f%%", getHieuSuatKho())).append("\n");

        if (yeuCauBaoQuanDacBiet != null && !yeuCauBaoQuanDacBiet.trim().isEmpty()) {
            sb.append("Yêu cầu bảo quản: ").append(yeuCauBaoQuanDacBiet).append("\n");
        }

        return sb.toString();
    }

    /**
     * Kiểm tra có thể thực hiện giao dịch không
     *
     * @return true nếu có thể, false nếu không
     */
    public boolean coTheGiaoDich() {
        return trangThaiHoatDong && vatTu != null && vatTu.getTrangThaiHoatDong();
    }

    /**
     * Tính dung lượng còn lại
     *
     * @return dung lượng còn lại (%)
     */
    public double tinhDungLuongConLai() {
        return 100.0 - tinhTyLeSuDungKho();
    }

    /**
     * Lấy mức độ ưu tiên bổ sung
     *
     * @return mức độ ưu tiên (1-5)
     */
    public int getMucDoUuTienBoSung() {
        if (khoTrong()) return 5; // Ưu tiên cao nhất
        if (vatTu != null && vatTu.thieuHang()) return 4;
        if (soLuongKhaDung < 10) return 3;
        if (tinhTyLeSuDungKho() > 80) return 2;
        return 1; // Ưu tiên thấp
    }

    /**
     * Lấy số ngày từ lần kiểm kê cuối
     *
     * @return số ngày
     */
    public long getSoNgayTuKiemKeCuoi() {
        if (lanKiemKeCuoi == null) return Long.MAX_VALUE;
        return java.time.temporal.ChronoUnit.DAYS.between(lanKiemKeCuoi, LocalDateTime.now());
    }

    /**
     * Kiểm tra cần kiểm kê không
     *
     * @param chuKyKiemKe chu kỳ kiểm kê (ngày)
     * @return true nếu cần kiểm kê, false nếu không
     */
    public boolean canKiemKe(int chuKyKiemKe) {
        return getSoNgayTuKiemKeCuoi() >= chuKyKiemKe;
    }

    /**
     * Sao chép kho cho vật tư khác
     *
     * @param vatTuMoi vật tư mới
     * @return kho mới
     */
    public KhoVatTu saoChepChoVatTu(VatTu vatTuMoi) {
        KhoVatTu khoMoi = new KhoVatTu();
        khoMoi.setMaKho(this.maKho);
        khoMoi.setTenKho(this.tenKho);
        khoMoi.setMoTa(this.moTa);
        khoMoi.setViTri(this.viTri);
        khoMoi.setDienTichKho(this.dienTichKho);
        khoMoi.setDungTichKho(this.dungTichKho);
        khoMoi.setTrongLuongToiDa(this.trongLuongToiDa);
        khoMoi.setNhietDoBaoQuanMin(this.nhietDoBaoQuanMin);
        khoMoi.setNhietDoBaoQuanMax(this.nhietDoBaoQuanMax);
        khoMoi.setDoAmBaoQuanMin(this.doAmBaoQuanMin);
        khoMoi.setDoAmBaoQuanMax(this.doAmBaoQuanMax);
        khoMoi.setYeuCauBaoQuanDacBiet(this.yeuCauBaoQuanDacBiet);
        khoMoi.setKhoChinh(this.khoChinh);
        khoMoi.setKhoTam(this.khoTam);
        khoMoi.setKhoKiemDinh(this.khoKiemDinh);
        khoMoi.setKhoHong(this.khoHong);
        khoMoi.setThuKho(this.thuKho);
        khoMoi.setVatTu(vatTuMoi);

        return khoMoi;
    }

    /**
     * Các loại kho
     */
    public static final String LOAI_KHO_CHINH = "KHO_CHINH";
    public static final String LOAI_KHO_TAM = "KHO_TAM";
    public static final String LOAI_KHO_KIEM_DINH = "KHO_KIEM_DINH";
    public static final String LOAI_KHO_HONG = "KHO_HONG";
    public static final String LOAI_KHO_THUONG = "KHO_THUONG";
}