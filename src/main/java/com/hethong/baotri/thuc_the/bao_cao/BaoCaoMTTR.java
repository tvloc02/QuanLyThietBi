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
public class BaoCaoMTTR {

    private final BaoCaoRepository baoCaoRepository;

    /**
     * Tạo báo cáo MTTR (Mean Time To Repair)
     */
    public BaoCao taoBaoCaoMTTR(String nguoiTao, LocalDateTime tuNgay, LocalDateTime denNgay, Long thietBiId) {
        log.info("Đang tạo báo cáo MTTR cho thiết bị {} từ {} đến {}", thietBiId, tuNgay, denNgay);

        BaoCao baoCao = new BaoCao();
        baoCao.setTenBaoCao("Báo cáo MTTR - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        baoCao.setLoaiBaoCao("MTTR");
        baoCao.setMoTa("Báo cáo thời gian trung bình để sửa chữa từ " +
                tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " đến " + denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        baoCao.setNguoiTao(nguoiTao);
        baoCao.setTrangThai("COMPLETED");

        // Tính toán MTTR
        Map<String, Object> mttrData = tinhToanMTTR(thietBiId, tuNgay, denNgay);
        String noiDung = taoNoiDungBaoCaoMTTR(mttrData, thietBiId, tuNgay, denNgay);
        baoCao.setNoiDung(noiDung);

        return baoCaoRepository.save(baoCao);
    }

    /**
     * Tính toán MTTR cho thiết bị
     */
    public Map<String, Object> tinhToanMTTR(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu sửa chữa
        int soLanSuaChua = laySoLanSuaChua(thietBiId, tuNgay, denNgay);
        double tongThoiGianSuaChua = layTongThoiGianSuaChua(thietBiId, tuNgay, denNgay);

        // Tính MTTR (giờ)
        double mttrGio = soLanSuaChua > 0 ? tongThoiGianSuaChua / soLanSuaChua : 0;

        // Tính hiệu quả bảo trì
        double hieuQuaBaoTri = tinhHieuQuaBaoTri(mttrGio);

        // Đánh giá mức độ phản ứng
        String mucDoPhang = danhGiaMucDoPhanUng(mttrGio);

        // Chi phí trung bình mỗi lần sửa chữa
        double chiPhiTrungBinh = layChiPhiSuaChuas(thietBiId, tuNgay, denNgay) / Math.max(soLanSuaChua, 1);

        result.put("thietBiId", thietBiId);
        result.put("tenThietBi", layTenThietBi(thietBiId));
        result.put("soLanSuaChua", soLanSuaChua);
        result.put("tongThoiGianSuaChua", tongThoiGianSuaChua);
        result.put("mttrGio", mttrGio);
        result.put("mttrPhut", mttrGio * 60);
        result.put("hieuQuaBaoTri", hieuQuaBaoTri);
        result.put("mucDoPhang", mucDoPhang);
        result.put("chiPhiTrungBinh", chiPhiTrungBinh);
        result.put("tuNgay", tuNgay);
        result.put("denNgay", denNgay);

        return result;
    }

    /**
     * Tạo nội dung báo cáo MTTR
     */
    private String taoNoiDungBaoCaoMTTR(Map<String, Object> data, Long thietBiId,
                                        LocalDateTime tuNgay, LocalDateTime denNgay) {
        StringBuilder content = new StringBuilder();

        content.append("=== BÁO CÁO MTTR (MEAN TIME TO REPAIR) ===\n\n");
        content.append("Thiết bị: ").append(data.get("tenThietBi")).append(" (ID: ").append(thietBiId).append(")\n");
        content.append("Thời gian phân tích: ").append(tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(" - ").append(denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        content.append("1. KẾT QUẢ PHÂN TÍCH\n");
        content.append("- Số lần sửa chữa: ").append(data.get("soLanSuaChua")).append("\n");
        content.append("- Tổng thời gian sửa chữa: ").append(String.format("%.2f", data.get("tongThoiGianSuaChua"))).append(" giờ\n");
        content.append("- MTTR: ").append(String.format("%.2f", data.get("mttrGio"))).append(" giờ\n");
        content.append("- MTTR: ").append(String.format("%.0f", data.get("mttrPhut"))).append(" phút\n");
        content.append("- Hiệu quả bảo trì: ").append(String.format("%.2f%%", data.get("hieuQuaBaoTri"))).append("\n");
        content.append("- Mức độ phản ứng: ").append(data.get("mucDoPhang")).append("\n");
        content.append("- Chi phí TB/lần: ").append(String.format("%.0f", data.get("chiPhiTrungBinh"))).append(" VNĐ\n\n");

        content.append("2. PHÂN TÍCH CHI TIẾT\n");
        double mttr = (Double) data.get("mttrGio");
        content.append("- Thời gian chẩn đoán TB: ").append(String.format("%.1f", mttr * 0.3)).append(" giờ (30%)\n");
        content.append("- Thời gian chờ vật tư: ").append(String.format("%.1f", mttr * 0.2)).append(" giờ (20%)\n");
        content.append("- Thời gian sửa chữa thực tế: ").append(String.format("%.1f", mttr * 0.4)).append(" giờ (40%)\n");
        content.append("- Thời gian kiểm tra: ").append(String.format("%.1f", mttr * 0.1)).append(" giờ (10%)\n\n");

        content.append("3. ĐÁNH GIÁ VÀ KHUYẾN NGHỊ\n");
        if (mttr <= 2) {
            content.append("- Thời gian sửa chữa rất tốt, duy trì hiệu quả hiện tại\n");
            content.append("- Đề xuất: Chia sẻ kinh nghiệm cho các thiết bị khác\n");
        } else if (mttr <= 6) {
            content.append("- Thời gian sửa chữa ở mức chấp nhận được\n");
            content.append("- Đề xuất: Cải thiện quy trình chẩn đoán và chuẩn bị vật tư\n");
        } else if (mttr <= 12) {
            content.append("- Thời gian sửa chữa hơi cao, cần cải thiện\n");
            content.append("- Đề xuất: Đào tạo thêm kỹ thuật viên, chuẩn bị sẵn vật tư\n");
        } else {
            content.append("- Thời gian sửa chữa quá cao, cần hành động ngay\n");
            content.append("- Đề xuất: Xem xét thay thế thiết bị hoặc cải thiện toàn bộ quy trình\n");
        }

        content.append("\n4. LỊCH SỬ SỬA CHỮA\n");
        content.append(layLichSuSuaChua(thietBiId, tuNgay, denNgay));

        content.append("\n5. SO SÁNH CHUẨN NGÀNH\n");
        content.append("- MTTR chuẩn ngành: 4-8 giờ\n");
        content.append("- MTTR hiện tại: ").append(String.format("%.2f", mttr)).append(" giờ\n");
        if (mttr <= 4) {
            content.append("- Đánh giá: Vượt trội so với chuẩn ngành\n");
        } else if (mttr <= 8) {
            content.append("- Đánh giá: Đạt chuẩn ngành\n");
        } else {
            content.append("- Đánh giá: Dưới chuẩn ngành, cần cải thiện\n");
        }

        content.append("\nBáo cáo được tạo tự động vào: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return content.toString();
    }

    /**
     * Tính hiệu quả bảo trì dựa trên MTTR
     */
    private double tinhHieuQuaBaoTri(double mttrGio) {
        // Hiệu quả bảo trì cao khi MTTR thấp
        if (mttrGio <= 2) return 95.0;
        if (mttrGio <= 4) return 85.0;
        if (mttrGio <= 6) return 75.0;
        if (mttrGio <= 8) return 65.0;
        if (mttrGio <= 12) return 50.0;
        return 30.0;
    }

    /**
     * Đánh giá mức độ phản ứng dựa trên MTTR
     */
    private String danhGiaMucDoPhanUng(double mttrGio) {
        if (mttrGio <= 1) return "RẤT NHANH";
        if (mttrGio <= 3) return "NHANH";
        if (mttrGio <= 6) return "TRUNG BÌNH";
        if (mttrGio <= 12) return "CHẬM";
        return "RẤT CHẬM";
    }

    // Các phương thức helper - tạm thời return dữ liệu mẫu
    private int laySoLanSuaChua(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query to count repairs
        return 5;
    }

    private double layTongThoiGianSuaChua(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual calculation
        return 18.5; // Tổng 18.5 giờ
    }

    private double layChiPhiSuaChuas(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual calculation
        return 15000000; // 15 triệu VNĐ
    }

    private String layTenThietBi(Long thietBiId) {
        // TODO: Implement actual query
        return "Máy nén khí số " + thietBiId;
    }

    private String layLichSuSuaChua(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return "- 05/07/2025: Thay thế van xả khí (2.5h)\n" +
                "- 18/07/2025: Sửa chữa động cơ (6.0h)\n" +
                "- 25/07/2025: Thay dầu thủy lực (1.5h)\n" +
                "- 30/07/2025: Kiểm tra hệ thống làm mát (3.0h)\n" +
                "- 10/08/2025: Thay thế bộ lọc khí (1.5h)\n";
    }
}