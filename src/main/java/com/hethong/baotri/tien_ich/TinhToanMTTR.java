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
public class TinhToanMTTR {

    public BigDecimal tinhMTTR(long tongThoiGianSuaChua, int soLanSuaChua) {
        if (soLanSuaChua == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal mttr = BigDecimal.valueOf(tongThoiGianSuaChua)
                .divide(BigDecimal.valueOf(soLanSuaChua), 2, RoundingMode.HALF_UP);

        return mttr;
    }

    public BigDecimal tinhMTTRTheoPhut(long tongPhutSuaChua, int soLanSuaChua) {
        return tinhMTTR(tongPhutSuaChua, soLanSuaChua);
    }

    public BigDecimal tinhMTTRTheoGio(long tongGioSuaChua, int soLanSuaChua) {
        return tinhMTTR(tongGioSuaChua, soLanSuaChua);
    }

    public BigDecimal tinhMTTRTheoKhoangThoiGian(List<Duration> danhSachThoiGianSuaChua) {
        if (danhSachThoiGianSuaChua == null || danhSachThoiGianSuaChua.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long tongPhut = danhSachThoiGianSuaChua.stream()
                .mapToLong(Duration::toMinutes)
                .sum();

        return tinhMTTR(tongPhut, danhSachThoiGianSuaChua.size());
    }

    public String phanLoaiMTTR(BigDecimal mttr) {
        if (mttr.compareTo(BigDecimal.valueOf(60)) <= 0) {
            return "Rất tốt";
        } else if (mttr.compareTo(BigDecimal.valueOf(120)) <= 0) {
            return "Tốt";
        } else if (mttr.compareTo(BigDecimal.valueOf(240)) <= 0) {
            return "Trung bình";
        } else if (mttr.compareTo(BigDecimal.valueOf(480)) <= 0) {
            return "Thấp";
        } else {
            return "Rất thấp";
        }
    }

    public BigDecimal tinhTyLeDowntime(BigDecimal mttr, BigDecimal mtbf) {
        if (mtbf.add(mttr).compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal downtime = mttr.divide(mtbf.add(mttr), 4, RoundingMode.HALF_UP);
        return downtime.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal duDoanThoiGianSuaChua(String loaiLoi, BigDecimal mttrTrungBinh) {
        // Hệ số điều chỉnh theo loại lỗi
        BigDecimal heSo = switch (loaiLoi) {
            case "LOI_DON_GIAN" -> BigDecimal.valueOf(0.5);
            case "LOI_TRUNG_BINH" -> BigDecimal.valueOf(1.0);
            case "LOI_PHUC_TAP" -> BigDecimal.valueOf(1.5);
            case "LOI_NGHIEM_TRONG" -> BigDecimal.valueOf(2.0);
            default -> BigDecimal.valueOf(1.0);
        };

        return mttrTrungBinh.multiply(heSo).setScale(0, RoundingMode.HALF_UP);
    }

    public String goiYCaiTienMTTR(BigDecimal mttr) {
        if (mttr.compareTo(BigDecimal.valueOf(240)) > 0) {
            return "Cần cải thiện: chuẩn bị sẵn phụ tùng, đào tạo kỹ thuật viên, cải tiến quy trình sửa chữa";
        } else if (mttr.compareTo(BigDecimal.valueOf(120)) > 0) {
            return "Có thể cải thiện: tối ưu hóa quy trình chẩn đoán, chuẩn bị công cụ";
        } else {
            return "Duy trì hiện tại: tiếp tục theo dõi và cải tiến";
        }
    }

    public BigDecimal tinhChiPhiDowntime(BigDecimal mttr, BigDecimal chiPhiMoiGio) {
        if (mttr == null || chiPhiMoiGio == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal gioDowntime = mttr.divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
        return gioDowntime.multiply(chiPhiMoiGio).setScale(0, RoundingMode.HALF_UP);
    }
}
