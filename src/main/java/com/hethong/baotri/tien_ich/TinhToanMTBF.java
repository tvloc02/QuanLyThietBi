package com.hethong.baotri.tien_ich;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class TinhToanMTBF {

    public BigDecimal tinhMTBF(long tongThoiGianHoatDong, int soLanHong) {
        if (soLanHong == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal mtbf = BigDecimal.valueOf(tongThoiGianHoatDong)
                .divide(BigDecimal.valueOf(soLanHong), 2, RoundingMode.HALF_UP);

        return mtbf;
    }

    public BigDecimal tinhMTBFTheoGio(long tongGioHoatDong, int soLanHong) {
        return tinhMTBF(tongGioHoatDong, soLanHong);
    }

    public BigDecimal tinhMTBFTheoNgay(long tongNgayHoatDong, int soLanHong) {
        return tinhMTBF(tongNgayHoatDong, soLanHong);
    }

    public BigDecimal tinhMTBFTheoKhoangThoiGian(LocalDateTime batDau, LocalDateTime ketThuc, int soLanHong) {
        if (batDau == null || ketThuc == null || soLanHong == 0) {
            return BigDecimal.ZERO;
        }

        Duration duration = Duration.between(batDau, ketThuc);
        long tongGio = duration.toHours();

        return tinhMTBF(tongGio, soLanHong);
    }

    public String phanLoaiMTBF(BigDecimal mtbf) {
        if (mtbf.compareTo(BigDecimal.valueOf(1000)) >= 0) {
            return "Rất tốt";
        } else if (mtbf.compareTo(BigDecimal.valueOf(500)) >= 0) {
            return "Tốt";
        } else if (mtbf.compareTo(BigDecimal.valueOf(200)) >= 0) {
            return "Trung bình";
        } else if (mtbf.compareTo(BigDecimal.valueOf(100)) >= 0) {
            return "Thấp";
        } else {
            return "Rất thấp";
        }
    }

    public BigDecimal tinhTyLeKhaDung(BigDecimal mtbf, BigDecimal mttr) {
        if (mtbf.add(mttr).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal tyLeKhaDung = mtbf.divide(mtbf.add(mttr), 4, RoundingMode.HALF_UP);
        return tyLeKhaDung.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal duDoanLanHongTiepTheo(BigDecimal mtbf, LocalDateTime lanHongCuoi) {
        if (mtbf.compareTo(BigDecimal.ZERO) == 0 || lanHongCuoi == null) {
            return BigDecimal.ZERO;
        }

        LocalDateTime duDoanLanHongTiepTheo = lanHongCuoi.plusHours(mtbf.longValue());
        Duration duration = Duration.between(LocalDateTime.now(), duDoanLanHongTiepTheo);

        return BigDecimal.valueOf(duration.toHours());
    }

    public String goiYCaiTienMTBF(BigDecimal mtbf) {
        if (mtbf.compareTo(BigDecimal.valueOf(500)) < 0) {
            return "Cần cải thiện: tăng cường bảo trì định kỳ, thay thế linh kiện già, đào tạo vận hành";
        } else if (mtbf.compareTo(BigDecimal.valueOf(1000)) < 0) {
            return "Có thể cải thiện: tối ưu hóa quy trình bảo trì, theo dõi sát sao các thông số";
        } else {
            return "Duy trì hiện tại: tiếp tục thực hiện bảo trì định kỳ";
        }
    }
}