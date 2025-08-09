package com.hethong.baotri.dto.nguoi_dung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VaiTroDTO {

    private Long idVaiTro;

    @NotBlank(message = "Tên vai trò không được để trống")
    @Size(max = 50, message = "Tên vai trò không được vượt quá 50 ký tự")
    private String tenVaiTro;

    @Size(max = 200, message = "Mô tả vai trò không được vượt quá 200 ký tự")
    private String moTa;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Boolean trangThaiHoatDong;
    private Set<QuyenDTO> quyenSet;
    private Integer soLuongNguoiDung;
}