package com.hethong.baotri.kho_du_lieu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.CanhBaoLoi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CanhBaoLoiRepository extends JpaRepository<CanhBaoLoi, Long> {

    // Kiểm tra mã cảnh báo tồn tại
    boolean existsByMaCanhBao(String maCanhBao);

    // Tìm theo mức độ nghiêm trọng
    List<CanhBaoLoi> findByMucDoNghiemTrong(Integer mucDoNghiemTrong);

    // Tìm theo trạng thái
    Page<CanhBaoLoi> findByTrangThai(String trangThai, Pageable pageable);

    // Tìm theo thiết bị
    Page<CanhBaoLoi> findByThietBi_IdThietBi(Long idThietBi, Pageable pageable);

    // Tìm cảnh báo chưa xử lý
    @Query("SELECT cb FROM CanhBaoLoi cb WHERE cb.trangThai = 'CHO_XU_LY' ORDER BY cb.mucDoNghiemTrong DESC, cb.ngayPhatSinh ASC")
    List<CanhBaoLoi> findCanhBaoChuaXuLy();

    // Tìm cảnh báo trong khoảng thời gian
    @Query("SELECT cb FROM CanhBaoLoi cb WHERE cb.ngayPhatSinh BETWEEN :tuNgay AND :denNgay")
    List<CanhBaoLoi> findByNgayPhatSinhBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    // Đếm cảnh báo theo trạng thái
    long countByTrangThai(String trangThai);

    // Thống kê theo mức độ nghiêm trọng
    @Query("SELECT cb.mucDoNghiemTrong, COUNT(cb) FROM CanhBaoLoi cb GROUP BY cb.mucDoNghiemTrong")
    List<Object[]> thongKeTheoMucDoNghiemTrong();

    // ===== CÁC METHOD BỔ SUNG =====

    // Tìm cảnh báo cần thông báo ngay
    @Query("SELECT cb FROM CanhBaoLoi cb WHERE cb.canThongBaoNgay = true AND cb.daGuiThongBao = false")
    List<CanhBaoLoi> findCanhBaoCanThongBaoNgay();

    // Tìm cảnh báo tự động
    List<CanhBaoLoi> findByTuDongTao(Boolean tuDongTao);

    // Tìm cảnh báo theo loại
    List<CanhBaoLoi> findByLoaiCanhBao(String loaiCanhBao);

    // Tìm cảnh báo theo người xử lý
    Page<CanhBaoLoi> findByNguoiXuLy_IdNguoiDung(Long idNguoiXuLy, Pageable pageable);

    // Tìm cảnh báo chưa có người xử lý
    @Query("SELECT cb FROM CanhBaoLoi cb WHERE cb.nguoiXuLy IS NULL AND cb.trangThai = 'CHO_XU_LY'")
    List<CanhBaoLoi> findCanhBaoChuaCoNguoiXuLy();

    // Tìm cảnh báo nghiêm trọng
    @Query("SELECT cb FROM CanhBaoLoi cb WHERE cb.mucDoNghiemTrong >= :mucDo ORDER BY cb.mucDoNghiemTrong DESC, cb.ngayPhatSinh DESC")
    List<CanhBaoLoi> findCanhBaoNghiemTrong(@Param("mucDo") Integer mucDo);

    // Thống kê cảnh báo theo loại
    @Query("SELECT cb.loaiCanhBao, COUNT(cb) FROM CanhBaoLoi cb GROUP BY cb.loaiCanhBao")
    List<Object[]> thongKeTheoLoaiCanhBao();

    // Thống kê cảnh báo theo thiết bị
    @Query("SELECT tb.tenThietBi, COUNT(cb) FROM CanhBaoLoi cb JOIN cb.thietBi tb GROUP BY tb.tenThietBi ORDER BY COUNT(cb) DESC")
    List<Object[]> thongKeTheoThietBi();

    // Tìm cảnh báo lặp lại cho thiết bị
    @Query("SELECT cb FROM CanhBaoLoi cb WHERE cb.thietBi.idThietBi = :idThietBi AND cb.loaiCanhBao = :loaiCanhBao AND cb.ngayPhatSinh >= :tuNgay")
    List<CanhBaoLoi> findCanhBaoLapLaiChoThietBi(@Param("idThietBi") Long idThietBi,
                                                 @Param("loaiCanhBao") String loaiCanhBao,
                                                 @Param("tuNgay") LocalDateTime tuNgay);
}