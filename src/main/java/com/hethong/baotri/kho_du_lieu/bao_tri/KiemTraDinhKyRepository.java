// KiemTraDinhKyRepository - sửa lỗi kiểu dữ liệu
package com.hethong.baotri.kho_du_lieu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.KiemTraDinhKy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KiemTraDinhKyRepository extends JpaRepository<KiemTraDinhKy, Long> {

    // Tìm theo thiết bị
    Page<KiemTraDinhKy> findByThietBi_IdThietBi(Long idThietBi, Pageable pageable);

    // Tìm theo người kiểm tra
    Page<KiemTraDinhKy> findByNguoiKiemTra_IdNguoiDung(Long idNguoiDung, Pageable pageable);

    // Tìm theo trạng thái
    List<KiemTraDinhKy> findByTrangThai(String trangThai);

    // Tìm theo loại kiểm tra
    List<KiemTraDinhKy> findByLoaiKiemTra(String loaiKiemTra);

    // Tìm kiểm tra cần thực hiện
    @Query("SELECT kt FROM KiemTraDinhKy kt WHERE kt.ngayKiemTraTiepTheo <= :ngayHienTai AND kt.trangThai != 'HOAN_THANH'")
    List<KiemTraDinhKy> findKiemTraCanThucHien(@Param("ngayHienTai") LocalDateTime ngayHienTai);

    // Tìm trong khoảng thời gian
    @Query("SELECT kt FROM KiemTraDinhKy kt WHERE kt.ngayKiemTra BETWEEN :tuNgay AND :denNgay")
    List<KiemTraDinhKy> findByNgayKiemTraBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    // Đếm theo trạng thái
    long countByTrangThai(String trangThai);

    // Tìm kiểm tra quá hạn
    @Query("SELECT kt FROM KiemTraDinhKy kt WHERE kt.ngayKiemTraTiepTheo < :ngayHienTai AND kt.trangThai != 'HOAN_THANH'")
    List<KiemTraDinhKy> findKiemTraQuaHan(@Param("ngayHienTai") LocalDateTime ngayHienTai);

    // Thống kê theo loại kiểm tra
    @Query("SELECT kt.loaiKiemTra, COUNT(kt) FROM KiemTraDinhKy kt GROUP BY kt.loaiKiemTra")
    List<Object[]> thongKeTheoLoaiKiemTra();
}