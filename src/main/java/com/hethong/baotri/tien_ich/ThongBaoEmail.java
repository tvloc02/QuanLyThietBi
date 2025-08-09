package com.hethong.baotri.tien_ich;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ThongBaoEmail {

    private final JavaMailSender mailSender;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Async
    public void guiEmailDon(String nguoiNhan, String tieuDe, String noiDung) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(nguoiNhan);
            message.setSubject(tieuDe);
            message.setText(noiDung);
            message.setFrom("hethongbaotri@company.com");

            mailSender.send(message);
            log.info("Đã gửi email thành công đến: {}", nguoiNhan);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email đến {}: {}", nguoiNhan, e.getMessage());
        }
    }

    @Async
    public void guiEmailHTML(String nguoiNhan, String tieuDe, String noiDungHTML) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(nguoiNhan);
            helper.setSubject(tieuDe);
            helper.setText(noiDungHTML, true);
            helper.setFrom("hethongbaotri@company.com");

            mailSender.send(mimeMessage);
            log.info("Đã gửi email HTML thành công đến: {}", nguoiNhan);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email HTML đến {}: {}", nguoiNhan, e.getMessage());
        }
    }

    @Async
    public void guiEmailNhieu(List<String> danhSachNguoiNhan, String tieuDe, String noiDung) {
        for (String nguoiNhan : danhSachNguoiNhan) {
            guiEmailDon(nguoiNhan, tieuDe, noiDung);
        }
    }

    public void guiThongBaoYeuCauBaoTri(String emailNguoiNhan, String tenThietBi, String maYeuCau, LocalDateTime ngayYeuCau) {
        String tieuDe = "Thông báo yêu cầu bảo trì mới - " + maYeuCau;
        String noiDung = String.format(
                "Kính gửi Anh/Chị,\n\n" +
                        "Hệ thống thông báo có yêu cầu bảo trì mới cần được xử lý:\n\n" +
                        "- Mã yêu cầu: %s\n" +
                        "- Thiết bị: %s\n" +
                        "- Ngày yêu cầu: %s\n\n" +
                        "Vui lòng truy cập hệ thống để xem chi tiết và xử lý yêu cầu.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý bảo trì thiết bị",
                maYeuCau, tenThietBi, ngayYeuCau.format(formatter)
        );

        guiEmailDon(emailNguoiNhan, tieuDe, noiDung);
    }

    public void guiThongBaoThietBiCanBaoTri(String emailNguoiNhan, String tenThietBi, String maThietBi, LocalDateTime ngayBaoTriDuKien) {
        String tieuDe = "Thông báo thiết bị cần bảo trì - " + maThietBi;
        String noiDung = String.format(
                "Kính gửi Anh/Chị,\n\n" +
                        "Hệ thống thông báo thiết bị sau đây cần được bảo trì:\n\n" +
                        "- Mã thiết bị: %s\n" +
                        "- Tên thiết bị: %s\n" +
                        "- Ngày bảo trì dự kiến: %s\n\n" +
                        "Vui lòng lập kế hoạch bảo trì cho thiết bị này.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý bảo trì thiết bị",
                maThietBi, tenThietBi, ngayBaoTriDuKien.format(formatter)
        );

        guiEmailDon(emailNguoiNhan, tieuDe, noiDung);
    }

    public void guiThongBaoVatTuThieuHang(String emailNguoiNhan, String tenVatTu, String maVatTu, int soLuongTon) {
        String tieuDe = "Cảnh báo vật tư thiếu hàng - " + maVatTu;
        String noiDung = String.format(
                "Kính gửi Anh/Chị,\n\n" +
                        "Hệ thống cảnh báo vật tư sau đây đang thiếu hàng:\n\n" +
                        "- Mã vật tư: %s\n" +
                        "- Tên vật tư: %s\n" +
                        "- Số lượng tồn kho: %d\n\n" +
                        "Vui lòng xem xét bổ sung vật tư để đảm bảo hoạt động bảo trì.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý bảo trì thiết bị",
                maVatTu, tenVatTu, soLuongTon
        );

        guiEmailDon(emailNguoiNhan, tieuDe, noiDung);
    }

    public void guiThongBaoHoanThanhBaoTri(String emailNguoiNhan, String maYeuCau, String tenThietBi, LocalDateTime ngayHoanThanh) {
        String tieuDe = "Thông báo hoàn thành bảo trì - " + maYeuCau;
        String noiDung = String.format(
                "Kính gửi Anh/Chị,\n\n" +
                        "Công việc bảo trì đã được hoàn thành:\n\n" +
                        "- Mã yêu cầu: %s\n" +
                        "- Thiết bị: %s\n" +
                        "- Ngày hoàn thành: %s\n\n" +
                        "Vui lòng truy cập hệ thống để xem báo cáo chi tiết.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý bảo trì thiết bị",
                maYeuCau, tenThietBi, ngayHoanThanh.format(formatter)
        );

        guiEmailDon(emailNguoiNhan, tieuDe, noiDung);
    }

    public void guiThongBaoTuChoi(String emailNguoiNhan, String maYeuCau, String tenThietBi, String lyDoTuChoi) {
        String tieuDe = "Thông báo từ chối yêu cầu bảo trì - " + maYeuCau;
        String noiDung = String.format(
                "Kính gửi Anh/Chị,\n\n" +
                        "Yêu cầu bảo trì đã bị từ chối:\n\n" +
                        "- Mã yêu cầu: %s\n" +
                        "- Thiết bị: %s\n" +
                        "- Lý do từ chối: %s\n\n" +
                        "Vui lòng liên hệ để biết thêm chi tiết.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý bảo trì thiết bị",
                maYeuCau, tenThietBi, lyDoTuChoi
        );

        guiEmailDon(emailNguoiNhan, tieuDe, noiDung);
    }

    public void guiThongBaoQuaHan(String emailNguoiNhan, String maYeuCau, String tenThietBi, LocalDateTime ngayQuaHan) {
        String tieuDe = "Cảnh báo yêu cầu bảo trì quá hạn - " + maYeuCau;
        String noiDung = String.format(
                "Kính gửi Anh/Chị,\n\n" +
                        "Yêu cầu bảo trì sau đây đã quá hạn xử lý:\n\n" +
                        "- Mã yêu cầu: %s\n" +
                        "- Thiết bị: %s\n" +
                        "- Ngày quá hạn: %s\n\n" +
                        "Vui lòng khẩn trương xử lý yêu cầu này.\n\n" +
                        "Trân trọng,\n" +
                        "Hệ thống quản lý bảo trì thiết bị",
                maYeuCau, tenThietBi, ngayQuaHan.format(formatter)
        );

        guiEmailDon(emailNguoiNhan, tieuDe, noiDung);
    }

    public void guiBaoCaoHangTuan(String emailNguoiNhan, String baoCaoHTML) {
        String tieuDe = "Báo cáo bảo trì tuần - " + LocalDateTime.now().format(formatter);
        guiEmailHTML(emailNguoiNhan, tieuDe, baoCaoHTML);
    }

    public void guiBaoCaoHangThang(String emailNguoiNhan, String baoCaoHTML) {
        String tieuDe = "Báo cáo bảo trì tháng - " + LocalDateTime.now().format(formatter);
        guiEmailHTML(emailNguoiNhan, tieuDe, baoCaoHTML);
    }
}