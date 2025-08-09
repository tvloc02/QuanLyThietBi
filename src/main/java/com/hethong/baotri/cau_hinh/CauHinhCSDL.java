package com.hethong.baotri.cau_hinh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Component
@Slf4j
public class CauHinhCSDL {

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void kiemTraCauHinhCSDL(ApplicationReadyEvent event) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            log.info("=== THÔNG TIN CƠ SỞ DỮ LIỆU ===");
            log.info("Database Product Name: {}", metaData.getDatabaseProductName());
            log.info("Database Product Version: {}", metaData.getDatabaseProductVersion());
            log.info("Driver Name: {}", metaData.getDriverName());
            log.info("Driver Version: {}", metaData.getDriverVersion());
            log.info("URL: {}", metaData.getURL());
            log.info("Username: {}", metaData.getUserName());
            log.info("=== KẾT NỐI CƠ SỞ DỮ LIỆU THÀNH CÔNG ===");

        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra cấu hình cơ sở dữ liệu: {}", e.getMessage(), e);
        }
    }
}