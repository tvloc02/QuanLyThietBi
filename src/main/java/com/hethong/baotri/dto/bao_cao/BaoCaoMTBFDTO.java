package com.hethong.baotri.dto.bao_cao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaoCaoMTBFDTO {
    private Long idThietBi;
    private String tenThietBi;
    private BigDecimal mtbf;
    private BigDecimal mtbfTrungBinh;
    private String phanLoai;
    private LocalDateTime tuNgay;
    private LocalDateTime denNgay;
    private Integer soLanHong;
    private Long tongThoiGianHoatDong;
}