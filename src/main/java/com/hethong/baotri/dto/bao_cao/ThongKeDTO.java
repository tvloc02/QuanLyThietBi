package com.hethong.baotri.dto.bao_cao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeDTO {
    private String loaiThongKe;
    private LocalDateTime tuNgay;
    private LocalDateTime denNgay;
    private Map<String, Object> duLieu;
    private String moTa;
}