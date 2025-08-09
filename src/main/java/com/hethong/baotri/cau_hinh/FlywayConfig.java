package com.hethong.baotri.cau_hinh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("disabled") // Vô hiệu hóa Flyway config
@ConditionalOnProperty(value = "spring.flyway.enabled", havingValue = "true")
@Slf4j
public class FlywayConfig {

    // ✅ FLYWAY ĐÃ BỊ VÔ HIỆU HÓA
    // Không sử dụng migration - dùng cơ sở dữ liệu có sẵn

    public FlywayConfig() {
        log.info("🚫 Flyway configuration đã bị vô hiệu hóa");
        log.info("📊 Sử dụng cơ sở dữ liệu SQL Server có sẵn");
    }
}