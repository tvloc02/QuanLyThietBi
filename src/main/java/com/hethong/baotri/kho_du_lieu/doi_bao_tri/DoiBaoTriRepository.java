package com.hethong.baotri.kho_du_lieu.doi_bao_tri;

import com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoiBaoTriRepository extends JpaRepository<DoiBaoTri, Long> {

    Optional<DoiBaoTri> findByMaDoi(String maDoi);

    boolean existsByMaDoi(String maDoi);

    Page<DoiBaoTri> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<DoiBaoTri> findByTenDoiContainingIgnoreCase(String tenDoi, Pageable pageable);

    List<DoiBaoTri> findByChuyenMon(String chuyenMon);

    List<DoiBaoTri> findByCaLamViec(String caLamViec);

    @Query("SELECT d FROM DoiBaoTri d WHERE d.mucDoBanRon <= :mucDo AND d.trangThaiHoatDong = true")
    List<DoiBaoTri> findDoiRanh(@Param("mucDo") Integer mucDo);

    @Query("SELECT COUNT(d) FROM DoiBaoTri d WHERE d.trangThaiHoatDong = :trangThai")
    long countByTrangThaiHoatDong(@Param("trangThai") Boolean trangThai);

    @Query("SELECT d.chuyenMon, COUNT(d) FROM DoiBaoTri d GROUP BY d.chuyenMon")
    List<Object[]> thongKeTheoChuyenMon();

    // Tìm theo trưởng đội
    Page<DoiBaoTri> findByTruongDoi_IdNguoiDung(Long idNguoiDung, Pageable pageable);

    // Tìm theo phó đội
    Page<DoiBaoTri> findByPhoDoi_IdNguoiDung(Long idNguoiDung, Pageable pageable);
}