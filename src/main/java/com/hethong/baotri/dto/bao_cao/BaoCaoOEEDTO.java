package com.hethong.baotri.dto.bao_cao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoOEEDTO {
    private Long idThietBi;
    private String tenThietBi;
    private BigDecimal oee;
    private BigDecimal khaDung;
    private BigDecimal hienSuat;
    private BigDecimal chatLuong;
    private LocalDateTime tuNgay;
    private LocalDateTime denNgay;
    private String phanLoai;
}