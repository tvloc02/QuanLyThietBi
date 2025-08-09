package com.hethong.baotri.dto.nguoi_dung;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuyenDTO {

    private Long idQuyen;

    @NotBlank(message = "Tên quyền không được để trống")
    @Size(max = 50, message = "Tên quyền không được vượt quá 50 ký tự")
    private String tenQuyen;

    @Size(max = 200, message = "Mô tả quyền không được vượt quá 200 ký tự")
    private String moTa;

    @NotBlank(message = "Nhóm quyền không được để trống")
    @Size(max = 50, message = "Nhóm quyền không được vượt quá 50 ký tự")
    private String nhomQuyen;

    private LocalDateTime ngayTao;
    private LocalDateTime ngayCapNhat;
    private Boolean trangThaiHoatDong;
}