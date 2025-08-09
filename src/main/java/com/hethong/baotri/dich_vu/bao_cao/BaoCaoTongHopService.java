package com.hethong.baotri.dich_vu.bao_cao;

import com.hethong.baotri.thuc_the.bao_cao.BaoCao;
import com.hethong.baotri.kho_du_lieu.bao_cao.BaoCaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BaoCaoTongHopService {

    private final BaoCaoRepository repository;

    @Autowired
    public BaoCaoTongHopService(BaoCaoRepository repository) {
        this.repository = repository;
    }

    // Create or update a report
    public BaoCao saveBaoCao(BaoCao baoCao) {
        if (baoCao.getNgayTao() == null) {
            baoCao.setNgayTao(LocalDateTime.now());
        }
        return repository.save(baoCao);
    }

    // Retrieve a report by ID
    public Optional<BaoCao> getBaoCaoById(Long id) {
        return repository.findById(id);
    }

    // Retrieve all reports
    public List<BaoCao> getAllBaoCao() {
        return repository.findAll();
    }

    // Find reports by type
    public List<BaoCao> findByLoaiBaoCao(String loaiBaoCao) {
        return repository.findByLoaiBaoCao(loaiBaoCao);
    }

    // Find reports by status with pagination
    public Page<BaoCao> findByTrangThai(String trangThai, Pageable pageable) {
        return repository.findByTrangThai(trangThai, pageable);
    }

    // Find reports by creator, ordered by creation date
    public List<BaoCao> findByNguoiTao(String nguoiTao) {
        return repository.findByNguoiTaoOrderByNgayTaoDesc(nguoiTao);
    }

    // Find reports within a date range
    public List<BaoCao> findByNgayTaoBetween(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return repository.findByNgayTaoBetween(tuNgay, denNgay);
    }

    // Search reports by keyword with pagination
    public Page<BaoCao> timKiemBaoCao(String tuKhoa, Pageable pageable) {
        return repository.timKiemBaoCao(tuKhoa, pageable);
    }

    // Count reports by status
    public long countByTrangThai(String trangThai) {
        return repository.countByTrangThai(trangThai);
    }

    // Delete a report by ID
    public void deleteBaoCao(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Báo cáo không tồn tại với ID: " + id);
        }
        repository.deleteById(id);
    }
}
