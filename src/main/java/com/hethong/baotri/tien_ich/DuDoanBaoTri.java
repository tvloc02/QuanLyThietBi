package com.hethong.baotri.tien_ich;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DuDoanBaoTri {

    private final TinhToanMTBF tinhToanMTBF;
    private final TinhToanMTTR tinhToanMTTR;

    public LocalDateTime duDoanNgayBaoTriTiepTheo(LocalDateTime lanBaoTriCuoi,
                                                  BigDecimal mtbf,
                                                  Integer chuKyBaoTriDinhKy) {
        if (lanBaoTriCuoi == null) {
            return LocalDateTime.now().plusDays(chuKyBaoTriDinhKy != null ? chuKyBaoTriDinhKy : 30);
        }

        // Dự đoán dựa trên MTBF
        LocalDateTime duDoanTheoMTBF = lanBaoTriCuoi.plusHours(mtbf.longValue());

        // Dự đoán dựa trên chu kỳ định kỳ
        LocalDateTime duDoanTheoChuKy = lanBaoTriCuoi.plusDays(chuKyBaoTriDinhKy != null ? chuKyBaoTriDinhKy : 30);

        // Chọn ngày gần nhất
        return duDoanTheoMTBF.isBefore(duDoanTheoChuKy) ? duDoanTheoMTBF : duDoanTheoChuKy;
    }

    public int tinhMucDoUuTienBaoTri(BigDecimal mtbf,
                                     LocalDateTime lanBaoTriCuoi,
                                     Boolean thieuPhuTung,
                                     Boolean coAnhHuongSanXuat) {
        int mucDoUuTien = 1;

        // Dựa trên MTBF
        if (mtbf.compareTo(BigDecimal.valueOf(100)) < 0) {
            mucDoUuTien += 2;
        } else if (mtbf.compareTo(BigDecimal.valueOf(500)) < 0) {
            mucDoUuTien += 1;
        }

        // Dựa trên thời gian từ lần bảo trì cuối
        if (lanBaoTriCuoi != null) {
            long ngayTuBaoTriCuoi = ChronoUnit.DAYS.between(lanBaoTriCuoi, LocalDateTime.now());
            if (ngayTuBaoTriCuoi > 30) {
                mucDoUuTien += 1;
            }
            if (ngayTuBaoTriCuoi > 60) {
                mucDoUuTien += 1;
            }
        }

        // Dựa trên tình trạng phụ tùng
        if (Boolean.TRUE.equals(thieuPhuTung)) {
            mucDoUuTien += 1;
        }

        // Dựa trên ảnh hưởng sản xuất
        if (Boolean.TRUE.equals(coAnhHuongSanXuat)) {
            mucDoUuTien += 2;
        }

        return Math.min(mucDoUuTien, 5);
    }

    public List<String> duDoanVatTuCanThiet(String loaiThietBi, String loaiBaoTri) {
        List<String> vatTuCanThiet = new ArrayList<>();

        if ("BAO_TRI_DINH_KY".equals(loaiBaoTri)) {
            vatTuCanThiet.add("Dầu bôi trơn");
            vatTuCanThiet.add("Bộ lọc");
            vatTuCanThiet.add("Vật liệu làm sạch");
        } else if ("SUA_CHUA_KHAN_CAP".equals(loaiBaoTri)) {
            vatTuCanThiet.add("Phụ tùng thay thế");
            vatTuCanThiet.add("Vật liệu hàn");
            vatTuCanThiet.add("Linh kiện điện");
        }

        // Thêm vật tư theo loại thiết bị
        switch (loaiThietBi) {
            case "MAY_SAN_XUAT" -> {
                vatTuCanThiet.add("Dây curoa");
                vatTuCanThiet.add("Vòng bi");
                vatTuCanThiet.add("Dầu thủy lực");
            }
            case "THIET_BI_DIEN" -> {
                vatTuCanThiet.add("Cầu chì");
                vatTuCanThiet.add("Rơ le");
                vatTuCanThiet.add("Dây dẫn");
            }
            case "THIET_BI_CO_KHI" -> {
                vatTuCanThiet.add("Ốc vít");
                vatTuCanThiet.add("Gasket");
                vatTuCanThiet.add("Mỡ bôi trơn");
            }
        }

        return vatTuCanThiet;
    }

    public BigDecimal duDoanChiPhiBaoTri(String loaiBaoTri,
                                         BigDecimal giaTriThietBi,
                                         Integer soGioLamViec) {
        BigDecimal heSoChiPhi = switch (loaiBaoTri) {
            case "BAO_TRI_DINH_KY" -> BigDecimal.valueOf(0.02);
            case "SUA_CHUA_KHAN_CAP" -> BigDecimal.valueOf(0.05);
            case "THAY_THE_LINH_KIEN" -> BigDecimal.valueOf(0.03);
            case "KIEM_TRA_AN_TOAN" -> BigDecimal.valueOf(0.01);
            default -> BigDecimal.valueOf(0.02);
        };

        BigDecimal chiPhiCoBan = giaTriThietBi.multiply(heSoChiPhi);

        // Điều chỉnh theo số giờ làm việc
        if (soGioLamViec != null && soGioLamViec > 2000) {
            chiPhiCoBan = chiPhiCoBan.multiply(BigDecimal.valueOf(1.2));
        }

        return chiPhiCoBan.setScale(0, RoundingMode.HALF_UP);
    }

    public Integer duDoanThoiGianBaoTri(String loaiBaoTri,
                                        String loaiThietBi,
                                        Integer mucDoPhucTap) {
        Map<String, Integer> thoiGianCoBan = Map.of(
                "BAO_TRI_DINH_KY", 4,
                "SUA_CHUA_KHAN_CAP", 8,
                "THAY_THE_LINH_KIEN", 6,
                "KIEM_TRA_AN_TOAN", 2,
                "NAY_CAP_THIET_BI", 12
        );

        Integer thoiGian = thoiGianCoBan.getOrDefault(loaiBaoTri, 4);

        // Điều chỉnh theo loại thiết bị
        if ("THIET_BI_PHUC_TAP".equals(loaiThietBi)) {
            thoiGian = (int) (thoiGian * 1.5);
        }

        // Điều chỉnh theo mức độ phức tạp
        if (mucDoPhucTap != null) {
            thoiGian = thoiGian * mucDoPhucTap;
        }

        return thoiGian;
    }
}