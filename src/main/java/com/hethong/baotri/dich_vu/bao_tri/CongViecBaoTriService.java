package com.hethong.baotri.dich_vu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.CongViecBaoTri;
import com.hethong.baotri.kho_du_lieu.bao_tri.CongViecBaoTriRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CongViecBaoTriService {

    private final CongViecBaoTriRepository repository;

    @Autowired
    public CongViecBaoTriService(CongViecBaoTriRepository repository) {
        this.repository = repository;
    }

    // Create or update a maintenance task
    public CongViecBaoTri saveCongViecBaoTri(CongViecBaoTri congViec) {
        return repository.save(congViec);
    }

    // Retrieve a task by ID
    public Optional<CongViecBaoTri> getCongViecBaoTriById(Long id) {
        return repository.findById(id);
    }

    // Retrieve all tasks
    public List<CongViecBaoTri> getAllCongViecBaoTri() {
        return repository.findAll();
    }

    // Find tasks by status with pagination
    public Page<CongViecBaoTri> findByTrangThai(String trangThai, Pageable pageable) {
        return repository.findByTrangThai(trangThai, pageable);
    }

    // Find tasks by executor with pagination
    public Page<CongViecBaoTri> findByNguoiThucHien(Long idNguoiDung, Pageable pageable) {
        return repository.findByNguoiThucHien_IdNguoiDung(idNguoiDung, pageable);
    }

    // Find tasks by device with pagination
    public Page<CongViecBaoTri> findByThietBi(Long idThietBi, Pageable pageable) {
        return repository.findByThietBi_IdThietBi(idThietBi, pageable);
    }

    // Find tasks by maintenance plan
    public List<CongViecBaoTri> findByKeHoachBaoTri(Long idKeHoach) {
        return repository.findByKeHoachBaoTri_IdKeHoach(idKeHoach);
    }

    // Find priority tasks
    public List<CongViecBaoTri> findCongViecUuTien(Integer mucDo) {
        return repository.findCongViecUuTien(mucDo);
    }

    // Find overdue tasks
    public List<CongViecBaoTri> findCongViecQuaHan(LocalDateTime ngayHienTai) {
        return repository.findCongViecQuaHan(ngayHienTai);
    }

    // Count tasks by status
    public long countByTrangThai(String trangThai) {
        return repository.countByTrangThai(trangThai);
    }

    // Search tasks by keyword with pagination
    public Page<CongViecBaoTri> timKiemCongViec(String tuKhoa, Pageable pageable) {
        return repository.timKiemCongViec(tuKhoa, pageable);
    }

    // Statistics by status
    public List<Object[]> thongKeTheoTrangThai() {
        return repository.thongKeTheoTrangThai();
    }

    // Find tasks by maintenance request
    public List<CongViecBaoTri> findByYeuCauBaoTri(Long idYeuCau) {
        return repository.findByYeuCauBaoTri_IdYeuCau(idYeuCau);
    }

    // Find ongoing tasks for a user
    public List<CongViecBaoTri> findCongViecDangThucHienCuaNguoiDung(Long idNguoiDung) {
        return repository.findCongViecDangThucHienCuaNguoiDung(idNguoiDung);
    }

    // Find tasks within a date range
    public List<CongViecBaoTri> findByNgayBatDauBetween(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return repository.findByNgayBatDauBetween(tuNgay, denNgay);
    }

    // Delete a task
    public void deleteCongViecBaoTri(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Công việc bảo trì không tồn tại với ID: " + id);
        }
        repository.deleteById(id);
    }
}
