package com.hethong.baotri.dich_vu.thiet_bi;

import com.hethong.baotri.kho_du_lieu.thiet_bi.ThietBiRepository;
import com.hethong.baotri.kho_du_lieu.thiet_bi.NhomThietBiRepository;
import com.hethong.baotri.kho_du_lieu.thiet_bi.TrangThaiThietBiRepository;
import com.hethong.baotri.thuc_the.thiet_bi.ThietBi;
import com.hethong.baotri.thuc_the.thiet_bi.NhomThietBi;
import com.hethong.baotri.thuc_the.thiet_bi.TrangThaiThietBi;
import com.hethong.baotri.ngoai_le.NgoaiLeThietBi;
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
public class ThietBiService {

    private final ThietBiRepository thietBiRepository;
    private final NhomThietBiRepository nhomThietBiRepository;
    private final TrangThaiThietBiRepository trangThaiThietBiRepository;

    public ThietBi taoThietBi(ThietBi thietBi) {
        log.info("Đang tạo thiết bị mới: {}", thietBi.getMaThietBi());

        if (thietBiRepository.existsByMaThietBi(thietBi.getMaThietBi())) {
            throw new NgoaiLeThietBi("Mã thiết bị đã tồn tại: " + thietBi.getMaThietBi());
        }

        return thietBiRepository.save(thietBi);
    }

    public ThietBi capNhatThietBi(Long idThietBi, ThietBi thietBiCapNhat) {
        log.info("Đang cập nhật thiết bị ID: {}", idThietBi);

        ThietBi thietBi = thietBiRepository.findById(idThietBi)
                .orElseThrow(() -> new NgoaiLeThietBi("Không tìm thấy thiết bị với ID: " + idThietBi));

        if (!thietBi.getMaThietBi().equals(thietBiCapNhat.getMaThietBi()) &&
                thietBiRepository.existsByMaThietBi(thietBiCapNhat.getMaThietBi())) {
            throw new NgoaiLeThietBi("Mã thiết bị đã tồn tại: " + thietBiCapNhat.getMaThietBi());
        }

        thietBi.setMaThietBi(thietBiCapNhat.getMaThietBi());
        thietBi.setTenThietBi(thietBiCapNhat.getTenThietBi());
        thietBi.setMoTa(thietBiCapNhat.getMoTa());
        thietBi.setViTriLapDat(thietBiCapNhat.getViTriLapDat());
        thietBi.setNhomThietBi(thietBiCapNhat.getNhomThietBi());
        thietBi.setTrangThaiThietBi(thietBiCapNhat.getTrangThaiThietBi());

        return thietBiRepository.save(thietBi);
    }

    public void xoaThietBi(Long idThietBi) {
        log.info("Đang xóa thiết bị ID: {}", idThietBi);

        ThietBi thietBi = thietBiRepository.findById(idThietBi)
                .orElseThrow(() -> new NgoaiLeThietBi("Không tìm thấy thiết bị với ID: " + idThietBi));

        thietBiRepository.delete(thietBi);
    }

    @Transactional(readOnly = true)
    public Optional<ThietBi> timThietBiTheoId(Long idThietBi) {
        return thietBiRepository.findById(idThietBi);
    }

    @Transactional(readOnly = true)
    public Optional<ThietBi> timThietBiTheoMa(String maThietBi) {
        return thietBiRepository.findByMaThietBi(maThietBi);
    }

    @Transactional(readOnly = true)
    public Page<ThietBi> layDanhSachThietBi(Pageable pageable) {
        return thietBiRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<ThietBi> timKiemThietBi(String tuKhoa, Pageable pageable) {
        return thietBiRepository.timKiemThietBi(tuKhoa, pageable);
    }

    @Transactional(readOnly = true)
    public List<ThietBi> layThietBiCanBaoTri() {
        return thietBiRepository.findThietBiCanBaoTri(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<ThietBi> layThietBiCanBaoTriTrongKhoang(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return thietBiRepository.findThietBiCanBaoTriTrongKhoang(tuNgay, denNgay);
    }

    public void capNhatTrangThaiThietBi(Long idThietBi, Long idTrangThaiMoi) {
        log.info("Đang cập nhật trạng thái thiết bị {} thành {}", idThietBi, idTrangThaiMoi);

        ThietBi thietBi = thietBiRepository.findById(idThietBi)
                .orElseThrow(() -> new NgoaiLeThietBi("Không tìm thấy thiết bị với ID: " + idThietBi));

        TrangThaiThietBi trangThaiMoi = trangThaiThietBiRepository.findById(idTrangThaiMoi)
                .orElseThrow(() -> new NgoaiLeThietBi("Không tìm thấy trạng thái với ID: " + idTrangThaiMoi));

        thietBi.setTrangThaiThietBi(trangThaiMoi);
        thietBiRepository.save(thietBi);
    }

    public void capNhatSauBaoTri(Long idThietBi, int thoiGianBaoTri) {
        log.info("Đang cập nhật thông tin sau bảo trì cho thiết bị ID: {}", idThietBi);

        ThietBi thietBi = thietBiRepository.findById(idThietBi)
                .orElseThrow(() -> new NgoaiLeThietBi("Không tìm thấy thiết bị với ID: " + idThietBi));

        thietBi.capNhatSauBaoTri(thoiGianBaoTri);
        thietBiRepository.save(thietBi);
    }

    @Transactional(readOnly = true)
    public long demThietBiHoatDong() {
        return thietBiRepository.demThietBiHoatDong();
    }

    @Transactional(readOnly = true)
    public Double tinhTrungBinhGioHoatDong() {
        return thietBiRepository.tinhTrungBinhGioHoatDong();
    }
}