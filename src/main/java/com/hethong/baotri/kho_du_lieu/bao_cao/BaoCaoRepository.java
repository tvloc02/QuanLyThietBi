package com.hethong.baotri.kho_du_lieu.bao_cao;

import com.hethong.baotri.thuc_the.bao_cao.BaoCao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BaoCaoRepository extends JpaRepository<BaoCao, Long> {

    // Tìm báo cáo theo loại
    List<BaoCao> findByLoaiBaoCao(String loaiBaoCao);

    // Tìm báo cáo theo trạng thái
    Page<BaoCao> findByTrangThai(String trangThai, Pageable pageable);

    // Tìm báo cáo theo người tạo
    List<BaoCao> findByNguoiTaoOrderByNgayTaoDesc(String nguoiTao);

    // Tìm báo cáo trong khoảng thời gian
    @Query("SELECT b FROM BaoCao b WHERE b.ngayTao BETWEEN :tuNgay AND :denNgay")
    List<BaoCao> findByNgayTaoBetween(@Param("tuNgay") LocalDateTime tuNgay, @Param("denNgay") LocalDateTime denNgay);

    // Tìm kiếm báo cáo
    @Query("SELECT b FROM BaoCao b WHERE " +
            "LOWER(b.tenBaoCao) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
            "LOWER(b.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))")
    Page<BaoCao> timKiemBaoCao(@Param("tuKhoa") String tuKhoa, Pageable pageable);

    // Đếm báo cáo theo trạng thái
    long countByTrangThai(String trangThai);
}