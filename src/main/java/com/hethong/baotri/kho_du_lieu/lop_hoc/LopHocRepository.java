package com.hethong.baotri.kho_du_lieu.lop_hoc;

import com.hethong.baotri.thuc_the.lop_hoc.LopHoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LopHocRepository extends JpaRepository<LopHoc, Long> {

    Optional<LopHoc> findByMaLop(String maLop);

    boolean existsByMaLop(String maLop);

    Page<LopHoc> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<LopHoc> findByTenLopContainingIgnoreCase(String tenLop, Pageable pageable);

    List<LopHoc> findByKhoi(String khoi);

    List<LopHoc> findByNamHoc(String namHoc);

    Page<LopHoc> findByGiaoVienChuNhiem_IdNguoiDung(Long idGiaoVien, Pageable pageable);

    List<LopHoc> findByGiaoVienChuNhiem_IdNguoiDung(Long idGiaoVien);

    @Query("SELECT lh FROM LopHoc lh WHERE lh.giaoVienChuNhiem IS NULL")
    List<LopHoc> findLopChuaCoPhanCongGiaoVien();

    @Query("SELECT lh FROM LopHoc lh WHERE " +
            "LOWER(lh.tenLop) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(lh.maLop) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(lh.phongHoc) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<LopHoc> timKiemLopHoc(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("SELECT COUNT(lh) FROM LopHoc lh WHERE lh.trangThaiHoatDong = true")
    long demLopHocHoatDong();

    @Query("SELECT lh.khoi, COUNT(lh) FROM LopHoc lh GROUP BY lh.khoi")
    List<Object[]> thongKeTheoKhoi();

    @Query("SELECT lh FROM LopHoc lh JOIN lh.thietBiTrongLopSet tb WHERE tb.idThietBi = :idThietBi")
    Optional<LopHoc> findByThietBi(@Param("idThietBi") Long idThietBi);

    @Query("SELECT COUNT(tb) FROM LopHoc lh JOIN lh.thietBiTrongLopSet tb WHERE lh.idLopHoc = :idLopHoc")
    long demThietBiTrongLop(@Param("idLopHoc") Long idLopHoc);

    @Query("SELECT COUNT(yc) FROM LopHoc lh JOIN lh.yeuCauBaoTriSet yc WHERE lh.idLopHoc = :idLopHoc")
    long demYeuCauBaoTriCuaLop(@Param("idLopHoc") Long idLopHoc);

    @Query("SELECT COUNT(yc) FROM LopHoc lh JOIN lh.yeuCauBaoTriSet yc WHERE lh.idLopHoc = :idLopHoc AND yc.trangThai IN ('CHO_DUYET', 'DA_DUYET', 'DANG_XU_LY')")
    long demYeuCauChuaXuLyCuaLop(@Param("idLopHoc") Long idLopHoc);

    @Query("SELECT lh, COUNT(yc) FROM LopHoc lh LEFT JOIN lh.yeuCauBaoTriSet yc GROUP BY lh ORDER BY COUNT(yc) DESC")
    List<Object[]> findTopLopTheoSoYeuCau(Pageable pageable);

    @Query("SELECT lh FROM LopHoc lh WHERE " +
            "(:tenLop IS NULL OR LOWER(lh.tenLop) LIKE LOWER(CONCAT('%', :tenLop, '%'))) AND " +
            "(:khoi IS NULL OR lh.khoi = :khoi) AND " +
            "(:namHoc IS NULL OR lh.namHoc = :namHoc) AND " +
            "(:idGiaoVien IS NULL OR lh.giaoVienChuNhiem.idNguoiDung = :idGiaoVien) AND " +
            "(:trangThaiHoatDong IS NULL OR lh.trangThaiHoatDong = :trangThaiHoatDong)")
    Page<LopHoc> timKiemNangCao(@Param("tenLop") String tenLop,
                                @Param("khoi") String khoi,
                                @Param("namHoc") String namHoc,
                                @Param("idGiaoVien") Long idGiaoVien,
                                @Param("trangThaiHoatDong") Boolean trangThaiHoatDong,
                                Pageable pageable);
}