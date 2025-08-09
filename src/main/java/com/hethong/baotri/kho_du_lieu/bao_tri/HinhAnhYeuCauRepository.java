package com.hethong.baotri.kho_du_lieu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.HinhAnhYeuCau;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhAnhYeuCauRepository extends JpaRepository<HinhAnhYeuCau, Long> {

    List<HinhAnhYeuCau> findByYeuCauBaoTri_IdYeuCau(Long idYeuCau);

    List<HinhAnhYeuCau> findByYeuCauBaoTri_IdYeuCauOrderByThuTuHienThi(Long idYeuCau);

    List<HinhAnhYeuCau> findByLoaiHinhAnh(String loaiHinhAnh);

    @Query("SELECT ha FROM HinhAnhYeuCau ha WHERE ha.yeuCauBaoTri.idYeuCau = :idYeuCau AND ha.laHinhChinh = true")
    List<HinhAnhYeuCau> findHinhChinhCuaYeuCau(@Param("idYeuCau") Long idYeuCau);

    @Query("SELECT COUNT(ha) FROM HinhAnhYeuCau ha WHERE ha.yeuCauBaoTri.idYeuCau = :idYeuCau")
    long demSoLuongHinhAnhCuaYeuCau(@Param("idYeuCau") Long idYeuCau);

    @Query("SELECT SUM(ha.kichThuocFile) FROM HinhAnhYeuCau ha WHERE ha.yeuCauBaoTri.idYeuCau = :idYeuCau")
    Long tinhTongKichThuocFileYeuCau(@Param("idYeuCau") Long idYeuCau);

    void deleteByYeuCauBaoTri_IdYeuCau(Long idYeuCau);
}