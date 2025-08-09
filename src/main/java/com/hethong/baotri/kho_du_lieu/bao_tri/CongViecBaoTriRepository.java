package com.hethong.baotri.kho_du_lieu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.CongViecBaoTri;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CongViecBaoTriRepository extends JpaRepository<CongViecBaoTri, Long> {

    // Tìm theo trạng thái
    Page<CongViecBaoTri> findByTrangThai(String trangThai, Pageable pageable);

    // Tìm theo loại công việc - LOẠI BỎ VÌ KHÔNG CÓ TRƯỜNG NÀY
    // List<CongViecBaoTri> findByLoaiCongViec(String loaiCongViec);

    // Tìm theo người thực hiện
    Page<CongViecBaoTri> findByNguoiThucHien_IdNguoiDung(Long idNguoiDung, Pageable pageable);

    // Tìm theo thiết bị thông qua yêu cầu bảo trì
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE cv.yeuCauBaoTri.thietBi.idThietBi = :idThietBi")
    Page<CongViecBaoTri> findByThietBi_IdThietBi(@Param("idThietBi") Long idThietBi, Pageable pageable);

    // Tìm theo kế hoạch bảo trì - SỬA LỖI CHÍNH
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE cv.yeuCauBaoTri.keHoachBaoTri.idKeHoach = :idKeHoach")
    List<CongViecBaoTri> findByKeHoachBaoTri_IdKeHoach(@Param("idKeHoach") Long idKeHoach);

    // Tìm công việc ưu tiên
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE cv.yeuCauBaoTri.mucDoUuTien >= :mucDo AND cv.trangThai != 'HOAN_THANH'")
    List<CongViecBaoTri> findCongViecUuTien(@Param("mucDo") Integer mucDo);

    // Tìm công việc quá hạn
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE cv.ngayHoanThanh IS NULL AND cv.yeuCauBaoTri.ngayMongMuon < :ngayHienTai AND cv.trangThai != 'HOAN_THANH'")
    List<CongViecBaoTri> findCongViecQuaHan(@Param("ngayHienTai") LocalDateTime ngayHienTai);

    // Đếm theo trạng thái
    long countByTrangThai(String trangThai);

    // Tìm kiếm công việc
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE " +
            "LOWER(cv.tenCongViec) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<CongViecBaoTri> timKiemCongViec(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    // Thống kê theo trạng thái
    @Query("SELECT cv.trangThai, COUNT(cv) FROM CongViecBaoTri cv GROUP BY cv.trangThai")
    List<Object[]> thongKeTheoTrangThai();

    // Tìm công việc theo yêu cầu bảo trì
    List<CongViecBaoTri> findByYeuCauBaoTri_IdYeuCau(Long idYeuCau);

    // Tìm công việc đang thực hiện của người dùng
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE cv.nguoiThucHien.idNguoiDung = :idNguoiDung AND cv.trangThai = 'DANG_THUC_HIEN'")
    List<CongViecBaoTri> findCongViecDangThucHienCuaNguoiDung(@Param("idNguoiDung") Long idNguoiDung);

    // Tìm công việc theo khoảng thời gian
    @Query("SELECT cv FROM CongViecBaoTri cv WHERE cv.ngayBatDau BETWEEN :tuNgay AND :denNgay")
    List<CongViecBaoTri> findByNgayBatDauBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);
}