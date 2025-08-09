package com.hethong.baotri.tien_ich;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class XuLyNgayThang {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public String formatNgay(LocalDate ngay) {
        return ngay != null ? ngay.format(DATE_FORMATTER) : "";
    }

    public String formatNgayGio(LocalDateTime ngayGio) {
        return ngayGio != null ? ngayGio.format(DATETIME_FORMATTER) : "";
    }

    public LocalDate parseNgay(String ngayString) {
        try {
            return LocalDate.parse(ngayString, DATE_FORMATTER);
        } catch (Exception e) {
            log.error("Lỗi parse ngày: {}", ngayString);
            return null;
        }
    }

    public LocalDateTime parseNgayGio(String ngayGioString) {
        try {
            return LocalDateTime.parse(ngayGioString, DATETIME_FORMATTER);
        } catch (Exception e) {
            log.error("Lỗi parse ngày giờ: {}", ngayGioString);
            return null;
        }
    }

    public long tinhSoNgay(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null) return 0;
        return ChronoUnit.DAYS.between(tuNgay, denNgay);
    }

    public long tinhSoGio(LocalDateTime tuNgay, LocalDateTime denNgay) {
        if (tuNgay == null || denNgay == null) return 0;
        return ChronoUnit.HOURS.between(tuNgay, denNgay);
    }

    public long tinhSoPhut(LocalDateTime tuNgay, LocalDateTime denNgay) {
        if (tuNgay == null || denNgay == null) return 0;
        return ChronoUnit.MINUTES.between(tuNgay, denNgay);
    }

    public boolean laNgayHienTai(LocalDate ngay) {
        return ngay != null && ngay.equals(LocalDate.now());
    }

    public boolean laTrongTuan(LocalDate ngay) {
        if (ngay == null) return false;
        LocalDate dauTuan = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate cuoiTuan = dauTuan.plusDays(6);
        return !ngay.isBefore(dauTuan) && !ngay.isAfter(cuoiTuan);
    }

    public boolean laTrongThang(LocalDate ngay) {
        if (ngay == null) return false;
        LocalDate dauThang = LocalDate.now().withDayOfMonth(1);
        LocalDate cuoiThang = dauThang.plusMonths(1).minusDays(1);
        return !ngay.isBefore(dauThang) && !ngay.isAfter(cuoiThang);
    }

    public boolean laTrongNam(LocalDate ngay) {
        if (ngay == null) return false;
        return ngay.getYear() == LocalDate.now().getYear();
    }

    public LocalDate ngayDauTuan() {
        return LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
    }

    public LocalDate ngayCuoiTuan() {
        return ngayDauTuan().plusDays(6);
    }

    public LocalDate ngayDauThang() {
        return LocalDate.now().withDayOfMonth(1);
    }

    public LocalDate ngayCuoiThang() {
        return ngayDauThang().plusMonths(1).minusDays(1);
    }

    public LocalDate ngayDauNam() {
        return LocalDate.now().withDayOfYear(1);
    }

    public LocalDate ngayCuoiNam() {
        return LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());
    }

    public List<LocalDate> layDanhSachNgayTrongKhoang(LocalDate tuNgay, LocalDate denNgay) {
        List<LocalDate> danhSachNgay = new ArrayList<>();
        if (tuNgay == null || denNgay == null) return danhSachNgay;

        LocalDate ngayHienTai = tuNgay;
        while (!ngayHienTai.isAfter(denNgay)) {
            danhSachNgay.add(ngayHienTai);
            ngayHienTai = ngayHienTai.plusDays(1);
        }
        return danhSachNgay;
    }

    public String moTaKhoangThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        if (tuNgay == null || denNgay == null) return "Không xác định";

        long soPhut = tinhSoPhut(tuNgay, denNgay);
        long soGio = soPhut / 60;
        long soNgay = soGio / 24;

        if (soNgay > 0) {
            return soNgay + " ngày" + (soGio % 24 > 0 ? " " + (soGio % 24) + " giờ" : "");
        } else if (soGio > 0) {
            return soGio + " giờ" + (soPhut % 60 > 0 ? " " + (soPhut % 60) + " phút" : "");
        } else {
            return soPhut + " phút";
        }
    }

    public String moTaKhoangCachNgay(LocalDate ngay) {
        if (ngay == null) return "Không xác định";

        long soNgay = tinhSoNgay(LocalDate.now(), ngay);

        if (soNgay == 0) {
            return "Hôm nay";
        } else if (soNgay == 1) {
            return "Ngày mai";
        } else if (soNgay == -1) {
            return "Hôm qua";
        } else if (soNgay > 0) {
            return "Còn " + soNgay + " ngày";
        } else {
            return "Đã qua " + Math.abs(soNgay) + " ngày";
        }
    }

    public boolean laNgayLamViec(LocalDate ngay) {
        if (ngay == null) return false;
        int dayOfWeek = ngay.getDayOfWeek().getValue();
        return dayOfWeek >= 1 && dayOfWeek <= 5; // Thứ 2 đến thứ 6
    }

    public boolean laCuoiTuan(LocalDate ngay) {
        if (ngay == null) return false;
        int dayOfWeek = ngay.getDayOfWeek().getValue();
        return dayOfWeek == 6 || dayOfWeek == 7; // Thứ 7 và Chủ nhật
    }

    public LocalDate ngayLamViecTiepTheo(LocalDate ngay) {
        if (ngay == null) return null;

        LocalDate ngayTiepTheo = ngay.plusDays(1);
        while (!laNgayLamViec(ngayTiepTheo)) {
            ngayTiepTheo = ngayTiepTheo.plusDays(1);
        }
        return ngayTiepTheo;
    }

    public LocalDate ngayLamViecTruoc(LocalDate ngay) {
        if (ngay == null) return null;

        LocalDate ngayTruoc = ngay.minusDays(1);
        while (!laNgayLamViec(ngayTruoc)) {
            ngayTruoc = ngayTruoc.minusDays(1);
        }
        return ngayTruoc;
    }

    public int tinhSoNgayLamViec(LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay == null || denNgay == null) return 0;

        int soNgayLamViec = 0;
        LocalDate ngayHienTai = tuNgay;

        while (!ngayHienTai.isAfter(denNgay)) {
            if (laNgayLamViec(ngayHienTai)) {
                soNgayLamViec++;
            }
            ngayHienTai = ngayHienTai.plusDays(1);
        }

        return soNgayLamViec;
    }

    public String getTenThu(LocalDate ngay) {
        if (ngay == null) return "Không xác định";

        return switch (ngay.getDayOfWeek().getValue()) {
            case 1 -> "Thứ Hai";
            case 2 -> "Thứ Ba";
            case 3 -> "Thứ Tư";
            case 4 -> "Thứ Năm";
            case 5 -> "Thứ Sáu";
            case 6 -> "Thứ Bảy";
            case 7 -> "Chủ Nhật";
            default -> "Không xác định";
        };
    }

    public String getTenThang(int thang) {
        return switch (thang) {
            case 1 -> "Tháng Một";
            case 2 -> "Tháng Hai";
            case 3 -> "Tháng Ba";
            case 4 -> "Tháng Tư";
            case 5 -> "Tháng Năm";
            case 6 -> "Tháng Sáu";
            case 7 -> "Tháng Bảy";
            case 8 -> "Tháng Tám";
            case 9 -> "Tháng Chín";
            case 10 -> "Tháng Mười";
            case 11 -> "Tháng Mười Một";
            case 12 -> "Tháng Mười Hai";
            default -> "Không xác định";
        };
    }

    public boolean laNamNhuan(int nam) {
        return (nam % 4 == 0 && nam % 100 != 0) || (nam % 400 == 0);
    }

    public int soNgayTrongThang(int thang, int nam) {
        return switch (thang) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> laNamNhuan(nam) ? 29 : 28;
            default -> 0;
        };
    }
}