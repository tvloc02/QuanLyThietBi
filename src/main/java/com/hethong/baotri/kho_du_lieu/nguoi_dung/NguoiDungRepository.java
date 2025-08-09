package com.hethong.baotri.kho_du_lieu.nguoi_dung;

import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    /**
     * ✅ TÌM THEO TÊN ĐĂNG NHẬP
     */
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    /**
     * ✅ TÌM THEO EMAIL
     */
    Optional<NguoiDung> findByEmail(String email);

    /**
     * ✅ TÌM THEO SỐ ĐIỆN THOẠI
     */
    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);

    /**
     * ✅ KIỂM TRA TỒN TẠI THEO TÊN ĐĂNG NHẬP
     */
    boolean existsByTenDangNhap(String tenDangNhap);

    /**
     * ✅ KIỂM TRA TỒN TẠI THEO EMAIL
     */
    boolean existsByEmail(String email);

    /**
     * ✅ KIỂM TRA TỒN TẠI THEO SỐ ĐIỆN THOẠI
     */
    boolean existsBySoDienThoai(String soDienThoai);

    /**
     * ✅ TÌM THEO TRẠNG THÁI HOẠT ĐỘNG (PAGEABLE)
     */
    Page<NguoiDung> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    /**
     * ✅ TÌM THEO TRẠNG THÁI HOẠT ĐỘNG (LIST)
     */
    List<NguoiDung> findByTrangThaiHoatDongTrue();

    /**
     * ✅ TÌM KIẾM THEO HỌ VÀ TÊN
     */
    Page<NguoiDung> findByHoVaTenContainingIgnoreCase(String hoVaTen, Pageable pageable);

    /**
     * ✅ TÌM KIẾM THEO NHIỀU TIÊU CHÍ
     */
    Page<NguoiDung> findByTenDangNhapContainingIgnoreCaseOrHoVaTenContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String tenDangNhap, String hoVaTen, String email, Pageable pageable);

    /**
     * ✅ TÌM KIẾM NÂNG CAO - GENERAL SEARCH
     */
    @Query("SELECT nd FROM NguoiDung nd WHERE " +
            "LOWER(nd.tenDangNhap) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(nd.hoVaTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(nd.email) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(nd.soDienThoai) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<NguoiDung> timKiemNguoiDung(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    /**
     * ✅ TÌM THEO VAI TRÒ
     */
    @Query("SELECT nd FROM NguoiDung nd JOIN nd.vaiTroSet vt WHERE vt.tenVaiTro = :tenVaiTro")
    Page<NguoiDung> findByVaiTro(@Param("tenVaiTro") String tenVaiTro, Pageable pageable);

    /**
     * ✅ TÌM THEO VAI TRÒ (LIST)
     */
    @Query("SELECT nd FROM NguoiDung nd JOIN nd.vaiTroSet vt WHERE vt.tenVaiTro = :tenVaiTro")
    List<NguoiDung> findByVaiTroList(@Param("tenVaiTro") String tenVaiTro);

    /**
     * ✅ TÌM THEO NHIỀU VAI TRÒ
     */
    @Query("SELECT nd FROM NguoiDung nd JOIN nd.vaiTroSet vt WHERE vt.tenVaiTro IN :danhSachVaiTro")
    List<NguoiDung> findByVaiTroIn(@Param("danhSachVaiTro") List<String> danhSachVaiTro);

    /**
     * ✅ TÌM THEO ĐỘI BẢO TRÌ
     */
    Page<NguoiDung> findByDoiBaoTri_IdDoiBaoTri(Long idDoiBaoTri, Pageable pageable);

    /**
     * ✅ TÌM NGƯỜI DÙNG CÓ QUYỀN CỤ THỂ
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd JOIN nd.vaiTroSet vt JOIN vt.quyenSet q WHERE q.tenQuyen = :tenQuyen")
    List<NguoiDung> findByQuyen(@Param("tenQuyen") String tenQuyen);

    /**
     * ✅ TÌM NGƯỜI DÙNG CÓ THỂ PHÂN CÔNG
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd JOIN nd.vaiTroSet vt JOIN vt.quyenSet q " +
            "WHERE q.tenQuyen = :tenQuyen AND nd.trangThaiHoatDong = true")
    List<NguoiDung> layNguoiDungCoThePhancong(@Param("tenQuyen") String tenQuyen);

    /**
     * ✅ TÌM THEO KHOẢNG THỜI GIAN ĐĂNG NHẬP CUỐI
     */
    Page<NguoiDung> findByLanDangNhapCuoiBetween(LocalDateTime tuNgay, LocalDateTime denNgay, Pageable pageable);

    /**
     * ✅ TÌM NGƯỜI DÙNG CHƯA ĐĂNG NHẬP BAO GIỜ
     */
    Page<NguoiDung> findByLanDangNhapCuoiIsNull(Pageable pageable);

    /**
     * ✅ TÌM THEO TRẠNG THÁI KHÓA TÀI KHOẢN
     */
    Page<NguoiDung> findByTaiKhoanKhongBiKhoa(Boolean taiKhoanKhongBiKhoa, Pageable pageable);

    /**
     * ✅ TÌM NGƯỜI DÙNG BỊ KHÓA
     */
    List<NguoiDung> findByTaiKhoanKhongBiKhoaFalse();

    /**
     * ✅ TÌM NGƯỜI DÙNG ĐĂNG NHẬP THẤT BẠI NHIỀU LẦN
     */
    Page<NguoiDung> findBySoLanDangNhapThatBaiGreaterThan(Integer soLan, Pageable pageable);

    /**
     * ✅ TÌM NGƯỜI DÙNG ĐĂNG NHẬP THẤT BẠI NHIỀU LẦN (LIST)
     */
    List<NguoiDung> findBySoLanDangNhapThatBaiGreaterThanEqual(Integer soLan);

    /**
     * ✅ XÓA VAI TRÒ CỦA NGƯỜI DÙNG
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM nguoi_dung_vai_tro WHERE id_nguoi_dung = ?1", nativeQuery = true)
    void deleteUserRoles(Long userId);

    /**
     * ✅ THÊM VAI TRÒ CHO NGƯỜI DÙNG
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO nguoi_dung_vai_tro (id_nguoi_dung, id_vai_tro) VALUES (?1, ?2)", nativeQuery = true)
    void insertUserRole(Long userId, Long roleId);

    /**
     * ✅ TÌM KIẾM NÂNG CAO VỚI NHIỀU ĐIỀU KIỆN
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd " +
            "LEFT JOIN nd.vaiTroSet vt " +
            "LEFT JOIN nd.doiBaoTri dbt " +
            "WHERE (:tuKhoa IS NULL OR " +
            "       LOWER(nd.tenDangNhap) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "       LOWER(nd.hoVaTen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "       LOWER(nd.email) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) " +
            "AND (:tenVaiTro IS NULL OR vt.tenVaiTro = :tenVaiTro) " +
            "AND (:trangThaiHoatDong IS NULL OR nd.trangThaiHoatDong = :trangThaiHoatDong) " +
            "AND (:idDoiBaoTri IS NULL OR dbt.idDoiBaoTri = :idDoiBaoTri)")
    Page<NguoiDung> timKiemNangCao(@Param("tuKhoa") String tuKhoa,
                                   @Param("tenVaiTro") String tenVaiTro,
                                   @Param("trangThaiHoatDong") Boolean trangThaiHoatDong,
                                   @Param("idDoiBaoTri") Long idDoiBaoTri,
                                   Pageable pageable);

    /**
     * ✅ ĐẾM THEO TRẠNG THÁI HOẠT ĐỘNG
     */
    long countByTrangThaiHoatDong(Boolean trangThaiHoatDong);

    /**
     * ✅ ĐẾM THEO VAI TRÒ
     */
    @Query("SELECT COUNT(DISTINCT nd) FROM NguoiDung nd JOIN nd.vaiTroSet vt WHERE vt.tenVaiTro = :tenVaiTro")
    long countByVaiTro(@Param("tenVaiTro") String tenVaiTro);

    /**
     * ✅ TÌM THEO KHOẢNG THỜI GIAN TẠO
     */
    Page<NguoiDung> findByNgayTaoBetween(LocalDateTime tuNgay, LocalDateTime denNgay, Pageable pageable);

    /**
     * ✅ TÌM NGƯỜI DÙNG CÓ THỜI GIAN KHÓA TÀI KHOẢN
     */
    @Query("SELECT nd FROM NguoiDung nd WHERE nd.thoiGianKhoaTaiKhoan IS NOT NULL AND nd.thoiGianKhoaTaiKhoan <= :thoiGian")
    List<NguoiDung> findByThoiGianKhoaTaiKhoanLessThanEqual(@Param("thoiGian") LocalDateTime thoiGian);

    /**
     * ✅ CẬP NHẬT TRẠNG THÁI HOẠT ĐỘNG
     */
    @Modifying
    @Transactional
    @Query("UPDATE NguoiDung nd SET nd.trangThaiHoatDong = :trangThaiHoatDong, nd.ngayCapNhat = CURRENT_TIMESTAMP WHERE nd.idNguoiDung = :idNguoiDung")
    int capNhatTrangThaiHoatDong(@Param("idNguoiDung") Long idNguoiDung, @Param("trangThaiHoatDong") Boolean trangThaiHoatDong);

    /**
     * ✅ CẬP NHẬT LẦN ĐĂNG NHẬP CUỐI
     */
    @Modifying
    @Transactional
    @Query("UPDATE NguoiDung nd SET nd.lanDangNhapCuoi = :lanDangNhapCuoi, nd.soLanDangNhapThatBai = 0 WHERE nd.idNguoiDung = :idNguoiDung")
    int capNhatLanDangNhapCuoi(@Param("idNguoiDung") Long idNguoiDung, @Param("lanDangNhapCuoi") LocalDateTime lanDangNhapCuoi);

    /**
     * ✅ THỐNG KÊ NGƯỜI DÙNG THEO VAI TRÒ
     */
    @Query("SELECT vt.tenVaiTro, COUNT(DISTINCT nd) FROM NguoiDung nd JOIN nd.vaiTroSet vt GROUP BY vt.tenVaiTro")
    List<Object[]> thongKeNguoiDungTheoVaiTro();

    /**
     * ✅ THỐNG KÊ NGƯỜI DÙNG THEO ĐỘI BẢO TRÌ
     */
    @Query("SELECT dbt.tenDoi, COUNT(nd) FROM NguoiDung nd JOIN nd.doiBaoTri dbt GROUP BY dbt.tenDoi")
    List<Object[]> thongKeNguoiDungTheoDoiBaoTri();

    /**
     * ✅ THỐNG KÊ THEO TRẠNG THÁI HOẠT ĐỘNG
     */
    @Query("SELECT nd.trangThaiHoatDong, COUNT(nd) FROM NguoiDung nd GROUP BY nd.trangThaiHoatDong")
    List<Object[]> thongKeTheoTrangThaiHoatDong();

    /**
     * ✅ TOP NGƯỜI DÙNG ĐĂNG NHẬP GẦN NHẤT - SỬA LẠI TÊN METHOD
     */
    @Query("SELECT nd FROM NguoiDung nd WHERE nd.lanDangNhapCuoi IS NOT NULL ORDER BY nd.lanDangNhapCuoi DESC")
    List<NguoiDung> findTopUsersByLastLogin(Pageable pageable);

    /**
     * ✅ CUSTOM QUERY ĐỂ LOAD VỚI VAI TRÒ - SỬA PARAMETER
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd LEFT JOIN FETCH nd.vaiTroSet WHERE nd.idNguoiDung = :idNguoiDung")
    Optional<NguoiDung> findByIdWithVaiTro(@Param("idNguoiDung") Long idNguoiDung);

    /**
     * ✅ CUSTOM QUERY ĐỂ LOAD VỚI VAI TRÒ VÀ QUYỀN
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd " +
            "LEFT JOIN FETCH nd.vaiTroSet vt " +
            "LEFT JOIN FETCH vt.quyenSet " +
            "WHERE nd.tenDangNhap = :tenDangNhap")
    Optional<NguoiDung> findByTenDangNhapWithVaiTroAndQuyen(@Param("tenDangNhap") String tenDangNhap);

    /**
     * ✅ TÌM NGƯỜI DÙNG CÓ VAI TRÒ VÀ HOẠT ĐỘNG
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd " +
            "JOIN nd.vaiTroSet vt " +
            "WHERE vt.tenVaiTro = :tenVaiTro " +
            "AND nd.trangThaiHoatDong = true " +
            "AND nd.taiKhoanKhongBiKhoa = true")
    List<NguoiDung> findActiveUsersByRole(@Param("tenVaiTro") String tenVaiTro);

    /**
     * ✅ TÌM NGƯỜI DÙNG THEO NHIỀU TIÊU CHÍ (CHO ADMIN)
     */
    @Query("SELECT nd FROM NguoiDung nd " +
            "WHERE (:tenDangNhap IS NULL OR LOWER(nd.tenDangNhap) LIKE LOWER(CONCAT('%', :tenDangNhap, '%'))) " +
            "AND (:email IS NULL OR LOWER(nd.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
            "AND (:hoVaTen IS NULL OR LOWER(nd.hoVaTen) LIKE LOWER(CONCAT('%', :hoVaTen, '%'))) " +
            "AND (:trangThaiHoatDong IS NULL OR nd.trangThaiHoatDong = :trangThaiHoatDong) " +
            "AND (:taiKhoanKhongBiKhoa IS NULL OR nd.taiKhoanKhongBiKhoa = :taiKhoanKhongBiKhoa)")
    Page<NguoiDung> findWithFilters(@Param("tenDangNhap") String tenDangNhap,
                                    @Param("email") String email,
                                    @Param("hoVaTen") String hoVaTen,
                                    @Param("trangThaiHoatDong") Boolean trangThaiHoatDong,
                                    @Param("taiKhoanKhongBiKhoa") Boolean taiKhoanKhongBiKhoa,
                                    Pageable pageable);

    /**
     * ✅ ĐẾM NGƯỜI DÙNG MỚI TRONG KHOẢNG THỜI GIAN
     */
    @Query("SELECT COUNT(nd) FROM NguoiDung nd WHERE nd.ngayTao BETWEEN :tuNgay AND :denNgay")
    long countNewUsersInPeriod(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    /**
     * ✅ ĐẾM NGƯỜI DÙNG ĐĂNG NHẬP TRONG KHOẢNG THỜI GIAN
     */
    @Query("SELECT COUNT(nd) FROM NguoiDung nd WHERE nd.lanDangNhapCuoi BETWEEN :tuNgay AND :denNgay")
    long countActiveUsersInPeriod(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    /**
     * ✅ TÌM NGƯỜI DÙNG CÓ NHIỀU VAI TRÒ NHẤT
     */
    @Query("SELECT nd FROM NguoiDung nd WHERE SIZE(nd.vaiTroSet) > 1")
    List<NguoiDung> findUsersWithMultipleRoles();

    /**
     * ✅ TÌM NGƯỜI DÙNG KHÔNG CÓ VAI TRÒ
     */
    @Query("SELECT nd FROM NguoiDung nd WHERE SIZE(nd.vaiTroSet) = 0")
    List<NguoiDung> findUsersWithoutRoles();

    /**
     * ✅ CẬP NHẬT THÔNG TIN CUỐI CÙNG CỦA NGƯỜI DÙNG
     */
    @Modifying
    @Transactional
    @Query("UPDATE NguoiDung nd SET " +
            "nd.lanDangNhapCuoi = :lanDangNhapCuoi, " +
            "nd.soLanDangNhapThatBai = 0, " +
            "nd.ngayCapNhat = CURRENT_TIMESTAMP " +
            "WHERE nd.tenDangNhap = :tenDangNhap")
    int updateLastLoginInfo(@Param("tenDangNhap") String tenDangNhap,
                            @Param("lanDangNhapCuoi") LocalDateTime lanDangNhapCuoi);

    /**
     * ✅ TĂNG SỐ LẦN ĐĂNG NHẬP THẤT BẠI
     */
    @Modifying
    @Transactional
    @Query("UPDATE NguoiDung nd SET " +
            "nd.soLanDangNhapThatBai = nd.soLanDangNhapThatBai + 1, " +
            "nd.ngayCapNhat = CURRENT_TIMESTAMP " +
            "WHERE nd.tenDangNhap = :tenDangNhap")
    int incrementFailedLoginAttempts(@Param("tenDangNhap") String tenDangNhap);

    /**
     * ✅ RESET FAILED LOGIN ATTEMPTS
     */
    @Modifying
    @Transactional
    @Query("UPDATE NguoiDung nd SET " +
            "nd.soLanDangNhapThatBai = 0, " +
            "nd.ngayCapNhat = CURRENT_TIMESTAMP " +
            "WHERE nd.tenDangNhap = :tenDangNhap")
    int resetFailedLoginAttempts(@Param("tenDangNhap") String tenDangNhap);
}