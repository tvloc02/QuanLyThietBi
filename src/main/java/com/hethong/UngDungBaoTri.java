package com.hethong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class UngDungBaoTri {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "dev");
        SpringApplication.run(UngDungBaoTri.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();
        String port = env.getProperty("server.port", "8081");
        String profile = env.getProperty("spring.profiles.active", "default");

        log.info("=================================================================");
        log.info("🚀 HỆ THỐNG QUẢN LÝ BẢO TRÌ THIẾT BỊ ĐÃ KHỞI ĐỘNG THÀNH CÔNG!");
        log.info("=================================================================");
        log.info("🌐 URL ứng dụng: http://localhost:{}", port);
        log.info("🔐 Trang đăng nhập: http://localhost:{}/login", port);
        log.info("📊 Dashboard: http://localhost:{}/dashboard", port);
        log.info("🔧 Test API: http://localhost:{}/api/debug/db-info", port);
        log.info("📝 Profile hiện tại: {}", profile);
        log.info("=================================================================");
        log.info("📋 THÔNG TIN ĐĂNG NHẬP:");
        log.info("   👤 Admin: admin / 123456");
        log.info("   👤 Hiệu trưởng: hieupho.nguyen / 123456");
        log.info("   👤 TP CSVC: phong.tran / 123456");
        log.info("   👤 NV CSVC: duc.le / 123456");
        log.info("   👤 Kỹ thuật viên: thanh.vo / 123456");
        log.info("   👤 Giáo viên: linh.nguyen / 123456");
        log.info("=================================================================");
    }
}