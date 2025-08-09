package com.hethong.baotri.kho_du_lieu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface YeuCauBaoTriRepository extends JpaRepository<YeuCauBaoTri, Long> {

    boolean existsByMaYeuCau(String maYeuCau);

    Page<YeuCauBaoTri> findByTrangThai(String trangThai, Pageable pageable);

    Page<YeuCauBaoTri> findByNguoiTao_IdNguoiDung(Long idNguoiDung, Pageable pageable);

    Page<YeuCauBaoTri> findByThietBi_IdThietBi(Long idThietBi, Pageable pageable);

    // *** THÊM MỚI CHO LỚP HỌC ***
    Page<YeuCauBaoTri> findByLopHoc_IdLopHoc(Long idLopHoc, Pageable pageable);

    List<YeuCauBaoTri> findByLopHoc_IdLopHoc(Long idLopHoc);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.lopHoc.idLopHoc = :idLopHoc AND yc.trangThai = :trangThai")
    List<YeuCauBaoTri> findByLopHocAndTrangThai(@Param("idLopHoc") Long idLopHoc, @Param("trangThai") String trangThai);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien AND yc.lopHoc IS NOT NULL")
    List<YeuCauBaoTri> findYeuCauTuGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien AND yc.trangThai IN ('CHO_DUYET', 'DA_DUYET', 'DANG_XU_LY')")
    List<YeuCauBaoTri> findYeuCauChuaXuLyCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien")
    long demYeuCauCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien AND yc.trangThai IN ('CHO_DUYET', 'DA_DUYET', 'DANG_XU_LY')")
    long demYeuCauChuaXuLyCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT COUNT(tb) FROM YeuCauBaoTri yc JOIN yc.thietBi tb WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien")
    long demThietBiCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien ORDER BY yc.ngayTao DESC")
    List<YeuCauBaoTri> findYeuCauGanDayCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien, Pageable pageable);

    @Query("SELECT tb FROM YeuCauBaoTri yc JOIN yc.thietBi tb WHERE yc.nguoiTao.idNguoiDung = :idGiaoVien AND tb.lanBaoTriTiepTheo <= CURRENT_TIMESTAMP")
    List<Object> findThietBiCanBaoTriCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien, Pageable pageable);

    // *** CÁC QUERY CŨ GIỮ NGUYÊN ***
    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.trangThai = :trangThai AND yc.mucDoUuTien = :mucDoUuTien")
    List<YeuCauBaoTri> findYeuCauUuTien(@Param("trangThai") String trangThai, @Param("mucDoUuTien") String mucDoUuTien);

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.trangThai = :trangThai")
    long demYeuCauTheoTrangThai(@Param("trangThai") String trangThai);

    @Query("SELECT yc.trangThai, COUNT(yc) FROM YeuCauBaoTri yc GROUP BY yc.trangThai")
    List<Object[]> thongKeYeuCauTheoTrangThai();

    @Query("SELECT yc.mucDoUuTien, COUNT(yc) FROM YeuCauBaoTri yc GROUP BY yc.mucDoUuTien")
    List<Object[]> thongKeYeuCauTheoUuTien();

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.trangThai = 'CHO_DUYET' ORDER BY yc.mucDoUuTien DESC, yc.ngayTao ASC")
    List<YeuCauBaoTri> findYeuCauCanDuyet(Pageable pageable);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.nguoiXuLy.idNguoiDung = :idNguoiXuLy AND yc.trangThai IN ('DA_DUYET', 'DANG_XU_LY')")
    List<YeuCauBaoTri> findYeuCauDuocPhanCong(@Param("idNguoiXuLy") Long idNguoiXuLy, Pageable pageable);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.nguoiXuLy.idNguoiDung = :idNguoiXuLy AND yc.trangThai = 'DANG_XU_LY'")
    List<YeuCauBaoTri> findYeuCauDangXuLy(@Param("idNguoiXuLy") Long idNguoiXuLy);

    @Query("SELECT yc FROM YeuCauBaoTri yc WHERE yc.nguoiXuLy.idNguoiDung = :idNguoiXuLy AND yc.ngayHoanThanh IS NOT NULL ORDER BY yc.ngayHoanThanh DESC")
    List<YeuCauBaoTri> findLichSuHoanThanh(@Param("idNguoiXuLy") Long idNguoiXuLy, Pageable pageable);

    @Query("SELECT YEAR(yc.ngayTao), MONTH(yc.ngayTao), COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.ngayTao >= :tuNgay GROUP BY YEAR(yc.ngayTao), MONTH(yc.ngayTao) ORDER BY YEAR(yc.ngayTao) DESC, MONTH(yc.ngayTao) DESC")
    List<Object[]> thongKeTheoThang(@Param("tuNgay") LocalDateTime tuNgay);

    @Query("SELECT lh.tenLop, COUNT(yc) FROM YeuCauBaoTri yc JOIN yc.lopHoc lh GROUP BY lh.tenLop ORDER BY COUNT(yc) DESC")
    List<Object[]> thongKeTopLopHoc(Pageable pageable);

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc")
    long demTongSoYeuCau();

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.trangThai = 'HOAN_THANH'")
    long demYeuCauHoanThanh();

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.trangThai IN ('CHO_DUYET', 'DA_DUYET', 'DANG_XU_LY')")
    long demYeuCauChuaXuLy();

    @Query("SELECT COUNT(yc) FROM YeuCauBaoTri yc WHERE yc.mucDoUuTien = 'RAT_KHAN' AND yc.trangThai != 'HOAN_THANH'")
    long demYeuCauKhanCap();
}