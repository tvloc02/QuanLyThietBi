package com.hethong.baotri.dich_vu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.KiemTraDinhKy;
import com.hethong.baotri.kho_du_lieu.bao_tri.KiemTraDinhKyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class KiemTraDinhKyService {

    private final KiemTraDinhKyRepository repository;

    @Autowired
    public KiemTraDinhKyService(KiemTraDinhKyRepository repository) {
        this.repository = repository;
    }

    // Create or update a periodic inspection
    public KiemTraDinhKy saveKiemTraDinhKy(KiemTraDinhKy kiemTra) {
        return repository.save(kiemTra);
    }

    // Retrieve an inspection by ID
    public Optional<KiemTraDinhKy> getKiemTraDinhKyById(Long id) {
        return repository.findById(id);
    }

    // Retrieve all inspections
    public List<KiemTraDinhKy> getAllKiemTraDinhKy() {
        return repository.findAll();
    }

    // Find inspections by device with pagination
    public Page<KiemTraDinhKy> findByThietBi(Long idThietBi, Pageable pageable) {
        return repository.findByThietBi_IdThietBi(idThietBi, pageable);
    }

    // Find inspections by inspector with pagination
    public Page<KiemTraDinhKy> findByNguoiKiemTra(Long idNguoiDung, Pageable pageable) {
        return repository.findByNguoiKiemTra_IdNguoiDung(idNguoiDung, pageable);
    }

    // Find inspections by status
    public List<KiemTraDinhKy> findByTrangThai(String trangThai) {
        return repository.findByTrangThai(trangThai);
    }

    // Find inspections by type
    public List<KiemTraDinhKy> findByLoaiKiemTra(String loaiKiemTra) {
        return repository.findByLoaiKiemTra(loaiKiemTra);
    }

    // Find inspections due
    public List<KiemTraDinhKy> findKiemTraCanThucHien(LocalDateTime ngayHienTai) {
        return repository.findKiemTraCanThucHien(ngayHienTai);
    }

    // Find inspections within a date range
    public List<KiemTraDinhKy> findByNgayKiemTraBetween(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return repository.findByNgayKiemTraBetween(tuNgay, denNgay);
    }

    // Count inspections by status
    public long countByTrangThai(String trangThai) {
        return repository.countByTrangThai(trangThai);
    }

    // Find overdue inspections
    public List<KiemTraDinhKy> findKiemTraQuaHan(LocalDateTime ngayHienTai) {
        return repository.findKiemTraQuaHan(ngayHienTai);
    }

    // Statistics by inspection type
    public List<Object[]> thongKeTheoLoaiKiemTra() {
        return repository.thongKeTheoLoaiKiemTra();
    }

    // Delete an inspection
    public void deleteKiemTraDinhKy(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Kiểm tra định kỳ không tồn tại với ID: " + id);
        }
        repository.deleteById(id);
    }
}
