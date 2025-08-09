package com.hethong.baotri.kho_du_lieu.doi_bao_tri;

import com.hethong.baotri.thuc_the.doi_bao_tri.ThanhVienDoi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThanhVienDoiRepository extends JpaRepository<ThanhVienDoi, Long> {

    List<ThanhVienDoi> findByDoiBaoTri_IdDoiBaoTri(Long idDoiBaoTri);

    List<ThanhVienDoi> findByNguoiDung_IdNguoiDung(Long idNguoiDung);

    List<ThanhVienDoi> findByTrangThaiHoatDong(Boolean trangThaiHoatDong);

    @Query("SELECT tv FROM ThanhVienDoi tv WHERE tv.doiBaoTri.idDoiBaoTri = :idDoi AND tv.trangThaiHoatDong = true")
    List<ThanhVienDoi> findThanhVienHoatDongCuaDoi(@Param("idDoi") Long idDoi);

    @Query("SELECT COUNT(tv) FROM ThanhVienDoi tv WHERE tv.doiBaoTri.idDoiBaoTri = :idDoi AND tv.trangThaiHoatDong = true")
    long countThanhVienHoatDong(@Param("idDoi") Long idDoi);
}