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
public class BaoCaoTongHop {

    private final BaoCaoRepository baoCaoRepository;

    /**
     * Tạo báo cáo tổng hợp hệ thống
     */
    public BaoCao taoBaoCaoTongHop(String nguoiTao, LocalDateTime tuNgay, LocalDateTime denNgay) {
        log.info("Đang tạo báo cáo tổng hợp từ {} đến {}", tuNgay, denNgay);

        BaoCao baoCao = new BaoCao();
        baoCao.setTenBaoCao("Báo cáo tổng hợp hệ thống - " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        baoCao.setLoaiBaoCao("TONG_HOP");
        baoCao.setMoTa("Báo cáo tổng hợp toàn bộ hoạt động bảo trì từ " +
                tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " đến " + denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        baoCao.setNguoiTao(nguoiTao);
        baoCao.setTrangThai("COMPLETED");

        // Tạo nội dung báo cáo
        String noiDung = taoNoiDungBaoCaoTongHop(tuNgay, denNgay);
        baoCao.setNoiDung(noiDung);

        return baoCaoRepository.save(baoCao);
    }

    /**
     * Tạo nội dung báo cáo tổng hợp
     */
    private String taoNoiDungBaoCaoTongHop(LocalDateTime tuNgay, LocalDateTime denNgay) {
        StringBuilder content = new StringBuilder();

        content.append("=== BÁO CÁO TỔNG HỢP HỆ THỐNG BẢO TRÌ ===\n\n");
        content.append("Thời gian: ").append(tuNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append(" - ").append(denNgay.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n\n");

        // Thống kê tổng quan
        content.append("1. THỐNG KÊ TỔNG QUAN\n");
        content.append("- Tổng số thiết bị: ").append(layTongSoThietBi()).append("\n");
        content.append("- Tổng số lệnh bảo trì: ").append(layTongSoLenhBaoTri(tuNgay, denNgay)).append("\n");
        content.append("- Số lệnh hoàn thành: ").append(layLenhHoanThanh(tuNgay, denNgay)).append("\n");
        content.append("- Số lệnh đang thực hiện: ").append(layLenhDangThucHien()).append("\n");
        content.append("- Tỷ lệ hoàn thành: ").append(String.format("%.2f%%", tinhTyLeHoanThanh(tuNgay, denNgay))).append("\n\n");

        // Phân tích hiệu quả
        content.append("2. PHÂN TÍCH HIỆU QUẢ\n");
        content.append("- Thời gian bảo trì trung bình: ").append(layThoiGianBaoTriTB(tuNgay, denNgay)).append(" giờ\n");
        content.append("- Chi phí bảo trì tổng: ").append(layChiPhiBaoTriTong(tuNgay, denNgay)).append(" VNĐ\n");
        content.append("- Số lượng vật tư sử dụng: ").append(layVatTuSuDung(tuNgay, denNgay)).append("\n\n");

        // Cảnh báo và đề xuất
        content.append("3. CẢNH BÁO VÀ ĐỀ XUẤT\n");
        content.append("- Thiết bị cần bảo trì khẩn cấp: ").append(layThietBiCanBaoTriKhanCap()).append("\n");
        content.append("- Vật tư sắp hết: ").append(layVatTuSapHet()).append("\n");
        content.append("- Đề xuất cải thiện: ").append(layDeXuatCaiThien()).append("\n\n");

        content.append("Báo cáo được tạo tự động bởi hệ thống vào: ")
                .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        return content.toString();
    }

    /**
     * Lấy thống kê chi tiết cho dashboard
     */
    public Map<String, Object> layThongKeChiTiet(LocalDateTime tuNgay, LocalDateTime denNgay) {
        Map<String, Object> thongKe = new HashMap<>();

        thongKe.put("tongSoThietBi", layTongSoThietBi());
        thongKe.put("tongSoLenhBaoTri", layTongSoLenhBaoTri(tuNgay, denNgay));
        thongKe.put("lenhHoanThanh", layLenhHoanThanh(tuNgay, denNgay));
        thongKe.put("lenhDangThucHien", layLenhDangThucHien());
        thongKe.put("tyLeHoanThanh", tinhTyLeHoanThanh(tuNgay, denNgay));
        thongKe.put("thoiGianBaoTriTB", layThoiGianBaoTriTB(tuNgay, denNgay));
        thongKe.put("chiPhiBaoTriTong", layChiPhiBaoTriTong(tuNgay, denNgay));
        thongKe.put("vatTuSuDung", layVatTuSuDung(tuNgay, denNgay));
        thongKe.put("thietBiCanBaoTriKhanCap", layThietBiCanBaoTriKhanCap());
        thongKe.put("vatTuSapHet", layVatTuSapHet());

        return thongKe;
    }

    // Các phương thức helper - tạm thời return dữ liệu mẫu
    private int layTongSoThietBi() {
        // TODO: Implement actual query
        return 150;
    }

    private int layTongSoLenhBaoTri(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 45;
    }

    private int layLenhHoanThanh(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 38;
    }

    private int layLenhDangThucHien() {
        // TODO: Implement actual query
        return 7;
    }

    private double tinhTyLeHoanThanh(LocalDateTime tuNgay, LocalDateTime denNgay) {
        int tong = layTongSoLenhBaoTri(tuNgay, denNgay);
        int hoanThanh = layLenhHoanThanh(tuNgay, denNgay);
        return tong > 0 ? (double) hoanThanh / tong * 100 : 0;
    }

    private double layThoiGianBaoTriTB(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 4.5;
    }

    private long layChiPhiBaoTriTong(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 125000000L;
    }

    private int layVatTuSuDung(LocalDateTime tuNgay, LocalDateTime denNgay) {
        // TODO: Implement actual query
        return 89;
    }

    private int layThietBiCanBaoTriKhanCap() {
        // TODO: Implement actual query
        return 3;
    }

    private int layVatTuSapHet() {
        // TODO: Implement actual query
        return 5;
    }

    private String layDeXuatCaiThien() {
        return "Tăng cường bảo trì định kỳ, cập nhật quy trình kiểm tra";
    }
}