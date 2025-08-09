package com.hethong.baotri.kho_du_lieu.thiet_bi;

import com.hethong.baotri.thuc_the.thiet_bi.ThietBi;
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
public interface ThietBiRepository extends JpaRepository<ThietBi, Long> {

    Optional<ThietBi> findByMaThietBi(String maThietBi);

    boolean existsByMaThietBi(String maThietBi);

    Page<ThietBi> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<ThietBi> findByTenThietBiContainingIgnoreCase(String tenThietBi, Pageable pageable);

    Page<ThietBi> findByNhomThietBi_IdNhomThietBi(Long idNhomThietBi, Pageable pageable);

    Page<ThietBi> findByTrangThaiThietBi_IdTrangThai(Long idTrangThai, Pageable pageable);

    // *** THÊM MỚI CHO LỚP HỌC ***
    Page<ThietBi> findByLopHoc_IdLopHoc(Long idLopHoc, Pageable pageable);

    List<ThietBi> findByLopHoc_IdLopHoc(Long idLopHoc);

    @Query("SELECT tb FROM ThietBi tb WHERE tb.lopHoc IS NULL")
    List<ThietBi> findThietBiChuaGanLopHoc();

    @Query("SELECT COUNT(tb) FROM ThietBi tb WHERE tb.lopHoc.idLopHoc = :idLopHoc")
    long demThietBiTrongLop(@Param("idLopHoc") Long idLopHoc);

    @Query("SELECT COUNT(tb) FROM ThietBi tb WHERE tb.lopHoc.idLopHoc = :idLopHoc AND tb.trangThaiHoatDong = true")
    long demThietBiHoatDongTrongLop(@Param("idLopHoc") Long idLopHoc);

    @Query("SELECT tb FROM ThietBi tb WHERE tb.lopHoc.giaoVienChuNhiem.idNguoiDung = :idGiaoVien")
    List<ThietBi> findThietBiCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT COUNT(tb) FROM ThietBi tb WHERE tb.lopHoc.giaoVienChuNhiem.idNguoiDung = :idGiaoVien")
    long demThietBiCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien);

    @Query("SELECT tb FROM ThietBi tb WHERE tb.lopHoc.giaoVienChuNhiem.idNguoiDung = :idGiaoVien AND tb.lanBaoTriTiepTheo <= :ngayHienTai")
    List<ThietBi> findThietBiCanBaoTriCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien, @Param("ngayHienTai") LocalDateTime ngayHienTai, Pageable pageable);

    @Query("SELECT COUNT(tb) FROM ThietBi tb WHERE tb.lopHoc.giaoVienChuNhiem.idNguoiDung = :idGiaoVien AND tb.lanBaoTriTiepTheo <= :ngayHienTai")
    long demThietBiCanBaoTriCuaGiaoVien(@Param("idGiaoVien") Long idGiaoVien, @Param("ngayHienTai") LocalDateTime ngayHienTai);

    // *** CÁC QUERY CŨ GIỮ NGUYÊN ***
    @Query("SELECT tb FROM ThietBi tb WHERE tb.lanBaoTriTiepTheo <= :ngayHienTai")
    List<ThietBi> findThietBiCanBaoTri(@Param("ngayHienTai") LocalDateTime ngayHienTai);

    @Query("SELECT tb FROM ThietBi tb WHERE tb.lanBaoTriTiepTheo BETWEEN :tuNgay AND :denNgay")
    List<ThietBi> findThietBiCanBaoTriTrongKhoang(@Param("tuNgay") LocalDateTime tuNgay,
                                                  @Param("denNgay") LocalDateTime denNgay);

    @Query("SELECT tb FROM ThietBi tb WHERE " +
            "LOWER(tb.tenThietBi) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(tb.maThietBi) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<ThietBi> timKiemThietBi(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("SELECT COUNT(tb) FROM ThietBi tb WHERE tb.trangThaiHoatDong = true")
    long demThietBiHoatDong();

    @Query("SELECT AVG(tb.soGioHoatDong) FROM ThietBi tb WHERE tb.trangThaiHoatDong = true")
    Double tinhTrungBinhGioHoatDong();

    @Query("SELECT tb FROM ThietBi tb WHERE " +
            "(:tenThietBi IS NULL OR LOWER(tb.tenThietBi) LIKE LOWER(CONCAT('%', :tenThietBi, '%'))) AND " +
            "(:maThietBi IS NULL OR LOWER(tb.maThietBi) LIKE LOWER(CONCAT('%', :maThietBi, '%'))) AND " +
            "(:idNhomThietBi IS NULL OR tb.nhomThietBi.idNhomThietBi = :idNhomThietBi) AND " +
            "(:idLopHoc IS NULL OR tb.lopHoc.idLopHoc = :idLopHoc) AND " +
            "(:trangThaiHoatDong IS NULL OR tb.trangThaiHoatDong = :trangThaiHoatDong)")
    Page<ThietBi> timKiemNangCao(@Param("tenThietBi") String tenThietBi,
                                 @Param("maThietBi") String maThietBi,
                                 @Param("idNhomThietBi") Long idNhomThietBi,
                                 @Param("idLopHoc") Long idLopHoc,
                                 @Param("trangThaiHoatDong") Boolean trangThaiHoatDong,
                                 Pageable pageable);
}