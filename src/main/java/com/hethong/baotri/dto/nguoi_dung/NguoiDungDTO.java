package com.hethong.baotri.dto.nguoi_dung;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NguoiDungDTO {

    private Long idNguoiDung;

    // Thêm alias getId() để tương thích
    public Long getId() {
        return getIdNguoiDung();
    }

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String matKhau;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên không được vượt quá 100 ký tự")
    private String hoVaTen;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Size(max = 15, message = "Số điện thoại không được vượt quá 15 ký tự")
    private String soDienThoai;

    @Size(max = 200, message = "Địa chỉ không được vượt quá 200 ký tự")
    private String diaChi;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private LocalDateTime lanDangNhapCuoi;
    private Boolean trangThaiHoatDong;
    private Boolean taiKhoanKhongBiKhoa;
    private Boolean taiKhoanKhongHetHan;
    private Boolean thongTinDangNhapHopLe;
    private Integer soLanDangNhapThatBai;

    // Thông tin vai trò
    private Set<VaiTroDTO> vaiTroSet;
    private List<String> danhSachVaiTro; // Danh sách tên vai trò dạng string

    // Thông tin bổ sung
    private String tenDoiBaoTri;
    private String trangThaiText;
    private String cssClassTrangThai;

    // Các thông tin tính toán
    private Long soNgayTuKhiTao;
    private Long soNgayTuLanDangNhapCuoi;
    private Boolean canEdit;
    private Boolean canDelete;
    private Boolean canResetPassword;
    private String primaryRole; // Vai trò chính

    // Thông tin thời gian khóa tài khoản
    private LocalDateTime thoiGianKhoaTaiKhoan;

    // Constructor và utility methods
    public boolean isActive() {
        return Boolean.TRUE.equals(trangThaiHoatDong);
    }

    public boolean isLocked() {
        return Boolean.FALSE.equals(taiKhoanKhongBiKhoa);
    }

    public boolean isExpired() {
        return Boolean.FALSE.equals(taiKhoanKhongHetHan);
    }

    public boolean isCredentialsExpired() {
        return Boolean.FALSE.equals(thongTinDangNhapHopLe);
    }

    public String getDisplayName() {
        return hoVaTen != null ? hoVaTen : tenDangNhap;
    }

    public String getStatusText() {
        if (Boolean.FALSE.equals(trangThaiHoatDong)) {
            return "Không hoạt động";
        }
        if (Boolean.FALSE.equals(taiKhoanKhongBiKhoa)) {
            return "Bị khóa";
        }
        if (Boolean.FALSE.equals(taiKhoanKhongHetHan)) {
            return "Hết hạn";
        }
        return "Hoạt động";
    }

    public String getStatusCssClass() {
        if (Boolean.FALSE.equals(trangThaiHoatDong)) {
            return "badge bg-secondary";
        }
        if (Boolean.FALSE.equals(taiKhoanKhongBiKhoa)) {
            return "badge bg-danger";
        }
        if (Boolean.FALSE.equals(taiKhoanKhongHetHan)) {
            return "badge bg-warning";
        }
        return "badge bg-success";
    }
}