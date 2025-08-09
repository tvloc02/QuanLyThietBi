package com.hethong.baotri.dich_vu.nguoi_dung;

import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.VaiTroRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.nguoi_dung.VaiTro;
import com.hethong.baotri.dto.nguoi_dung.NguoiDungDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    /**
     * ✅ TÌM NGƯỜI DÙNG THEO TÊN ĐĂNG NHẬP - TRẢ VỀ OPTIONAL
     */
    @Transactional(readOnly = true)
    public Optional<NguoiDung> timNguoiDungTheoTenDangNhap(String tenDangNhap) {
        log.debug("🔍 Tìm người dùng theo tên đăng nhập: {}", tenDangNhap);
        return nguoiDungRepository.findByTenDangNhap(tenDangNhap);
    }

    /**
     * ✅ LẤY NGƯỜI DÙNG THEO TÊN ĐĂNG NHẬP - TRẢ VỀ DTO
     */
    @Transactional(readOnly = true)
    public NguoiDungDTO layNguoiDungTheoTenDangNhap(String tenDangNhap) {
        log.debug("🔍 Lấy thông tin người dùng theo tên đăng nhập: {}", tenDangNhap);

        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với tên đăng nhập: " + tenDangNhap));

        NguoiDungDTO dto = modelMapper.map(nguoiDung, NguoiDungDTO.class);

        // Thêm thông tin vai trò
        List<String> vaiTroList = nguoiDung.getVaiTroSet().stream()
                .map(VaiTro::getTenVaiTro)
                .collect(Collectors.toList());
        dto.setDanhSachVaiTro(vaiTroList);

        return dto;
    }

    /**
     * ✅ ĐẾM TỔNG SỐ NGƯỜI DÙNG
     */
    @Transactional(readOnly = true)
    public Long demTongNguoiDung() {
        log.debug("🔢 Đếm tổng số người dùng");
        Long count = nguoiDungRepository.count();
        log.debug("📊 Tổng số người dùng: {}", count);
        return count;
    }

    /**
     * ✅ TÌM NGƯỜI DÙNG THEO ID
     */
    @Transactional(readOnly = true)
    public NguoiDungDTO timNguoiDungTheoId(Long id) {
        log.debug("🔍 Tìm người dùng theo ID: {}", id);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));

        NguoiDungDTO dto = modelMapper.map(nguoiDung, NguoiDungDTO.class);

        // Thêm thông tin vai trò
        List<String> vaiTroList = nguoiDung.getVaiTroSet().stream()
                .map(VaiTro::getTenVaiTro)
                .collect(Collectors.toList());
        dto.setDanhSachVaiTro(vaiTroList);

        return dto;
    }

    /**
     * ✅ LẤY DANH SÁCH NGƯỜI DÙNG (PHÂN TRANG)
     */
    @Transactional(readOnly = true)
    public Page<NguoiDungDTO> layDanhSachNguoiDung(Pageable pageable) {
        log.debug("📋 Lấy danh sách người dùng - page: {}, size: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<NguoiDung> nguoiDungPage = nguoiDungRepository.findAll(pageable);

        return nguoiDungPage.map(nguoiDung -> {
            NguoiDungDTO dto = modelMapper.map(nguoiDung, NguoiDungDTO.class);

            // Thêm thông tin vai trò
            List<String> vaiTroList = nguoiDung.getVaiTroSet().stream()
                    .map(VaiTro::getTenVaiTro)
                    .collect(Collectors.toList());
            dto.setDanhSachVaiTro(vaiTroList);

            return dto;
        });
    }

    /**
     * ✅ TÌM KIẾM NGƯỜI DÙNG
     */
    @Transactional(readOnly = true)
    public Page<NguoiDungDTO> timKiemNguoiDung(String tuKhoa, Pageable pageable) {
        log.debug("🔍 Tìm kiếm người dùng với từ khóa: [{}]", tuKhoa);

        Page<NguoiDung> ketQua = nguoiDungRepository.timKiemNguoiDung(tuKhoa, pageable);

        return ketQua.map(nguoiDung -> {
            NguoiDungDTO dto = modelMapper.map(nguoiDung, NguoiDungDTO.class);

            // Thêm thông tin vai trò
            List<String> vaiTroList = nguoiDung.getVaiTroSet().stream()
                    .map(VaiTro::getTenVaiTro)
                    .collect(Collectors.toList());
            dto.setDanhSachVaiTro(vaiTroList);

            return dto;
        });
    }

    /**
     * ✅ TẠO NGƯỜI DÙNG MỚI
     */
    public NguoiDungDTO taoNguoiDung(NguoiDungDTO nguoiDungDTO) {
        log.info("➕ Tạo người dùng mới: {}", nguoiDungDTO.getTenDangNhap());

        try {
            // Kiểm tra trùng tên đăng nhập
            if (nguoiDungRepository.existsByTenDangNhap(nguoiDungDTO.getTenDangNhap())) {
                throw new RuntimeException("Tên đăng nhập đã tồn tại: " + nguoiDungDTO.getTenDangNhap());
            }

            // Kiểm tra trùng email
            if (nguoiDungRepository.existsByEmail(nguoiDungDTO.getEmail())) {
                throw new RuntimeException("Email đã tồn tại: " + nguoiDungDTO.getEmail());
            }

            NguoiDung nguoiDung = modelMapper.map(nguoiDungDTO, NguoiDung.class);

            // Mã hóa mật khẩu
            if (nguoiDungDTO.getMatKhau() != null && !nguoiDungDTO.getMatKhau().isEmpty()) {
                nguoiDung.setMatKhau(passwordEncoder.encode(nguoiDungDTO.getMatKhau()));
            } else {
                // Mật khẩu mặc định
                nguoiDung.setMatKhau(passwordEncoder.encode("123456"));
            }

            // Thiết lập các giá trị mặc định
            nguoiDung.setTrangThaiHoatDong(true);
            nguoiDung.setTaiKhoanKhongBiKhoa(true);
            nguoiDung.setTaiKhoanKhongHetHan(true);
            nguoiDung.setThongTinDangNhapHopLe(true);
            nguoiDung.setSoLanDangNhapThatBai(0);
            nguoiDung.setNgayTao(LocalDateTime.now());

            // Gán vai trò mặc định (nếu cần)
            if (nguoiDungDTO.getDanhSachVaiTro() != null && !nguoiDungDTO.getDanhSachVaiTro().isEmpty()) {
                nguoiDung.getVaiTroSet().addAll(
                        nguoiDungDTO.getDanhSachVaiTro().stream()
                                .map(vaiTroTen -> vaiTroRepository.findByTenVaiTro(vaiTroTen)
                                        .orElseThrow(() -> new RuntimeException("Vai trò không tồn tại: " + vaiTroTen)))
                                .collect(Collectors.toSet())
                );
            }

            NguoiDung savedNguoiDung = nguoiDungRepository.save(nguoiDung);
            log.info("✅ Tạo người dùng thành công: {}", savedNguoiDung.getTenDangNhap());

            return modelMapper.map(savedNguoiDung, NguoiDungDTO.class);
        } catch (Exception e) {
            log.error("❌ Lỗi khi tạo người dùng: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo người dùng: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ CẬP NHẬT NGƯỜI DÙNG
     */
    public NguoiDungDTO capNhatNguoiDung(Long id, NguoiDungDTO nguoiDungDTO) {
        log.info("💾 Cập nhật người dùng ID: {}", id);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng có ID: " + id));

        // Cập nhật thông tin (không cập nhật password và username)
        nguoiDung.setHoVaTen(nguoiDungDTO.getHoVaTen());
        nguoiDung.setEmail(nguoiDungDTO.getEmail());
        nguoiDung.setSoDienThoai(nguoiDungDTO.getSoDienThoai());
        nguoiDung.setDiaChi(nguoiDungDTO.getDiaChi());
        nguoiDung.setNgayCapNhat(LocalDateTime.now());

        NguoiDung updatedNguoiDung = nguoiDungRepository.save(nguoiDung);
        log.info("✅ Cập nhật người dùng thành công ID: {}", id);

        return modelMapper.map(updatedNguoiDung, NguoiDungDTO.class);
    }

    /**
     * ✅ XÓA NGƯỜI DÙNG
     */
    public void xoaNguoiDung(Long id) {
        log.info("🗑️ Xóa người dùng ID: {}", id);

        if (!nguoiDungRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy người dùng có ID: " + id);
        }

        nguoiDungRepository.deleteById(id);
        log.info("✅ Xóa người dùng thành công ID: {}", id);
    }

    /**
     * ✅ LẤY DANH SÁCH VAI TRÒ CỦA NGƯỜI DÙNG
     */
    @Transactional(readOnly = true)
    public List<String> layDanhSachVaiTro(String tenDangNhap) {
        log.debug("🔍 Lấy danh sách vai trò cho user: {}", tenDangNhap);

        Optional<NguoiDung> nguoiDung = nguoiDungRepository.findByTenDangNhap(tenDangNhap);

        if (nguoiDung.isPresent()) {
            return nguoiDung.get().getVaiTroSet().stream()
                    .map(VaiTro::getTenVaiTro)
                    .collect(Collectors.toList());
        }

        return List.of();
    }

    /**
     * ✅ GẮN VAI TRÒ CHO NGƯỜI DÙNG
     */
    public void ganVaiTro(Long userId, Long vaiTroId) {
        log.info("🔗 Gắn vai trò {} cho user ID: {}", vaiTroId, userId);

        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        VaiTro vaiTro = vaiTroRepository.findById(vaiTroId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        nguoiDung.getVaiTroSet().add(vaiTro);
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Gắn vai trò thành công");
    }

    /**
     * ✅ GỠ VAI TRÒ KHỎI NGƯỜI DÙNG
     */
    public void goVaiTro(Long userId, Long vaiTroId) {
        log.info("🔗 Gỡ vai trò {} khỏi user ID: {}", vaiTroId, userId);

        NguoiDung nguoiDung = nguoiDungRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        VaiTro vaiTro = vaiTroRepository.findById(vaiTroId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vai trò"));

        nguoiDung.getVaiTroSet().remove(vaiTro);
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Gỡ vai trò thành công");
    }

    /**
     * ✅ KIỂM TRA VAI TRÒ CỦA NGƯỜI DÙNG
     */
    @Transactional(readOnly = true)
    public boolean kiemTraVaiTro(String tenDangNhap, String tenVaiTro) {
        log.debug("🔍 Kiểm tra vai trò {} cho user: {}", tenVaiTro, tenDangNhap);

        Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
        if (nguoiDungOpt.isPresent()) {
            return nguoiDungOpt.get().getVaiTroSet().stream()
                    .anyMatch(vaiTro -> vaiTro.getTenVaiTro().equals(tenVaiTro));
        }
        return false;
    }

    /**
     * ✅ ĐẾM TỔNG SỐ NGƯỜI DÙNG
     */
    @Transactional(readOnly = true)
    public long demTongSoNguoiDung() {
        log.debug("🔢 Đếm tổng số người dùng");
        long count = nguoiDungRepository.count();
        log.debug("📊 Tổng số người dùng: {}", count);
        return count;
    }

    /**
     * ✅ ĐẾM NGƯỜI DÙNG ĐANG HOẠT ĐỘNG
     */
    @Transactional(readOnly = true)
    public long demNguoiDungHoatDong() {
        log.debug("🔢 Đếm người dùng đang hoạt động");
        long count = nguoiDungRepository.countByTrangThaiHoatDong(true);
        log.debug("📊 Số người dùng hoạt động: {}", count);
        return count;
    }

    /**
     * ✅ CẬP NHẬT TRẠNG THÁI HOẠT ĐỘNG
     */
    public void capNhatTrangThaiHoatDong(Long id, Boolean trangThaiHoatDong) {
        log.info("🔄 Cập nhật trạng thái hoạt động user ID: {} -> {}", id, trangThaiHoatDong);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        nguoiDung.setTrangThaiHoatDong(trangThaiHoatDong);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Cập nhật trạng thái thành công");
    }

    /**
     * ✅ KHÓA TÀI KHOẢN
     */
    public void khoaTaiKhoan(Long id) {
        log.info("🔒 Khóa tài khoản user ID: {}", id);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        nguoiDung.setTaiKhoanKhongBiKhoa(false);
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Khóa tài khoản thành công");
    }

    /**
     * ✅ MỞ KHÓA TÀI KHOẢN
     */
    public void moKhoaTaiKhoan(Long id) {
        log.info("🔓 Mở khóa tài khoản user ID: {}", id);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        nguoiDung.setTaiKhoanKhongBiKhoa(true);
        nguoiDung.setSoLanDangNhapThatBai(0); // Reset failed attempts
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Mở khóa tài khoản thành công");
    }

    /**
     * ✅ ĐỔI MẬT KHẨU
     */
    public void doiMatKhau(Long id, String matKhauCu, String matKhauMoi) {
        log.info("🔑 Đổi mật khẩu user ID: {}", id);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(matKhauCu, nguoiDung.getMatKhau())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }

        // Cập nhật mật khẩu mới
        nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Đổi mật khẩu thành công");
    }

    /**
     * ✅ RESET MẬT KHẨU (ADMIN)
     */
    public void resetMatKhau(Long id, String matKhauMoi) {
        log.info("🔄 Reset mật khẩu user ID: {}", id);

        NguoiDung nguoiDung = nguoiDungRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        nguoiDung.setMatKhau(passwordEncoder.encode(matKhauMoi));
        nguoiDung.setSoLanDangNhapThatBai(0); // Reset failed attempts
        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDungRepository.save(nguoiDung);

        log.info("✅ Reset mật khẩu thành công");
    }

    /**
     * ✅ LẤY NGƯỜI DÙNG CÓ THỂ PHÂN CÔNG
     */
    @Transactional(readOnly = true)
    public List<NguoiDungDTO> layNguoiDungCoThePhancong(String tenQuyen) {
        log.debug("🔍 Lấy người dùng có thể phân công với quyền: {}", tenQuyen);

        try {
            List<NguoiDung> nguoiDungList = nguoiDungRepository.layNguoiDungCoThePhancong(tenQuyen);

            return nguoiDungList.stream()
                    .map(nguoiDung -> modelMapper.map(nguoiDung, NguoiDungDTO.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Lỗi khi lấy người dùng có thể phân công: {}", e.getMessage());
            // Fallback: Lấy tất cả người dùng hoạt động
            List<NguoiDung> fallbackList = nguoiDungRepository.findByTrangThaiHoatDong(true, Pageable.unpaged()).getContent();
            return fallbackList.stream()
                    .map(nguoiDung -> modelMapper.map(nguoiDung, NguoiDungDTO.class))
                    .collect(Collectors.toList());
        }
    }

    /**
     * ✅ THỐNG KÊ NGƯỜI DÙNG THEO VAI TRÒ
     */
    @Transactional(readOnly = true)
    public List<Object[]> thongKeNguoiDungTheoVaiTro() {
        log.debug("📊 Thống kê người dùng theo vai trò");

        try {
            return nguoiDungRepository.thongKeNguoiDungTheoVaiTro();
        } catch (Exception e) {
            log.error("Lỗi khi thống kê người dùng theo vai trò: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * ✅ CẬP NHẬT LẦN ĐĂNG NHẬP CUỐI
     */
    public void capNhatLanDangNhapCuoi(String tenDangNhap) {
        log.debug("🕒 Cập nhật lần đăng nhập cuối cho user: {}", tenDangNhap);

        try {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
            if (nguoiDungOpt.isPresent()) {
                NguoiDung nguoiDung = nguoiDungOpt.get();
                nguoiDung.setLanDangNhapCuoi(LocalDateTime.now());
                nguoiDung.setSoLanDangNhapThatBai(0); // Reset failed attempts on successful login
                nguoiDungRepository.save(nguoiDung);
                log.debug("✅ Cập nhật lần đăng nhập cuối thành công cho: {}", tenDangNhap);
            }
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật lần đăng nhập cuối cho {}: {}", tenDangNhap, e.getMessage());
        }
    }

    /**
     * ✅ XỬ LÝ ĐĂNG NHẬP THẤT BẠN
     */
    public void xuLyDangNhapThatBai(String tenDangNhap) {
        log.debug("❌ Xử lý đăng nhập thất bại cho user: {}", tenDangNhap);

        try {
            Optional<NguoiDung> nguoiDungOpt = nguoiDungRepository.findByTenDangNhap(tenDangNhap);
            if (nguoiDungOpt.isPresent()) {
                NguoiDung nguoiDung = nguoiDungOpt.get();
                int failedAttempts = nguoiDung.getSoLanDangNhapThatBai() + 1;
                nguoiDung.setSoLanDangNhapThatBai(failedAttempts);

                // Khóa tài khoản nếu quá 5 lần thất bại
                if (failedAttempts >= 5) {
                    nguoiDung.setTaiKhoanKhongBiKhoa(false);
                    log.warn("🔒 Tài khoản {} đã bị khóa do đăng nhập thất bại quá nhiều lần", tenDangNhap);
                }

                nguoiDungRepository.save(nguoiDung);
                log.debug("✅ Xử lý đăng nhập thất bại thành công cho: {}", tenDangNhap);
            }
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đăng nhập thất bại cho {}: {}", tenDangNhap, e.getMessage());
        }
    }
}