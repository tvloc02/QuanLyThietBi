package com.hethong.baotri.kho_du_lieu.vat_tu;

import com.hethong.baotri.thuc_the.vat_tu.VatTu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VatTuRepository extends JpaRepository<VatTu, Long> {

    Optional<VatTu> findByMaVatTu(String maVatTu);

    boolean existsByMaVatTu(String maVatTu);

    Page<VatTu> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<VatTu> findByTenVatTuContainingIgnoreCase(String tenVatTu, Pageable pageable);

    Page<VatTu> findByNhomVatTu_IdNhomVatTu(Long idNhomVatTu, Pageable pageable);

    @Query("SELECT vt FROM VatTu vt WHERE vt.soLuongTonKho <= vt.soLuongTonToiThieu")
    List<VatTu> findVatTuThieuHang();

    @Query("SELECT vt FROM VatTu vt WHERE vt.ngayHetHan <= :ngayHienTai")
    List<VatTu> findVatTuHetHan(@Param("ngayHienTai") LocalDate ngayHienTai);

    @Query("SELECT vt FROM VatTu vt WHERE vt.ngayKiemTraTiepTheo <= :ngayHienTai")
    List<VatTu> findVatTuCanKiemTra(@Param("ngayHienTai") LocalDate ngayHienTai);

    @Query("SELECT COUNT(vt) FROM VatTu vt WHERE vt.soLuongTonKho <= vt.soLuongTonToiThieu")
    long demVatTuThieuHang();

    @Query("SELECT SUM(vt.soLuongTonKho * vt.giaNhap) FROM VatTu vt WHERE vt.trangThaiHoatDong = true")
    java.math.BigDecimal tinhTongGiaTriTonKho();

    // Tìm theo mức độ quan trọng
    List<VatTu> findByMucDoQuanTrong(Integer mucDoQuanTrong);

    // Tìm theo loại vật tư
    Page<VatTu> findByLoaiVatTu(String loaiVatTu, Pageable pageable);

    // Tìm vật tư quan trọng
    List<VatTu> findByVatTuQuanTrong(Boolean vatTuQuanTrong);

    // Tìm vật tư yêu cầu kiểm tra
    List<VatTu> findByYeuCauKiemTra(Boolean yeuCauKiemTra);
}