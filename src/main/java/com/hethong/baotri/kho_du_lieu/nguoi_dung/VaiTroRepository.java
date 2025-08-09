package com.hethong.baotri.kho_du_lieu.nguoi_dung;

import com.hethong.baotri.thuc_the.nguoi_dung.VaiTro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaiTroRepository extends JpaRepository<VaiTro, Long> {

    Optional<VaiTro> findByTenVaiTro(String tenVaiTro);

    boolean existsByTenVaiTro(String tenVaiTro);

    Page<VaiTro> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    @Query("SELECT vt FROM VaiTro vt WHERE LOWER(vt.tenVaiTro) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR LOWER(vt.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<VaiTro> timKiemVaiTro(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    @Query("SELECT COUNT(vt) FROM VaiTro vt WHERE vt.trangThaiHoatDong = :trangThai")
    long countByTrangThaiHoatDong(@Param("trangThai") Boolean trangThai);

    @Query("SELECT vt FROM VaiTro vt JOIN vt.quyenSet q WHERE q.tenQuyen = :tenQuyen")
    List<VaiTro> findByQuyen(@Param("tenQuyen") String tenQuyen);


}