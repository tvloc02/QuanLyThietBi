package com.hethong.baotri.kho_du_lieu.vat_tu;

import com.hethong.baotri.thuc_the.vat_tu.NhomVatTu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhomVatTuRepository extends JpaRepository<NhomVatTu, Long> {

    Optional<NhomVatTu> findByMaNhom(String maNhom);

    boolean existsByMaNhom(String maNhom);

    Page<NhomVatTu> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<NhomVatTu> findByTenNhomContainingIgnoreCase(String tenNhom, Pageable pageable);

    List<NhomVatTu> findByNhomVatTuCha_IdNhomVatTu(Long idNhomCha);

    List<NhomVatTu> findByNhomVatTuChaIsNull();
}