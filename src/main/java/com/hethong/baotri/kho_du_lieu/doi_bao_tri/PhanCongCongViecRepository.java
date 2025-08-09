package com.hethong.baotri.kho_du_lieu.doi_bao_tri;

import com.hethong.baotri.thuc_the.doi_bao_tri.PhanCongCongViec;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PhanCongCongViecRepository extends JpaRepository<PhanCongCongViec, Long> {

    boolean existsByYeuCauBaoTri_IdYeuCauAndNguoiDuocPhanCong_IdNguoiDung(Long idYeuCau, Long idNguoiDung);

    List<PhanCongCongViec> findByYeuCauBaoTri_IdYeuCau(Long idYeuCau);

    List<PhanCongCongViec> findByNguoiDuocPhanCong_IdNguoiDung(Long idNguoiDung);

    List<PhanCongCongViec> findByDoiBaoTri_IdDoiBaoTri(Long idDoiBaoTri);

    Page<PhanCongCongViec> findByTrangThai(String trangThai, Pageable pageable);

    @Query("SELECT pc FROM PhanCongCongViec pc WHERE pc.ngayHoanThanhMongMuon < ?1 AND pc.trangThai NOT IN ('HOAN_THANH', 'HUY')")
    List<PhanCongCongViec> findQuaHan();

    @Query("SELECT pc.trangThai, COUNT(pc) FROM PhanCongCongViec pc GROUP BY pc.trangThai")
    List<Object[]> thongKeTheoTrangThai();
}