package com.hethong.baotri.dich_vu.doi_bao_tri;

import com.hethong.baotri.dich_vu.nguoi_dung.NguoiDungService;
import com.hethong.baotri.kho_du_lieu.bao_tri.YeuCauBaoTriRepository;
import com.hethong.baotri.kho_du_lieu.doi_bao_tri.DoiBaoTriRepository;
import com.hethong.baotri.kho_du_lieu.doi_bao_tri.PhanCongCongViecRepository;
import com.hethong.baotri.kho_du_lieu.doi_bao_tri.ThanhVienDoiRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri;
import com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri;
import com.hethong.baotri.thuc_the.doi_bao_tri.PhanCongCongViec;
import com.hethong.baotri.thuc_the.doi_bao_tri.ThanhVienDoi;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.nguoi_dung.Quyen;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PhanCongCongViecService {

    private final PhanCongCongViecRepository phanCongCongViecRepository;
    private final DoiBaoTriRepository doiBaoTriRepository;
    private final ThanhVienDoiRepository thanhVienDoiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final YeuCauBaoTriRepository yeuCauBaoTriRepository;
    private final NguoiDungService nguoiDungService;

    public PhanCongCongViec phanCongCongViec(Long idYeuCau, Long idNguoiDuocPhanCong, Long idDoiBaoTri,
                                             Long idNguoiPhanCong, Integer mucDoUuTien, String ghiChu,
                                             LocalDateTime ngayBatDauMongMuon, LocalDateTime ngayHoanThanhMongMuon,
                                             Integer thoiGianDuKien, String yeuCauDacBiet) {
        // Validate maintenance request
        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new RuntimeException("Yêu cầu bảo trì không tồn tại với ID: " + idYeuCau));

        // Validate assigned user
        NguoiDung nguoiDuocPhanCong = nguoiDungRepository.findById(idNguoiDuocPhanCong)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại với ID: " + idNguoiDuocPhanCong));
        if (!nguoiDuocPhanCong.isEnabled() || !nguoiDuocPhanCong.isAccountNonLocked()) {
            throw new RuntimeException("Người dùng không hoạt động hoặc bị khóa");
        }
        if (!nguoiDuocPhanCong.coVaiTro("NHAN_VIEN_BAO_TRI") && !nguoiDuocPhanCong.coVaiTro("TRUONG_DOI_BAO_TRI")) {
            throw new RuntimeException("Người dùng không có quyền thực hiện bảo trì");
        }

        // Validate assigning user
        NguoiDung nguoiPhanCong = nguoiDungRepository.findById(idNguoiPhanCong)
                .orElseThrow(() -> new RuntimeException("Người phân công không tồn tại với ID: " + idNguoiPhanCong));
        if (!nguoiPhanCong.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(Quyen.PHAN_CONG_CONG_VIEC))) {
            throw new RuntimeException("Người dùng không có quyền phân công công việc");
        }

        // Validate team (if provided)
        DoiBaoTri doiBaoTri = null;
        if (idDoiBaoTri != null) {
            doiBaoTri = doiBaoTriRepository.findById(idDoiBaoTri)
                    .orElseThrow(() -> new RuntimeException("Đội bảo trì không tồn tại với ID: " + idDoiBaoTri));
            if (!doiBaoTri.coTheNhanCongViec()) {
                throw new RuntimeException("Đội bảo trì không thể nhận thêm công việc");
            }
            if (!doiBaoTri.laThanhVien(nguoiDuocPhanCong)) {
                throw new RuntimeException("Người dùng không thuộc đội bảo trì được chỉ định");
            }
            if (!doiBaoTri.getChuyenMon().equals(yeuCau.getLoaiYeuCau()) &&
                    !doiBaoTri.getChuyenMon().equals(DoiBaoTri.CHUYEN_MON_TONG_HOP)) {
                throw new RuntimeException("Đội bảo trì không có chuyên môn phù hợp");
            }
        }

        // Check for duplicate assignment
        if (phanCongCongViecRepository.existsByYeuCauBaoTri_IdYeuCauAndNguoiDuocPhanCong_IdNguoiDung(idYeuCau, idNguoiDuocPhanCong)) {
            throw new RuntimeException("Công việc đã được phân công cho người dùng này");
        }

        // Create and save assignment
        PhanCongCongViec phanCong = new PhanCongCongViec();
        phanCong.setYeuCauBaoTri(yeuCau);
        phanCong.setNguoiDuocPhanCong(nguoiDuocPhanCong);
        phanCong.setNguoiPhanCong(nguoiPhanCong);
        phanCong.setDoiBaoTri(doiBaoTri);
        phanCong.setMucDoUuTien(mucDoUuTien != null ? mucDoUuTien : 1);
        phanCong.setGhiChu(ghiChu);
        phanCong.setYeuCauDacBiet(yeuCauDacBiet);
        phanCong.setNgayBatDauMongMuon(ngayBatDauMongMuon);
        phanCong.setNgayHoanThanhMongMuon(ngayHoanThanhMongMuon);
        phanCong.setThoiGianDuKien(thoiGianDuKien);

        if (doiBaoTri != null) {
            doiBaoTri.tangSoCongViecDangThucHien();
            doiBaoTriRepository.save(doiBaoTri);
        }

        Optional<ThanhVienDoi> thanhVienOpt = thanhVienDoiRepository.findByNguoiDung_IdNguoiDung(idNguoiDuocPhanCong)
                .stream().filter(tv -> tv.getDoiBaoTri().getIdDoiBaoTri().equals(idDoiBaoTri) && tv.getTrangThaiHoatDong())
                .findFirst();
        thanhVienOpt.ifPresent(thanhVien -> {
            thanhVien.tangSoGioLamViec(thoiGianDuKien != null ? thoiGianDuKien : 0);
            thanhVienDoiRepository.save(thanhVien);
        });

        log.info("Phân công công việc cho yêu cầu ID {} cho người dùng ID {} (đội ID: {})",
                idYeuCau, idNguoiDuocPhanCong, idDoiBaoTri);
        return phanCongCongViecRepository.save(phanCong);
    }

    public PhanCongCongViec batDauCongViec(Long idPhanCong) {
        PhanCongCongViec phanCong = phanCongCongViecRepository.findById(idPhanCong)
                .orElseThrow(() -> new RuntimeException("Phân công không tồn tại với ID: " + idPhanCong));
        if (!phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_CHUA_BAT_DAU)) {
            throw new RuntimeException("Công việc không thể bắt đầu vì không ở trạng thái chờ");
        }
        phanCong.batDauThucHien();
        log.info("Bắt đầu công việc phân công ID {}", idPhanCong);
        return phanCongCongViecRepository.save(phanCong);
    }

    public PhanCongCongViec hoanThanhCongViec(Long idPhanCong, String ghiChu) {
        PhanCongCongViec phanCong = phanCongCongViecRepository.findById(idPhanCong)
                .orElseThrow(() -> new RuntimeException("Phân công không tồn tại với ID: " + idPhanCong));
        if (!phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_DANG_THUC_HIEN)) {
            throw new RuntimeException("Công việc không thể hoàn thành vì không ở trạng thái đang thực hiện");
        }
        phanCong.hoanThanh();
        phanCong.setGhiChu(ghiChu);

        if (phanCong.getDoiBaoTri() != null) {
            phanCong.getDoiBaoTri().tangSoCongViecHoanThanh();
            doiBaoTriRepository.save(phanCong.getDoiBaoTri());
        }

        Optional<ThanhVienDoi> thanhVienOpt = thanhVienDoiRepository.findByNguoiDung_IdNguoiDung(phanCong.getNguoiDuocPhanCong().getIdNguoiDung())
                .stream().filter(tv -> tv.getDoiBaoTri().equals(phanCong.getDoiBaoTri()) && tv.getTrangThaiHoatDong())
                .findFirst();
        thanhVienOpt.ifPresent(ThanhVienDoi::tangSoCongViecHoanThanh);

        log.info("Hoàn thành công việc phân công ID {}", idPhanCong);
        return phanCongCongViecRepository.save(phanCong);
    }

    public PhanCongCongViec huyCongViec(Long idPhanCong, String lyDo) {
        PhanCongCongViec phanCong = phanCongCongViecRepository.findById(idPhanCong)
                .orElseThrow(() -> new RuntimeException("Phân công không tồn tại với ID: " + idPhanCong));
        if (phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_HOAN_THANH)) {
            throw new RuntimeException("Công việc đã hoàn thành, không thể hủy");
        }
        phanCong.huy(lyDo);

        if (phanCong.getDoiBaoTri() != null && phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_DANG_THUC_HIEN)) {
            phanCong.getDoiBaoTri().setSoCongViecDangThucHien(phanCong.getDoiBaoTri().getSoCongViecDangThucHien() - 1);
            phanCong.getDoiBaoTri().capNhatMucDoBanRon();
            doiBaoTriRepository.save(phanCong.getDoiBaoTri());
        }

        log.info("Hủy công việc phân công ID {} với lý do: {}", idPhanCong, lyDo);
        return phanCongCongViecRepository.save(phanCong);
    }

    public PhanCongCongViec tamDungCongViec(Long idPhanCong, String lyDo) {
        PhanCongCongViec phanCong = phanCongCongViecRepository.findById(idPhanCong)
                .orElseThrow(() -> new RuntimeException("Phân công không tồn tại với ID: " + idPhanCong));
        if (!phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_DANG_THUC_HIEN)) {
            throw new RuntimeException("Công việc không thể tạm dừng vì không ở trạng thái đang thực hiện");
        }
        phanCong.tamDung(lyDo);
        log.info("Tạm dừng công việc phân công ID {} với lý do: {}", idPhanCong, lyDo);
        return phanCongCongViecRepository.save(phanCong);
    }

    public PhanCongCongViec tiepTucCongViec(Long idPhanCong) {
        PhanCongCongViec phanCong = phanCongCongViecRepository.findById(idPhanCong)
                .orElseThrow(() -> new RuntimeException("Phân công không tồn tại với ID: " + idPhanCong));
        if (!phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_TAM_DUNG)) {
            throw new RuntimeException("Công việc không thể tiếp tục vì không ở trạng thái tạm dừng");
        }
        phanCong.tiepTuc();
        log.info("Tiếp tục công việc phân công ID {}", idPhanCong);
        return phanCongCongViecRepository.save(phanCong);
    }

    public List<PhanCongCongViec> getByYeuCauBaoTri(Long idYeuCau) {
        return phanCongCongViecRepository.findByYeuCauBaoTri_IdYeuCau(idYeuCau);
    }

    public List<PhanCongCongViec> getByNguoiDuocPhanCong(Long idNguoiDung) {
        return phanCongCongViecRepository.findByNguoiDuocPhanCong_IdNguoiDung(idNguoiDung);
    }

    public List<PhanCongCongViec> getByDoiBaoTri(Long idDoiBaoTri) {
        return phanCongCongViecRepository.findByDoiBaoTri_IdDoiBaoTri(idDoiBaoTri);
    }

    public Page<PhanCongCongViec> getByTrangThai(String trangThai, Pageable pageable) {
        return phanCongCongViecRepository.findByTrangThai(trangThai, pageable);
    }

    public List<PhanCongCongViec> getQuaHan() {
        return phanCongCongViecRepository.findQuaHan();
    }

    public List<Object[]> thongKeTheoTrangThai() {
        return phanCongCongViecRepository.thongKeTheoTrangThai();
    }

    public void xoaPhanCong(Long idPhanCong) {
        PhanCongCongViec phanCong = phanCongCongViecRepository.findById(idPhanCong)
                .orElseThrow(() -> new RuntimeException("Phân công không tồn tại với ID: " + idPhanCong));
        if (phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_HOAN_THANH)) {
            throw new RuntimeException("Không thể xóa phân công đã hoàn thành");
        }
        if (phanCong.getDoiBaoTri() != null && phanCong.getTrangThai().equals(PhanCongCongViec.TRANG_THAI_DANG_THUC_HIEN)) {
            phanCong.getDoiBaoTri().setSoCongViecDangThucHien(phanCong.getDoiBaoTri().getSoCongViecDangThucHien() - 1);
            phanCong.getDoiBaoTri().capNhatMucDoBanRon();
            doiBaoTriRepository.save(phanCong.getDoiBaoTri());
        }
        phanCongCongViecRepository.deleteById(idPhanCong);
        log.info("Xóa phân công công việc ID {}", idPhanCong);
    }
}
