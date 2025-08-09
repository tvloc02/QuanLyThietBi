package com.hethong.baotri.kho_du_lieu.nguoi_dung;

import com.hethong.baotri.thuc_the.nguoi_dung.Quyen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuyenRepository extends JpaRepository<Quyen, Long> {

    Optional<Quyen> findByTenQuyen(String tenQuyen);

    boolean existsByTenQuyen(String tenQuyen);

    Page<Quyen> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<Quyen> findByNhomQuyen(String nhomQuyen, Pageable pageable);

    @Query("SELECT q FROM Quyen q WHERE LOWER(q.tenQuyen) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(q.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<Quyen> timKiemQuyen(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("SELECT COUNT(q) FROM Quyen q WHERE q.trangThaiHoatDong = :trangThai")
    long countByTrangThaiHoatDong(@Param("trangThai") Boolean trangThai);

    @Query("SELECT q.nhomQuyen, COUNT(q) FROM Quyen q GROUP BY q.nhomQuyen")
    List<Object[]> thongKeQuyenTheoNhom();

    List<Quyen> findByNhomQuyenOrderByTenQuyen(String nhomQuyen);
}