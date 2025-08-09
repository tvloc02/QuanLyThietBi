package com.hethong.baotri.kho_du_lieu.vat_tu;

import com.hethong.baotri.thuc_the.vat_tu.PhuTung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PhuTungRepository extends JpaRepository<PhuTung, Long> {

    Page<PhuTung> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<PhuTung> findByTenPhuTungContainingIgnoreCase(String tenPhuTung, Pageable pageable);

    List<PhuTung> findByPhuTungQuanTrong(Boolean phuTungQuanTrong);

    @Query("SELECT pt FROM PhuTung pt WHERE pt.ngayThayTheTiepTheo <= :ngayHienTai")
    List<PhuTung> findPhuTungCanThayThe(@Param("ngayHienTai") LocalDate ngayHienTai);

    @Query("SELECT pt FROM PhuTung pt WHERE pt.ngayThayTheTiepTheo BETWEEN :tuNgay AND :denNgay")
    List<PhuTung> findPhuTungCanThayTheTrongKhoang(@Param("tuNgay") LocalDate tuNgay,
                                                   @Param("denNgay") LocalDate denNgay);

    @Query("SELECT COUNT(pt) FROM PhuTung pt WHERE pt.mucDoQuanTrong >= :mucDo")
    long countByMucDoQuanTrongGreaterThanEqual(@Param("mucDo") Integer mucDo);

    List<PhuTung> findByMucDoQuanTrong(Integer mucDoQuanTrong);

    List<PhuTung> findByThietBiApDungSet_IdThietBi(Long idThietBi);
}