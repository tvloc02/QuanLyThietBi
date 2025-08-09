package com.hethong.baotri.cau_hinh;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(
        value = "app.data-loader.enabled",
        havingValue = "true",
        matchIfMissing = false
) // Chỉ chạy khi được enable
@Profile("!prod") // Không chạy trong production
public class DataLoader implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚫 DataLoader đã bị vô hiệu hóa - sử dụng dữ liệu có sẵn từ SQL Server");
        log.info("📊 Kiểm tra kết nối cơ sở dữ liệu...");

        try {
            // Chỉ kiểm tra kết nối
            long userCount = nguoiDungRepository.count();
            long roleCount = vaiTroRepository.count();

            log.info("✅ Kết nối cơ sở dữ liệu thành công!");
            log.info("👥 Số người dùng trong DB: {}", userCount);
            log.info("🔒 Số vai trò trong DB: {}", roleCount);

            if (userCount == 0) {
                log.warn("⚠️ Không có người dùng nào trong cơ sở dữ liệu!");
                log.warn("💡 Hãy đảm bảo cơ sở dữ liệu đã được tạo với dữ liệu mẫu");
            }

        } catch (Exception e) {
            log.error("❌ Lỗi khi kiểm tra cơ sở dữ liệu: {}", e.getMessage(), e);
        }
    }
}