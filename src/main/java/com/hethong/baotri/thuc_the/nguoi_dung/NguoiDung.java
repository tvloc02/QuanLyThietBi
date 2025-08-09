package com.hethong.baotri.thuc_the.nguoi_dung;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Thực thể người dùng trong hệ thống
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "nguoi_dung")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // ✅ CHỈ SỬ DỤNG ID
@ToString(exclude = {"matKhau", "vaiTroSet", "doiBaoTri"}) // ✅ LOẠI BỎ circular references
public class NguoiDung implements UserDetails {

    private static final Logger log = LoggerFactory.getLogger(NguoiDung.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nguoi_dung")
    @EqualsAndHashCode.Include // ✅ CHỈ SỬ DỤNG ID cho equals/hashCode
    private Long idNguoiDung;

    // Thêm alias getId()
    public Long getId() {
        return getIdNguoiDung();
    }

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    @Column(name = "ten_dang_nhap", unique = true, nullable = false, length = 50)
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Column(name = "mat_khau", nullable = false)
    @JsonIgnore // ✅ KHÔNG trả về password trong JSON
    private String matKhau;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    @Column(name = "ho_va_ten", nullable = false, length = 100)
    private String hoVaTen;

    @Email(message = "Email không đúng định dạng")
    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự")
    @Column(name = "dia_chi", length = 200)
    private String diaChi;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    private LocalDateTime ngayCapNhat;

    @Column(name = "lan_dang_nhap_cuoi")
    private LocalDateTime lanDangNhapCuoi;

    @Column(name = "trang_thai_hoat_dong", nullable = false)
    private Boolean trangThaiHoatDong = true;

    @Column(name = "tai_khoan_khong_het_han", nullable = false)
    private Boolean taiKhoanKhongHetHan = true;

    @Column(name = "tai_khoan_khong_bi_khoa", nullable = false)
    private Boolean taiKhoanKhongBiKhoa = true;

    @Column(name = "thong_tin_dang_nhap_hop_le", nullable = false)
    private Boolean thongTinDangNhapHopLe = true;

    @Column(name = "so_lan_dang_nhap_that_bai", nullable = false)
    private Integer soLanDangNhapThatBai = 0;

    @Column(name = "thoi_gian_khoa_tai_khoan")
    private LocalDateTime thoiGianKhoaTaiKhoan;

    // ✅ Quan hệ Many-to-Many với VaiTro - SỬA ĐỂ TRÁNH CIRCULAR REFERENCE
    @ManyToMany(fetch = FetchType.LAZY) // ✅ ĐỔI THÀNH LAZY
    @JoinTable(
            name = "nguoi_dung_vai_tro",
            joinColumns = @JoinColumn(name = "id_nguoi_dung"),
            inverseJoinColumns = @JoinColumn(name = "id_vai_tro")
    )
    private Set<VaiTro> vaiTroSet = new HashSet<>();

    // ✅ Quan hệ với DoiBaoTri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_doi_bao_tri")
    @JsonIgnore // ✅ TRÁNH JSON circular reference
    private com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri doiBaoTri;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        ngayCapNhat = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngayCapNhat = LocalDateTime.now();
    }

    // ✅ HELPER METHODS - KHÔNG SỬ DỤNG RELATIONSHIPS TRỰC TIẾP
    public void themVaiTro(VaiTro vaiTro) {
        if (this.vaiTroSet == null) {
            this.vaiTroSet = new HashSet<>();
        }
        this.vaiTroSet.add(vaiTro);
        // ✅ KHÔNG gọi vaiTro.getNguoiDungSet().add(this) để tránh circular reference
    }

    public void xoaVaiTro(VaiTro vaiTro) {
        if (this.vaiTroSet != null) {
            this.vaiTroSet.remove(vaiTro);
        }
        // ✅ KHÔNG gọi vaiTro.getNguoiDungSet().remove(this) để tránh circular reference
    }

    public boolean coVaiTro(String tenVaiTro) {
        if (vaiTroSet == null) return false;
        return vaiTroSet.stream()
                .anyMatch(vaiTro -> vaiTro.getTenVaiTro().equals(tenVaiTro));
    }

    /**
     * Cập nhật thông tin đăng nhập cuối
     */
    public void capNhatLanDangNhapCuoi() {
        this.lanDangNhapCuoi = LocalDateTime.now();
        this.soLanDangNhapThatBai = 0;
    }

    /**
     * Tăng số lần đăng nhập thất bại
     */
    public void tangSoLanDangNhapThatBai() {
        this.soLanDangNhapThatBai++;
        if (this.soLanDangNhapThatBai >= 5) {
            this.taiKhoanKhongBiKhoa = false;
            this.thoiGianKhoaTaiKhoan = LocalDateTime.now().plusMinutes(15);
        }
    }

    /**
     * Mở khóa tài khoản
     */
    public void moKhoaTaiKhoan() {
        this.taiKhoanKhongBiKhoa = true;
        this.soLanDangNhapThatBai = 0;
        this.thoiGianKhoaTaiKhoan = null;
    }

    // ✅ IMPLEMENT UserDetails interface - SỬA ĐỂ TRÁNH LAZY LOADING ISSUES
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        try {
            if (vaiTroSet != null) {
                for (VaiTro vaiTro : vaiTroSet) {
                    // Thêm ROLE_
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + vaiTro.getTenVaiTro()));

                    // ✅ SỬA: Kiểm tra null và tránh lazy loading issues
                    try {
                        if (vaiTro.getQuyenSet() != null) {
                            for (Quyen quyen : vaiTro.getQuyenSet()) {
                                authorities.add(new SimpleGrantedAuthority(quyen.getTenQuyen()));
                            }
                        }
                    } catch (Exception e) {
                        log.warn("⚠️ Cannot load quyenSet for vaiTro: {}, error: {}",
                                vaiTro.getTenVaiTro(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("❌ Error loading authorities for user: {}, error: {}",
                    tenDangNhap, e.getMessage());
        }

        log.debug("User {} has authorities: {}", tenDangNhap, authorities);
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.matKhau;
    }

    @Override
    public String getUsername() {
        return this.tenDangNhap;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Boolean.TRUE.equals(this.taiKhoanKhongHetHan);
    }

    @Override
    public boolean isAccountNonLocked() {
        if (thoiGianKhoaTaiKhoan != null && thoiGianKhoaTaiKhoan.isAfter(LocalDateTime.now())) {
            return false;
        }
        return Boolean.TRUE.equals(this.taiKhoanKhongBiKhoa);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return Boolean.TRUE.equals(this.thongTinDangNhapHopLe);
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.trangThaiHoatDong);
    }

    // ✅ HELPER METHODS
    public boolean isActive() {
        return Boolean.TRUE.equals(trangThaiHoatDong);
    }

    public boolean isLocked() {
        return Boolean.FALSE.equals(taiKhoanKhongBiKhoa);
    }
}