package com.hethong.baotri.kho_du_lieu.vat_tu;

import com.hethong.baotri.thuc_the.vat_tu.KhoVatTu;
import com.hethong.baotri.thuc_the.vat_tu.VatTu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhoVatTuRepository extends JpaRepository<KhoVatTu, Long> {

    Optional<KhoVatTu> findByMaKho(String maKho);

    Optional<KhoVatTu> findByMaKhoAndVatTu(String maKho, VatTu vatTu);

    Page<KhoVatTu> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<KhoVatTu> findByVatTu_IdVatTu(Long idVatTu, Pageable pageable);

    @Query("SELECT kv FROM KhoVatTu kv WHERE kv.soLuongKhaDung <= 0")
    List<KhoVatTu> findKhoHetHang();

    @Query("SELECT kv FROM KhoVatTu kv WHERE kv.soLuongTon <= 10")
    List<KhoVatTu> findKhoSapHetHang();

    @Query("SELECT SUM(kv.giaTriTonKho) FROM KhoVatTu kv WHERE kv.trangThaiHoatDong = true")
    java.math.BigDecimal tinhTongGiaTriTonKho();
}