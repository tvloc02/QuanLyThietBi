package com.hethong.baotri.dich_vu.nguoi_dung;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.nguoi_dung.VaiTro;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("🔍 Loading user details for username: [{}]", username);

        // ✅ Tìm user trong database
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                .orElseThrow(() -> {
                    log.error("❌ User not found: [{}]", username);
                    return new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
                });

        log.info("👤 Found user: [{}] - Active: [{}]", nguoiDung.getTenDangNhap(), nguoiDung.getTrangThaiHoatDong());

        // ✅ Load authorities từ vai trò
        Collection<? extends GrantedAuthority> authorities = getAuthorities(nguoiDung);

        log.info("🎭 Loaded authorities for [{}]: {}", username, authorities);

        // ✅ Cập nhật lần đăng nhập cuối
        try {
            nguoiDung.capNhatLanDangNhapCuoi();
            nguoiDungRepository.save(nguoiDung);
            log.debug("✅ Updated last login time for user: [{}]", username);
        } catch (Exception e) {
            log.warn("⚠️ Could not update last login time: {}", e.getMessage());
        }

        // ✅ Tạo UserDetails - QUAN TRỌNG: Không check account status quá strict
        return User.builder()
                .username(nguoiDung.getTenDangNhap())
                .password(nguoiDung.getMatKhau())
                .authorities(authorities)
                .accountExpired(false)  // ✅ SỬA: Không block account expired
                .accountLocked(false)   // ✅ SỬA: Không block account locked
                .credentialsExpired(false)  // ✅ SỬA: Không block credentials expired
                .disabled(!nguoiDung.getTrangThaiHoatDong())  // Chỉ check active status
                .build();
    }

    /**
     * ✅ Load authorities từ vai trò của user - SỬA để support tất cả user
     */
    private Collection<? extends GrantedAuthority> getAuthorities(NguoiDung nguoiDung) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // ✅ Thêm vai trò từ database
        if (nguoiDung.getVaiTroSet() != null && !nguoiDung.getVaiTroSet().isEmpty()) {
            for (VaiTro vaiTro : nguoiDung.getVaiTroSet()) {
                String roleName = vaiTro.getTenVaiTro();
                log.debug("➕ Adding role authority: [{}]", roleName);
                authorities.add(new SimpleGrantedAuthority(roleName));

                // ✅ Thêm quyền từ vai trò
                if (vaiTro.getQuyenSet() != null) {
                    vaiTro.getQuyenSet().forEach(quyen -> {
                        String quyenName = quyen.getTenQuyen();
                        log.debug("➕ Adding permission authority: [{}]", quyenName);
                        authorities.add(new SimpleGrantedAuthority(quyenName));
                    });
                }
            }
        }

        // ✅ FALLBACK: Nếu không có role từ DB, tạo default authorities
        if (authorities.isEmpty()) {
            log.warn("⚠️ User [{}] has no roles from DB, creating default authorities", nguoiDung.getTenDangNhap());

            String username = nguoiDung.getTenDangNhap().toLowerCase();

            // ✅ Admin user
            if ("admin".equals(username)) {
                log.info("🔑 Creating ADMIN authorities for user [{}]", username);
                authorities.add(new SimpleGrantedAuthority("QUAN_TRI_VIEN"));
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
                authorities.add(new SimpleGrantedAuthority("QUAN_LY_NGUOI_DUNG_XEM"));
                authorities.add(new SimpleGrantedAuthority("QUAN_LY_NGUOI_DUNG_THEM"));
                authorities.add(new SimpleGrantedAuthority("QUAN_LY_NGUOI_DUNG_SUA"));
                authorities.add(new SimpleGrantedAuthority("QUAN_LY_NGUOI_DUNG_XOA"));
                authorities.add(new SimpleGrantedAuthority("XEM_THONG_KE_BAO_TRI")); // Thêm quyền mới
            }
            // ✅ SỬA: Tất cả user khác đều được phép đăng nhập với quyền USER
            else {
                log.info("👤 Creating USER authorities for user [{}]", username);
                authorities.add(new SimpleGrantedAuthority("USER"));
                authorities.add(new SimpleGrantedAuthority("NGUOI_DUNG"));

                // ✅ THÊM: Các quyền cơ bản cho user thường
                authorities.add(new SimpleGrantedAuthority("XEM_TRANG_CHU"));
                authorities.add(new SimpleGrantedAuthority("XEM_DASHBOARD"));
                authorities.add(new SimpleGrantedAuthority("XEM_THONG_TIN_CA_NHAN"));
            }
        }

        // ✅ QUAN TRỌNG: Luôn thêm quyền truy cập dashboard cho tất cả user
        authorities.add(new SimpleGrantedAuthority("ACCESS_DASHBOARD"));

        log.info("🎭 Final authorities for [{}]: {}", nguoiDung.getTenDangNhap(), authorities);
        return authorities;
    }
}