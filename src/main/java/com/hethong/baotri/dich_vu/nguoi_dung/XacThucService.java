package com.hethong.baotri.dich_vu.nguoi_dung;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.tien_ich.JwtTokenUtil;
import com.hethong.baotri.ngoai_le.NgoaiLeNguoiDung;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class XacThucService {

    private final NguoiDungRepository nguoiDungRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    // ✅ Lazy injection để tránh circular dependency
    @Autowired
    @Lazy
    private AuthenticationManager authenticationManager;

    public XacThucService(NguoiDungRepository nguoiDungRepository,
                          JwtTokenUtil jwtTokenUtil,
                          PasswordEncoder passwordEncoder,
                          CustomUserDetailsService userDetailsService) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    public Map<String, Object> dangNhap(String tenDangNhap, String matKhau) {
        log.info("Đang xử lý đăng nhập cho: {}", tenDangNhap);

        try {
            // ✅ Kiểm tra user exists trước
            NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                    .orElseThrow(() -> new BadCredentialsException("Tên đăng nhập không tồn tại"));

            log.debug("Found user: {}", nguoiDung.getTenDangNhap());

            // ✅ Kiểm tra password trực tiếp trước
            if (!passwordEncoder.matches(matKhau, nguoiDung.getMatKhau())) {
                log.warn("Password mismatch for user: {}", tenDangNhap);
                xuLyDangNhapThatBaiTrucTiep(tenDangNhap);
                throw new BadCredentialsException("Mật khẩu không đúng");
            }

            // ✅ Chỉ authentication khi authenticationManager có sẵn
            Authentication authentication = null;
            if (authenticationManager != null) {
                authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(tenDangNhap, matKhau)
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(tenDangNhap);

            // Tạo JWT token
            String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

            // Cập nhật lần đăng nhập cuối
            capNhatLanDangNhapCuoiTrucTiep(tenDangNhap);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", jwtTokenUtil.getExpirationTime());
            response.put("nguoiDung", nguoiDung);
            response.put("message", "Đăng nhập thành công");

            log.info("Đăng nhập thành công cho: {}", tenDangNhap);
            return response;

        } catch (DisabledException e) {
            log.warn("Tài khoản bị vô hiệu hóa: {}", tenDangNhap);
            xuLyDangNhapThatBaiTrucTiep(tenDangNhap);
            throw new NgoaiLeNguoiDung("Tài khoản đã bị vô hiệu hóa");
        } catch (BadCredentialsException e) {
            log.warn("Thông tin đăng nhập không hợp lệ: {}", tenDangNhap);
            xuLyDangNhapThatBaiTrucTiep(tenDangNhap);
            throw new NgoaiLeNguoiDung("Tên đăng nhập hoặc mật khẩu không đúng");
        } catch (Exception e) {
            log.error("Lỗi đăng nhập: ", e);
            throw new NgoaiLeNguoiDung("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    // Method helper để tránh circular dependency
    private void capNhatLanDangNhapCuoiTrucTiep(String tenDangNhap) {
        try {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
            if (nguoiDungOpt.isPresent()) {
                NguoiDung nguoiDung = nguoiDungOpt.get();
                nguoiDung.capNhatLanDangNhapCuoi();
                nguoiDungRepository.save(nguoiDung);
            }
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật lần đăng nhập cuối: {}", e.getMessage());
        }
    }

    // Method helper để tránh circular dependency
    private void xuLyDangNhapThatBaiTrucTiep(String tenDangNhap) {
        try {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
            if (nguoiDungOpt.isPresent()) {
                NguoiDung nguoiDung = nguoiDungOpt.get();
                nguoiDung.tangSoLanDangNhapThatBai();
                nguoiDungRepository.save(nguoiDung);
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đăng nhập thất bại: {}", e.getMessage());
        }
    }

    public void dangXuat(String token) {
        log.info("Đang xử lý đăng xuất");

        try {
            if (token != null) {
                String tenDangNhap = jwtTokenUtil.getUsernameFromToken(token);
                log.info("Đăng xuất thành công cho: {}", tenDangNhap);
            }

            // Xóa authentication context
            SecurityContextHolder.clearContext();

        } catch (Exception e) {
            log.error("Lỗi khi đăng xuất: {}", e.getMessage());
        }
    }

    public void quenMatKhau(String email) {
        log.info("Đang xử lý quên mật khẩu cho email: {}", email);

        Optional<NguoiDung> nguoiDung = nguoiDungRepository.findByEmail(email);
        if (nguoiDung.isEmpty()) {
            throw new NgoaiLeNguoiDung("Không tìm thấy tài khoản với email: " + email);
        }

        // TODO: Implement reset password logic
        log.info("Đã gửi email đặt lại mật khẩu cho: {}", email);
    }

    public void doiMatKhau(String tenDangNhap, String matKhauCu, String matKhauMoi) {
        log.info("Đang xử lý đổi mật khẩu cho: {}", tenDangNhap);

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy người dùng: " + tenDangNhap));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhau())) {
            throw new NgoaiLeNguoiDung("Mật khẩu cũ không đúng");
        }

        // Cập nhật mật khẩu mới
        nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepository.save(nguoiDung);

        log.info("Đổi mật khẩu thành công cho: {}", tenDangNhap);
    }

    @Transactional(readOnly = true)
    public NguoiDung layNguoiDungHienTai() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String tenDangNhap = authentication.getName();
        return nguoiDungRepository.findByTenDangNhap(tenDangNhap).orElse(null);
    }
}