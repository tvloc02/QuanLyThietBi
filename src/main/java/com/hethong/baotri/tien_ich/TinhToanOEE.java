package com.hethong.baotri.tien_ich;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Slf4j
public class TinhToanOEE {

    public BigDecimal tinhOEE(BigDecimal khaDung, BigDecimal hienSuat, BigDecimal chatLuong) {
        if (khaDung == null || hienSuat == null || chatLuong == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal oee = khaDung.multiply(hienSuat).multiply(chatLuong);
        return oee.setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal tinhTyLeKhaDung(long thoiGianHoatDong, long thoiGianLenKe) {
        if (thoiGianLenKe == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal khaDung = BigDecimal.valueOf(thoiGianHoatDong)
                .divide(BigDecimal.valueOf(thoiGianLenKe), 4, RoundingMode.HALF_UP);

        return khaDung.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal tinhHienSuat(long sanLuongThucTe, long sanLuongTieuChuan) {
        if (sanLuongTieuChuan == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal hienSuat = BigDecimal.valueOf(sanLuongThucTe)
                .divide(BigDecimal.valueOf(sanLuongTieuChuan), 4, RoundingMode.HALF_UP);

        return hienSuat.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal tinhChatLuong(long sanPhamTot, long tongSanPham) {
        if (tongSanPham == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal chatLuong = BigDecimal.valueOf(sanPhamTot)
                .divide(BigDecimal.valueOf(tongSanPham), 4, RoundingMode.HALF_UP);

        return chatLuong.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public String phanLoaiOEE(BigDecimal oee) {
        if (oee.compareTo(BigDecimal.valueOf(85)) >= 0) {
            return "Xuất sắc";
        } else if (oee.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "Tốt";
        } else if (oee.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "Trung bình";
        } else if (oee.compareTo(BigDecimal.valueOf(40)) >= 0) {
            return "Thấp";
        } else {
            return "Rất thấp";
        }
    }

    public String goiYCaiTien(BigDecimal khaDung, BigDecimal hienSuat, BigDecimal chatLuong) {
        StringBuilder goiY = new StringBuilder();

        if (khaDung.compareTo(BigDecimal.valueOf(90)) < 0) {
            goiY.append("Cải thiện tỷ lệ khả dụng: giảm thời gian dừng máy, bảo trì định kỳ; ");
        }

        if (hienSuat.compareTo(BigDecimal.valueOf(95)) < 0) {
            goiY.append("Cải thiện hiệu suất: tối ưu hóa quy trình, đào tạo nhân viên; ");
        }

        if (chatLuong.compareTo(BigDecimal.valueOf(99)) < 0) {
            goiY.append("Cải thiện chất lượng: kiểm soát chất lượng, cải tiến quy trình; ");
        }

        return goiY.toString();
    }
}