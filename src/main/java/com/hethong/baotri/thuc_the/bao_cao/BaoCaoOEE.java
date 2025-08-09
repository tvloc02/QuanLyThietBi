package com.hethong.baotri.thuc_the.bao_cao;

import com.hethong.baotri.kho_du_lieu.bao_cao.BaoCaoRepository;
import com.hethong.baotri.thuc_the.bao_cao.BaoCao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BaoCaoOEE {

    private final BaoCaoRepository baoCaoRepository;

    /**
     * Tạo báo cáo OEE (Overall Equipment Effectiveness)
     */
    public BaoCao taoBaoCaoOEE(String nguoiTao, LocalDateTime tuNgay, LocalDateTime denNgay, Long thietBiId) {
        log.info("Đang tạo báo cáo OEE cho thiết bị {} từ {} đến {}", thietBiId, tuNgay, denNgay);

        BaoCao baoCao = new BaoCao();
        baoCao.setTenBaoCao("Báo cáo OEE - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        baoCao.setLoaiBaoCao("OEE");
        baoCao.setMoTa("Báo cáo hiệu quả tổng thể thiết bị từ " +
                tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " đến " + denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        baoCao.setNguoiTao(nguoiTao);
        baoCao.setTrangThai("COMPLETED");

        // Tính toán OEE
        Map<String, Object> oeeData = tinhToanOEE(thietBiId, tuNgay, denNgay);
        String noiDung = taoNoiDungBaoCaoOEE(oeeData, thietBiId, tuNgay, denNgay);
        baoCao.setNoiDung(noiDung);

        return baoCaoRepository.save(baoCao);
    }

    /**
     * Tính toán OEE cho thiết bị
     */
    public Map<String, Object> tinhToanOEE(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu cơ bản
        double thoiGianLenKe = layThoiGianLenKe(thietBiId, tuNgay, denNgay);
        double thoiGianHoatDong = layThoiGianHoatDong(thietBiId, tuNgay, denNgay);
        double thoiGianSanXuat = layThoiGianSanXuat(thietBiId, tuNgay, denNgay);

        int sanLuongThucTe = laySanLuongThucTe(thietBiId, tuNgay, denNgay);
        int sanLuongKeHoach = laySanLuongKeHoach(thietBiId, tuNgay, denNgay);
        int sanPhamDat = laySanPhamDat(thietBiId, tuNgay, denNgay);

        // Tính 3 thành phần của OEE
        double availability = thoiGianLenKe > 0 ? (thoiGianHoatDong / thoiGianLenKe) * 100 : 0;
        double performance = sanLuongKeHoach > 0 ? ((double) sanLuongThucTe / sanLuongKeHoach) * 100 : 0;
        double quality = sanLuongThucTe > 0 ? ((double) sanPhamDat / sanLuongThucTe) * 100 : 0;

        // Tính OEE tổng thể
        double oee = (availability * performance * quality) / 10000;

        // Phân loại mức độ OEE
        String xepLoai = xepLoaiOEE(oee);
        String mucDoHieuQua = danhGiaMucDoHieuQua(oee);

        result.put("thietBiId", thietBiId);
        result.put("tenThietBi", layTenThietBi(thietBiId));
        result.put("thoiGianLenKe", thoiGianLenKe);
        result.put("thoiGianHoatDong", thoiGianHoatDong);
        result.put("thoiGianSanXuat", thoiGianSanXuat);
        result.put("sanLuongThucTe", sanLuongThucTe);
        result.put("sanLuongKeHoach", sanLuongKeHoach);
        result.put("sanPhamDat", sanPhamDat);
        result.put("availability", availability);
        result.put("performance", performance);
        result.put("quality", quality);
        result.put("oee", oee);
        result.put("xepLoai", xepLoai);
        result.put("mucDoHieuQua", mucDoHieuQua);
        result.put("tuNgay", tuNgay);
        result.put("denNgay", denNgay);

        return result;
    }

    /**
     * Tạo nội dung báo cáo OEE
     */
    private String taoNoiDungBaoCaoOEE(Map<String, Object> data, Long thietBiId,
                                       LocalDateTime tuNgay, LocalDateTime denNgay) {
        StringBuilder content = new StringBuilder();

        content.append("=== BÁO CÁO OEE (OVERALL EQUIPMENT EFFECTIVENESS) ===\n\n");
        content.append("Thiết bị: ").append(data.get("tenThietBi")).append(" (ID: ").append(thietBiId).append(")\n");
        content.append("Thời gian phân tích: ").append(tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(" - ").append(denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        content.append("1. KẾT QUẢ TỔNG QUAN\n");
        content.append("- OEE: ").append(String.format("%.2f%%", data.get("oee"))).append("\n");
        content.append("- Xếp loại: ").append(data.get("xepLoai")).append("\n");
        content.append("- Mức độ hiệu quả: ").append(data.get("mucDoHieuQua")).append("\n\n");

        content.append("2. PHÂN TÍCH CHI TIẾT\n");
        content.append("a) AVAILABILITY (Tính khả dụng): ").append(String.format("%.2f%%", data.get("availability"))).append("\n");
        content.append("   - Thời gian lên kế: ").append(String.format("%.1f", data.get("thoiGianLenKe"))).append(" giờ\n");
        content.append("   - Thời gian hoạt động: ").append(String.format("%.1f", data.get("thoiGianHoatDong"))).append(" giờ\n");
        content.append("   - Thời gian dừng máy: ").append(String.format("%.1f", (Double)data.get("thoiGianLenKe") - (Double)data.get("thoiGianHoatDong"))).append(" giờ\n\n");

        content.append("b) PERFORMANCE (Hiệu suất): ").append(String.format("%.2f%%", data.get("performance"))).append("\n");
        content.append("   - Sản lượng kế hoạch: ").append(data.get("sanLuongKeHoach")).append(" sản phẩm\n");
        content.append("   - Sản lượng thực tế: ").append(data.get("sanLuongThucTe")).append(" sản phẩm\n");
        content.append("   - Chênh lệch: ").append((Integer)data.get("sanLuongThucTe") - (Integer)data.get("sanLuongKeHoach")).append(" sản phẩm\n\n");

        content.append("c) QUALITY (Chất lượng): ").append(String.format("%.2f%%", data.get("quality"))).append("\n");
        content.append("   - Sản phẩm đạt chất lượng: ").append(data.get("sanPhamDat")).append(" sản phẩm\n");
        content.append("   - Sản phẩm lỗi: ").append((Integer)data.get("sanLuongThucTe") - (Integer)data.get("sanPhamDat")).append(" sản phẩm\n");
        content.append("   - Tỷ lệ lỗi: ").append(String.format("%.2f%%", 100 - (Double)data.get("quality"))).append("\n\n");

        content.append("3. PHÂN TÍCH NGUYÊN NHÂN MẤT MÁT\n");
        double availability = (Double) data.get("availability");
        double performance = (Double) data.get("performance");
        double quality = (Double) data.get("quality");

        content.append("- Mất mát do Availability: ").append(String.format("%.2f%%", 100 - availability)).append("\n");
        if (availability < 90) {
            content.append("  * Nguyên nhân chính: Thời gian dừng máy cao\n");
            content.append("  * Đề xuất: Cải thiện bảo trì dự phòng, giảm thời gian setup\n");
        }

        content.append("- Mất mát do Performance: ").append(String.format("%.2f%%", 100 - performance)).append("\n");
        if (performance < 95) {
            content.append("  * Nguyên nhân chính: Tốc độ sản xuất chưa đạt\n");
            content.append("  * Đề xuất: Tối ưu hóa quy trình, đào tạo vận hành\n");
        }

        content.append("- Mất mát do Quality: ").append(String.format("%.2f%%", 100 - quality)).append("\n");
        if (quality < 99) {
            content.append("  * Nguyên nhân chính: Tỷ lệ lỗi cao\n");
            content.append("  * Đề xuất: Cải thiện kiểm soát chất lượng, hiệu chỉnh thiết bị\n");
        }

        content.append("\n4. SO SÁNH CHUẨN THẾ GIỚI\n");
        double oee = (Double) data.get("oee");
        content.append("- World Class OEE: ≥85%\n");
        content.append("- OEE hiện tại: ").append(String.format("%.2f%%", oee)).append("\n");
        content.append("- Khoảng cách: ").append(String.format("%.2f%%", Math.max(0, 85 - oee))).append("\n\n");

        content.append("5. KẾ HOẠCH HÀNH ĐỘNG\n");
        if (oee >= 85) {
            content.append("- Duy trì hiệu quả hiện tại\n");
            content.append("- Chia sẻ best practices cho thiết bị khác\n");
        } else if (oee >= 70) {
            content.append("- Tập trung cải thiện yếu tố kém nhất\n");
            content.append("- Đào tạo nâng cao cho nhân viên\n");
        } else {
            content.append("- Xem xét toàn bộ quy trình sản xuất\n");
            content.append("- Có thể cần đầu tư nâng cấp thiết bị\n");
        }

        content.append("\nBáo cáo được tạo tự động vào: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return content.toString();
    }

    /**
     * Xếp loại OEE theo tiêu chuẩn quốc tế
     */
    private String xepLoaiOEE(double oee) {
        if (oee >= 85) return "WORLD CLASS";
        if (oee >= 70) return "TỐT";
        if (oee >= 60) return "TRUNG BÌNH";
        if (oee >= 40) return "KÉM";
        return "RẤT KÉM";
    }

    /**
     * Đánh giá mức độ hiệu quả
     */
    private String danhGiaMucDoHieuQua(double oee) {
        if (oee >= 85) return "XUẤT SẮC";
        if (oee >= 70) return "TỐT";
        if (oee >= 60) return "CẦN CẢI THIỆN";
        return "CẦN HÀNH ĐỘNG NGAY";
    }

    // Các phương thức helper - tạm thời return dữ liệu mẫu
    private double layThoiGianLenKe(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual calculation - thời gian theo kế hoạch
        return 168.0; // 7 ngày * 24 giờ
    }

    private double layThoiGianHoatDong(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual calculation - thời gian thực tế hoạt động
        return 150.0; // 150 giờ
    }

    private double layThoiGianSanXuat(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual calculation - thời gian sản xuất hiệu quả
        return 140.0; // 140 giờ
    }

    private int laySanLuongThucTe(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 950; // 950 sản phẩm
    }

    private int laySanLuongKeHoach(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 1000; // 1000 sản phẩm
    }

    private int laySanPhamDat(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 930; // 930 sản phẩm đạt chất lượng
    }

    private String layTenThietBi(Long thietBiId) {
        // TODO: Implement actual query
        return "Dây chuyền sản xuất số " + thietBiId;
    }
}