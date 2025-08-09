package com.hethong.baotri.dieu_khien.nguoi_dung;

import com.hethong.baotri.dich_vu.nguoi_dung.XacThucService;
import com.hethong.baotri.dto.nguoi_dung.DangNhapDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class XacThucController {

    private final XacThucService xacThucService;

    @PostMapping(value = "/dang-nhap", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> dangNhap(@Valid @RequestBody DangNhapDTO dangNhapDTO) {
        log.info("Yêu cầu đăng nhập từ: {}", dangNhapDTO.getTenDangNhap());

        Map<String, Object> response = new HashMap<>();

        try {
            // Sử dụng XacThucService để xử lý đăng nhập
            Map<String, Object> ketQuaDangNhap = xacThucService.dangNhap(
                    dangNhapDTO.getTenDangNhap(),
                    dangNhapDTO.getMatKhau()
            );

            response.put("success", true);
            response.put("token", ketQuaDangNhap.get("accessToken"));
            response.put("tokenType", ketQuaDangNhap.get("tokenType"));
            response.put("nguoiDung", ketQuaDangNhap.get("nguoiDung"));
            response.put("message", ketQuaDangNhap.get("message"));

            log.info("Đăng nhập thành công cho: {}", dangNhapDTO.getTenDangNhap());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);

        } catch (Exception e) {
            log.error("Lỗi khi đăng nhập: {}", e.getMessage());

            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @PostMapping("/dang-xuat")
    public ResponseEntity<Map<String, String>> dangXuat(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, String> response = new HashMap<>();

        try {
            if (token != null && token.startsWith("Bearer ")) {
                String actualToken = token.substring(7);
                xacThucService.dangXuat(actualToken);
            }

            response.put("message", "Đăng xuất thành công");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: {}", e.getMessage());
            response.put("message", "Đăng xuất thành công"); // Vẫn trả về thành công
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @PostMapping("/doi-mat-khau")
    public ResponseEntity<Map<String, String>> doiMatKhau(@RequestParam String tenDangNhap,
                                                          @RequestParam String matKhauCu,
                                                          @RequestParam String matKhauMoi) {
        Map<String, String> response = new HashMap<>();

        try {
            xacThucService.doiMatKhau(tenDangNhap, matKhauCu, matKhauMoi);
            response.put("message", "Đổi mật khẩu thành công");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @PostMapping("/quen-mat-khau")
    public ResponseEntity<Map<String, String>> quenMatKhau(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();

        try {
            xacThucService.quenMatKhau(email);
            response.put("message", "Hướng dẫn đặt lại mật khẩu đã được gửi đến email");
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        } catch (Exception e) {
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(response);
        }
    }

    @GetMapping("/thong-tin-ca-nhan")
    public ResponseEntity<Object> layThongTinCaNhan(@RequestParam String tenDangNhap) {
        try {
            // Sử dụng XacThucService để lấy thông tin người dùng hiện tại
            var nguoiDung = xacThucService.layNguoiDungHienTai();
            if (nguoiDung != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(nguoiDung);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin cá nhân: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Lỗi server"));
        }
    }

    // API test để kiểm tra server
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testAPI() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Server đang hoạt động");
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}