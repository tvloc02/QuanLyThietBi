package com.hethong.baotri.dich_vu.bao_tri;

import com.hethong.baotri.thuc_the.bao_tri.CanhBaoLoi;
import com.hethong.baotri.kho_du_lieu.bao_tri.CanhBaoLoiRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CanhBaoLoiService {

    private final CanhBaoLoiRepository repository;

    @Autowired
    public CanhBaoLoiService(CanhBaoLoiRepository repository) {
        this.repository = repository;
    }

    // Create or update an error alert
    public CanhBaoLoi saveCanhBaoLoi(CanhBaoLoi canhBao) {
        if (repository.existsByMaCanhBao(canhBao.getMaCanhBao()) && canhBao.getIdCanhBao() == null) {
            throw new RuntimeException("Mã cảnh báo " + canhBao.getMaCanhBao() + " đã tồn tại");
        }
        return repository.save(canhBao);
    }

    // Retrieve an alert by ID
    public Optional<CanhBaoLoi> getCanhBaoLoiById(Long id) {
        return repository.findById(id);
    }

    // Retrieve all alerts
    public List<CanhBaoLoi> getAllCanhBaoLoi() {
        return repository.findAll();
    }

    // Check if alert code exists
    public boolean existsByMaCanhBao(String maCanhBao) {
        return repository.existsByMaCanhBao(maCanhBao);
    }

    // Find alerts by severity
    public List<CanhBaoLoi> findByMucDoNghiemTrong(Integer mucDoNghiemTrong) {
        return repository.findByMucDoNghiemTrong(mucDoNghiemTrong);
    }

    // Find alerts by status with pagination
    public Page<CanhBaoLoi> findByTrangThai(String trangThai, Pageable pageable) {
        return repository.findByTrangThai(trangThai, pageable);
    }

    // Find alerts by device with pagination
    public Page<CanhBaoLoi> findByThietBi(Long idThietBi, Pageable pageable) {
        return repository.findByThietBi_IdThietBi(idThietBi, pageable);
    }

    // Find unprocessed alerts
    public List<CanhBaoLoi> findCanhBaoChuaXuLy() {
        return repository.findCanhBaoChuaXuLy();
    }

    // Find alerts within a date range
    public List<CanhBaoLoi> findByNgayPhatSinhBetween(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return repository.findByNgayPhatSinhBetween(tuNgay, denNgay);
    }

    // Count alerts by status
    public long countByTrangThai(String trangThai) {
        return repository.countByTrangThai(trangThai);
    }

    // Statistics by severity
    public List<Object[]> thongKeTheoMucDoNghiemTrong() {
        return repository.thongKeTheoMucDoNghiemTrong();
    }

    // Find alerts requiring immediate notification
    public List<CanhBaoLoi> findCanhBaoCanThongBaoNgay() {
        return repository.findCanhBaoCanThongBaoNgay();
    }

    // Find auto-generated alerts
    public List<CanhBaoLoi> findByTuDongTao(Boolean tuDongTao) {
        return repository.findByTuDongTao(tuDongTao);
    }

    // Find alerts by type
    public List<CanhBaoLoi> findByLoaiCanhBao(String loaiCanhBao) {
        return repository.findByLoaiCanhBao(loaiCanhBao);
    }

    // Find alerts by handler with pagination
    public Page<CanhBaoLoi> findByNguoiXuLy(Long idNguoiXuLy, Pageable pageable) {
        return repository.findByNguoiXuLy_IdNguoiDung(idNguoiXuLy, pageable);
    }

    // Find alerts without a handler
    public List<CanhBaoLoi> findCanhBaoChuaCoNguoiXuLy() {
        return repository.findCanhBaoChuaCoNguoiXuLy();
    }

    // Find critical alerts
    public List<CanhBaoLoi> findCanhBaoNghiemTrong(Integer mucDo) {
        return repository.findCanhBaoNghiemTrong(mucDo);
    }

    // Statistics by alert type
    public List<Object[]> thongKeTheoLoaiCanhBao() {
        return repository.thongKeTheoLoaiCanhBao();
    }

    // Statistics by device
    public List<Object[]> thongKeTheoThietBi() {
        return repository.thongKeTheoThietBi();
    }

    // Find recurring alerts for a device
    public List<CanhBaoLoi> findCanhBaoLapLaiChoThietBi(Long idThietBi, String loaiCanhBao, LocalDateTime tuNgay) {
        return repository.findCanhBaoLapLaiChoThietBi(idThietBi, loaiCanhBao, tuNgay);
    }

    // Delete an alert
    public void deleteCanhBaoLoi(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Cảnh báo lỗi không tồn tại với ID: " + id);
        }
        repository.deleteById(id);
    }
}
