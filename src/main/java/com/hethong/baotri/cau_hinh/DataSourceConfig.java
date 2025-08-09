package com.hethong.baotri.cau_hinh;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Slf4j
@Profile("disabled") // ✅ VÔ HIỆU HÓA - dùng auto-configuration của Spring Boot
public class DataSourceConfig {

    // ✅ CLASS NÀY ĐÃ BỊ VÔ HIỆU HÓA
    // Spring Boot sẽ tự động tạo DataSource từ application.properties

    public DataSourceConfig() {
        log.info("🚫 DataSourceConfig đã bị vô hiệu hóa - sử dụng Spring Boot auto-configuration");
    }
}