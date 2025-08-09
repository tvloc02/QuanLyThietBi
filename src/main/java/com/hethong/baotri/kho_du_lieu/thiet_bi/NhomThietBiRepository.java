package com.hethong.baotri.kho_du_lieu.thiet_bi;

import com.hethong.baotri.thuc_the.thiet_bi.NhomThietBi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhomThietBiRepository extends JpaRepository<NhomThietBi, Long> {

    Optional<NhomThietBi> findByMaNhom(String maNhom);

    boolean existsByMaNhom(String maNhom);

    Page<NhomThietBi> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);

    Page<NhomThietBi> findByTenNhomContainingIgnoreCase(String tenNhom, Pageable pageable);

    List<NhomThietBi> findByNhomThietBiCha_IdNhomThietBi(Long idNhomCha);

    List<NhomThietBi> findByNhomThietBiChaIsNull();
}