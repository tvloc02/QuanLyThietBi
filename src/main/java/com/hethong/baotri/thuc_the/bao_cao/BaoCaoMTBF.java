package com.hethong.baotri.thuc_the.bao_cao;

import com.hethong.baotri.kho_du_lieu.bao_cao.BaoCaoRepository;
import com.hethong.baotri.thuc_the.bao_cao.BaoCao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BaoCaoMTBF {

    private final BaoCaoRepository baoCaoRepository;

    /**
     * Tạo báo cáo MTBF (Mean Time Between Failures)
     */
    public BaoCao taoBaoCaoMTBF(String nguoiTao, LocalDateTime tuNgay, LocalDateTime denNgay, Long thietBiId) {
        log.info("Đang tạo báo cáo MTBF cho thiết bị {} từ {} đến {}", thietBiId, tuNgay, denNgay);

        BaoCao baoCao = new BaoCao();
        baoCao.setTenBaoCao("Báo cáo MTBF - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        baoCao.setLoaiBaoCao("MTBF");
        baoCao.setMoTa("Báo cáo thời gian trung bình giữa các lần hỏng hóc từ " +
                tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " đến " + denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        baoCao.setNguoiTao(nguoiTao);
        baoCao.setTrangThai("COMPLETED");

        // Tính toán MTBF
        Map<String, Object> mtbfData = tinhToanMTBF(thietBiId, tuNgay, denNgay);
        String noiDung = taoNoiDungBaoCaoMTBF(mtbfData, thietBiId, tuNgay, denNgay);
        baoCao.setNoiDung(noiDung);

        return baoCaoRepository.save(baoCao);
    }

    /**
     * Tính toán MTBF cho thiết bị
     */
    public Map<String, Object> tinhToanMTBF(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        Map<String, Object> result = new HashMap<>();

        // Lấy dữ liệu hỏng hóc
        int soLanHongHoc = laySoLanHongHoc(thietBiId, tuNgay, denNgay);
        long tongThoiGianHoatDong = tinhTongThoiGianHoatDong(thietBiId, tuNgay, denNgay);

        // Tính MTBF (giờ)
        double mtbfGio = soLanHongHoc > 0 ? (double) tongThoiGianHoatDong / soLanHongHoc : 0;

        // Tính độ tin cậy
        double doTinCay = tinhDoTinCay(mtbfGio);

        // Tình trạng thiết bị
        String tinhTrang = danhGiaTinhTrang(mtbfGio);

        result.put("thietBiId", thietBiId);
        result.put("tenThietBi", layTenThietBi(thietBiId));
        result.put("soLanHongHoc", soLanHongHoc);
        result.put("tongThoiGianHoatDong", tongThoiGianHoatDong);
        result.put("mtbfGio", mtbfGio);
        result.put("mtbfNgay", mtbfGio / 24);
        result.put("doTinCay", doTinCay);
        result.put("tinhTrang", tinhTrang);
        result.put("tuNgay", tuNgay);
        result.put("denNgay", denNgay);

        return result;
    }

    /**
     * Tạo nội dung báo cáo MTBF
     */
    private String taoNoiDungBaoCaoMTBF(Map<String, Object> data, Long thietBiId,
                                        LocalDateTime tuNgay, LocalDateTime denNgay) {
        StringBuilder content = new StringBuilder();

        content.append("=== BÁO CÁO MTBF (MEAN TIME BETWEEN FAILURES) ===\n\n");
        content.append("Thiết bị: ").append(data.get("tenThietBi")).append(" (ID: ").append(thietBiId).append(")\n");
        content.append("Thời gian phân tích: ").append(tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(" - ").append(denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        content.append("1. KẾT QUẢ PHÂN TÍCH\n");
        content.append("- Số lần hỏng hóc: ").append(data.get("soLanHongHoc")).append("\n");
        content.append("- Tổng thời gian hoạt động: ").append(data.get("tongThoiGianHoatDong")).append(" giờ\n");
        content.append("- MTBF: ").append(String.format("%.2f", data.get("mtbfGio"))).append(" giờ\n");
        content.append("- MTBF: ").append(String.format("%.2f", data.get("mtbfNgay"))).append(" ngày\n");
        content.append("- Độ tin cậy: ").append(String.format("%.2f%%", data.get("doTinCay"))).append("\n");
        content.append("- Tình trạng: ").append(data.get("tinhTrang")).append("\n\n");

        content.append("2. ĐÁNH GIÁ VÀ KHUYẾN NGHỊ\n");
        double mtbf = (Double) data.get("mtbfGio");
        if (mtbf > 720) { // > 30 ngày
            content.append("- Thiết bị hoạt động ổn định, duy trì kế hoạch bảo trì hiện tại\n");
        } else if (mtbf > 168) { // > 7 ngày
            content.append("- Thiết bị hoạt động khá tốt, cần theo dõi thêm\n");
            content.append("- Đề xuất: Tăng cường kiểm tra định kỳ\n");
        } else {
            content.append("- Thiết bị có xu hướng hỏng hóc thường xuyên\n");
            content.append("- Đề xuất: Kiểm tra nguyên nhân gốc, cân nhắc thay thế\n");
        }

        content.append("\n3. LỊCH SỬ HỎNG HÓC\n");
        content.append(layLichSuHongHoc(thietBiId, tuNgay, denNgay));

        content.append("\nBáo cáo được tạo tự động vào: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return content.toString();
    }

    /**
     * Tính độ tin cậy dựa trên MTBF
     */
    private double tinhDoTinCay(double mtbfGio) {
        // Công thức đơn giản: R(t) = e^(-t/MTBF) cho t = 24h
        double t = 24; // 24 giờ
        return mtbfGio > 0 ? Math.exp(-t / mtbfGio) * 100 : 0;
    }

    /**
     * Đánh giá tình trạng thiết bị dựa trên MTBF
     */
    private String danhGiaTinhTrang(double mtbfGio) {
        if (mtbfGio > 720) return "RẤT TỐT";
        if (mtbfGio > 168) return "TỐT";
        if (mtbfGio > 72) return "TRUNG BÌNH";
        if (mtbfGio > 24) return "KÉM";
        return "RẤT KÉM";
    }

    // Các phương thức helper - tạm thời return dữ liệu mẫu
    private int laySoLanHongHoc(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query to count failures
        return 3;
    }

    private long tinhTongThoiGianHoatDong(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual calculation
        long tongGio = ChronoUnit.HOURS.between(tuNgay, denNgay);
        return tongGio - 24; // Trừ thời gian downtime
    }

    private String layTenThietBi(Long thietBiId) {
        // TODO: Implement actual query
        return "Máy nén khí số " + thietBiId;
    }

    private String layLichSuHongHoc(Long thietBiId, LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return "- 01/07/2025: Hỏng van xả khí\n" +
                "- 15/07/2025: Quá nhiệt động cơ\n" +
                "- 28/07/2025: Rò rỉ dầu thủy lực\n";
    }
}