package com.hethong.baotri.kho_du_lieu.thiet_bi;

import com.hethong.baotri.thuc_the.thiet_bi.TrangThaiThietBi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrangThaiThietBiRepository extends JpaRepository<TrangThaiThietBi, Long> {

    Optional<TrangThaiThietBi> findByMaTrangThai(String maTrangThai);

    boolean existsByMaTrangThai(String maTrangThai);

    Page<TrangThaiThietBi> findByTrangThaiHoatDong(Boolean trangThaiHoatDong, Pageable pageable);
}
