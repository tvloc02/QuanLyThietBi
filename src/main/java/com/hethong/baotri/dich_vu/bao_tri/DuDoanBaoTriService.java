package com.hethong.baotri.dich_vu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.CanhBaoLoi;
import com.hethong.baotri.thuc_the.bao_tri.KiemTraDinhKy;
import com.hethong.baotri.kho_du_lieu.bao_tri.CanhBaoLoiRepository;
import com.hethong.baotri.kho_du_lieu.bao_tri.KiemTraDinhKyRepository;
import com.hethong.baotri.thuc_the.thiet_bi.ThietBi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DuDoanBaoTriService {

    private final CanhBaoLoiRepository canhBaoLoiRepository;
    private final KiemTraDinhKyRepository kiemTraDinhKyRepository;


    // Predict maintenance needs for a device based on alerts and history
    public Map<String, Object> duDoanBaoTriChoThietBi(Long idThietBi, LocalDateTime tuNgay, LocalDateTime denNgay) {
        log.info("Dự đoán bảo trì cho thiết bị ID {} từ {} đến {}", idThietBi, tuNgay, denNgay);

        // Retrieve alerts for the device
        List<CanhBaoLoi> alerts = canhBaoLoiRepository.findByThietBi_IdThietBi(idThietBi, null).getContent();
        List<CanhBaoLoi> recentAlerts = alerts.stream()
                .filter(alert -> alert.getNgayPhatSinh().isAfter(tuNgay) && alert.getNgayPhatSinh().isBefore(denNgay))
                .collect(Collectors.toList());

        // Retrieve inspection history
        List<KiemTraDinhKy> inspections = kiemTraDinhKyRepository.findByThietBi_IdThietBi(idThietBi, null).getContent();
        List<KiemTraDinhKy> recentInspections = inspections.stream()
                .filter(inspection -> inspection.getNgayKiemTra().isAfter(tuNgay) && inspection.getNgayKiemTra().isBefore(denNgay))
                .collect(Collectors.toList());


        // Analyze alerts for critical issues
        long criticalAlerts = recentAlerts.stream()
                .filter(alert -> alert.getMucDoNghiemTrong() >= 3)
                .count();

        // Analyze inspections for maintenance recommendations
        long maintenanceRecommendations = recentInspections.stream()
                .filter(KiemTraDinhKy::getYeuCauBaoTri)
                .count();



        // Simple prediction logic
        Map<String, Object> prediction = new HashMap<>();
        prediction.put("thietBiId", idThietBi);
        prediction.put("soLuongCanhBaoNghiemTrong", criticalAlerts);
        prediction.put("soLuongKiemTraYeuCauBaoTri", maintenanceRecommendations);

        log.info("Dự đoán bảo trì hoàn tất cho thiết bị ID {}", idThietBi);
        return prediction;
    }

    // Generate maintenance recommendation based on analysis
    private String generateRecommendation(long criticalAlerts, long maintenanceRecommendations, Map<String, Long> issueFrequency) {
        StringBuilder recommendation = new StringBuilder();
        if (criticalAlerts > 2) {
            recommendation.append("Thiết bị có số lượng cảnh báo nghiêm trọng cao (").append(criticalAlerts)
                    .append("). Cần ưu tiên kiểm tra và bảo trì khẩn cấp.\n");
        }
        if (maintenanceRecommendations > 0) {
            recommendation.append("Có ").append(maintenanceRecommendations)
                    .append(" kiểm tra định kỳ yêu cầu bảo trì. Cần lập kế hoạch bảo trì sớm.\n");
        }
        issueFrequency.forEach((type, count) -> {
            if (count > 1) {
                recommendation.append("Loại bảo trì ").append(type)
                        .append(" xuất hiện ").append(count).append(" lần. Xem xét bảo trì phòng ngừa định kỳ.\n");
            }
        });
        return recommendation.length() > 0 ? recommendation.toString() : "Không có khuyến nghị bảo trì cụ thể.";
    }

    // Check if a device needs immediate maintenance
    public boolean canhBaoCanBaoTriNgay(Long idThietBi) {
        List<CanhBaoLoi> alerts = canhBaoLoiRepository.findCanhBaoCanThongBaoNgay();
        boolean needsMaintenance = alerts.stream()
                .anyMatch(alert -> alert.getThietBi().getIdThietBi().equals(idThietBi));
        if (needsMaintenance) {
            log.info("Thiết bị ID {} cần bảo trì ngay lập tức do cảnh báo nghiêm trọng", idThietBi);
        }
        return needsMaintenance;
    }

    // Get overdue inspections for a device
    public List<KiemTraDinhKy> kiemTraQuaHanChoThietBi(Long idThietBi, LocalDateTime ngayHienTai) {
        List<KiemTraDinhKy> overdueInspections = kiemTraDinhKyRepository.findKiemTraQuaHan(ngayHienTai);
        List<KiemTraDinhKy> deviceOverdue = overdueInspections.stream()
                .filter(inspection -> inspection.getThietBi().getIdThietBi().equals(idThietBi))
                .collect(Collectors.toList());
        log.info("Tìm thấy {} kiểm tra định kỳ quá hạn cho thiết bị ID {}", deviceOverdue.size(), idThietBi);
        return deviceOverdue;
    }
}