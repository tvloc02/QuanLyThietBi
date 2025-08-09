package com.hethong.baotri.dich_vu.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:noreply@baotri.edu.vn}")
    private String fromEmail;

    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;

    /**
     * Gửi email đơn giản
     */
    @Async
    public void guiEmailDonGian(String toEmail, String subject, String content) {
        if (!emailEnabled) {
            log.info("Email service is disabled. Skipping email to: {}", toEmail);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            javaMailSender.send(message);
            log.info("Gửi email thành công đến: {}", toEmail);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email đến {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Gửi email HTML
     */
    @Async
    public void guiEmailHTML(String toEmail, String subject, String htmlContent) {
        if (!emailEnabled) {
            log.info("Email service is disabled. Skipping HTML email to: {}", toEmail);
            return;
        }

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            log.info("Gửi email HTML thành công đến: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Lỗi khi gửi email HTML đến {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Gửi email thông báo hệ thống
     */
    @Async
    public void guiEmailThongBao(String toEmail, String tieuDe, String noiDung) {
        String htmlContent = taoTemplateThongBao(tieuDe, noiDung);
        guiEmailHTML(toEmail, "[Hệ thống Bảo trì] " + tieuDe, htmlContent);
    }

    /**
     * Gửi email cho nhiều người
     */
    @Async
    public void guiEmailNhieuNguoi(List<String> danhSachEmail, String subject, String content) {
        if (!emailEnabled) {
            log.info("Email service is disabled. Skipping bulk email to {} recipients", danhSachEmail.size());
            return;
        }

        for (String email : danhSachEmail) {
            guiEmailDonGian(email, subject, content);
        }
    }

    /**
     * Gửi email reset mật khẩu
     */
    @Async
    public void guiEmailResetMatKhau(String toEmail, String tenNguoiDung, String matKhauMoi) {
        String tieuDe = "Reset mật khẩu hệ thống bảo trì";
        String htmlContent = taoTemplateResetMatKhau(tenNguoiDung, matKhauMoi);

        guiEmailHTML(toEmail, tieuDe, htmlContent);
    }

    /**
     * Gửi email chào mừng người dùng mới
     */
    @Async
    public void guiEmailChaoMung(String toEmail, String tenNguoiDung, String tenDangNhap, String matKhau) {
        String tieuDe = "Chào mừng đến với hệ thống bảo trì thiết bị";
        String htmlContent = taoTemplateChaoMung(tenNguoiDung, tenDangNhap, matKhau);

        guiEmailHTML(toEmail, tieuDe, htmlContent);
    }

    /**
     * Gửi email báo cáo định kỳ
     */
    @Async
    public void guiEmailBaoCaoTuan(String toEmail, String noiDungBaoCao) {
        String tieuDe = "Báo cáo hoạt động bảo trì tuần";
        String htmlContent = taoTemplateBaoCao(noiDungBaoCao);

        guiEmailHTML(toEmail, tieuDe, htmlContent);
    }

    // Template Methods

    private String taoTemplateThongBao(String tieuDe, String noiDung) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #007bff; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                    .btn { display: inline-block; padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>Hệ thống Bảo trì Thiết bị</h2>
                    </div>
                    <div class="content">
                        <h3>%s</h3>
                        <p>%s</p>
                        <p>
                            <a href="http://localhost:8081/login" class="btn">Đăng nhập hệ thống</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>Email này được gửi tự động từ hệ thống. Vui lòng không trả lời email này.</p>
                        <p>© 2024 Hệ thống Bảo trì Thiết bị</p>
                    </div>
                </div>
            </body>
            </html>
            """, tieuDe, tieuDe, noiDung.replace("\n", "<br>"));
    }

    private String taoTemplateResetMatKhau(String tenNguoiDung, String matKhauMoi) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Reset Mật khẩu</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #dc3545; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                    .password { background-color: #e9ecef; padding: 10px; border-radius: 5px; font-family: monospace; font-size: 16px; }
                    .warning { background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 15px; border-radius: 5px; margin: 15px 0; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>🔐 Reset Mật khẩu</h2>
                    </div>
                    <div class="content">
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Mật khẩu của bạn đã được reset thành công. Thông tin đăng nhập mới:</p>
                        
                        <div class="password">
                            <strong>Mật khẩu mới:</strong> %s
                        </div>
                        
                        <div class="warning">
                            <strong>⚠️ Lưu ý quan trọng:</strong>
                            <ul>
                                <li>Vui lòng đổi mật khẩu ngay sau khi đăng nhập</li>
                                <li>Không chia sẻ mật khẩu với bất kỳ ai</li>
                                <li>Sử dụng mật khẩu mạnh khi thay đổi</li>
                            </ul>
                        </div>
                        
                        <p>
                            <a href="http://localhost:8081/login" style="display: inline-block; padding: 10px 20px; background-color: #dc3545; color: white; text-decoration: none; border-radius: 5px;">Đăng nhập ngay</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>Nếu bạn không yêu cầu reset mật khẩu, vui lòng liên hệ admin ngay lập tức.</p>
                        <p>© 2024 Hệ thống Bảo trì Thiết bị</p>
                    </div>
                </div>
            </body>
            </html>
            """, tenNguoiDung, matKhauMoi);
    }

    private String taoTemplateChaoMung(String tenNguoiDung, String tenDangNhap, String matKhau) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Chào mừng</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #28a745; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                    .credentials { background-color: #e9ecef; padding: 15px; border-radius: 5px; margin: 15px 0; }
                    .btn { display: inline-block; padding: 12px 24px; background-color: #28a745; color: white; text-decoration: none; border-radius: 5px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>🎉 Chào mừng đến với Hệ thống Bảo trì!</h2>
                    </div>
                    <div class="content">
                        <p>Xin chào <strong>%s</strong>,</p>
                        <p>Chào mừng bạn đến với Hệ thống Quản lý Bảo trì Thiết bị! Tài khoản của bạn đã được tạo thành công.</p>
                        
                        <div class="credentials">
                            <h4>📋 Thông tin đăng nhập:</h4>
                            <p><strong>Tên đăng nhập:</strong> %s</p>
                            <p><strong>Mật khẩu:</strong> %s</p>
                        </div>
                        
                        <h4>🚀 Bước tiếp theo:</h4>
                        <ol>
                            <li>Đăng nhập vào hệ thống</li>
                            <li>Đổi mật khẩu mặc định</li>
                            <li>Cập nhật thông tin cá nhân</li>
                            <li>Khám phá các tính năng của hệ thống</li>
                        </ol>
                        
                        <p>
                            <a href="http://localhost:8081/login" class="btn">Đăng nhập ngay</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>Nếu bạn có câu hỏi nào, vui lòng liên hệ với quản trị viên hệ thống.</p>
                        <p>© 2024 Hệ thống Bảo trì Thiết bị</p>
                    </div>
                </div>
            </body>
            </html>
            """, tenNguoiDung, tenDangNhap, matKhau);
    }

    private String taoTemplateBaoCao(String noiDungBaoCao) {
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Báo cáo tuần</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background-color: #6f42c1; color: white; padding: 20px; text-align: center; }
                    .content { padding: 20px; background-color: #f8f9fa; }
                    .footer { padding: 20px; text-align: center; font-size: 12px; color: #666; }
                    .report-content { background-color: white; padding: 20px; border-radius: 5px; border-left: 4px solid #6f42c1; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h2>📊 Báo cáo Hoạt động Bảo trì</h2>
                    </div>
                    <div class="content">
                        <div class="report-content">
                            %s
                        </div>
                        
                        <p>
                            <a href="http://localhost:8081/bao-cao" style="display: inline-block; padding: 10px 20px; background-color: #6f42c1; color: white; text-decoration: none; border-radius: 5px;">Xem báo cáo chi tiết</a>
                        </p>
                    </div>
                    <div class="footer">
                        <p>Báo cáo này được tạo tự động hàng tuần.</p>
                        <p>© 2024 Hệ thống Bảo trì Thiết bị</p>
                    </div>
                </div>
            </body>
            </html>
            """, noiDungBaoCao.replace("\n", "<br>"));
    }

    /**
     * Kiểm tra email có hợp lệ không
     */
    public boolean kiemTraEmailHopLe(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    /**
     * Gửi email test
     */
    public boolean guiEmailTest(String toEmail) {
        if (!emailEnabled) {
            log.info("Email service is disabled. Cannot send test email.");
            return false;
        }

        try {
            String subject = "Test Email - Hệ thống Bảo trì";
            String content = "Đây là email test từ hệ thống Bảo trì Thiết bị.\n\n" +
                    "Nếu bạn nhận được email này, nghĩa là hệ thống email đang hoạt động bình thường.\n\n" +
                    "Thời gian gửi: " + java.time.LocalDateTime.now();

            guiEmailDonGian(toEmail, subject, content);
            log.info("Gửi email test thành công đến: {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Lỗi khi gửi email test đến {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    /**
     * Lấy trạng thái email service
     */
    public boolean kiemTraTrangThaiEmailService() {
        return emailEnabled;
    }

    /**
     * Bật/tắt email service
     */
    public void capNhatTrangThaiEmailService(boolean enabled) {
        this.emailEnabled = enabled;
        log.info("Email service đã được {}", enabled ? "bật" : "tắt");
    }
}