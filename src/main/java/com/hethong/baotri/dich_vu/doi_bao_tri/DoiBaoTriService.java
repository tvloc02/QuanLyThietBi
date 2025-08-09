package com.hethong.baotri.dich_vu.doi_bao_tri;

import com.hethong.baotri.kho_du_lieu.doi_bao_tri.DoiBaoTriRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.ngoai_le.NgoaiLeDoiBaoTri;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DoiBaoTriService {

    private final DoiBaoTriRepository doiBaoTriRepository;
    private final NguoiDungRepository nguoiDungRepository;

    public DoiBaoTri taoDoiBaoTri(DoiBaoTri doiBaoTri) {
        log.info("Đang tạo đội bảo trì mới: {}", doiBaoTri.getMaDoi());

        if (doiBaoTriRepository.existsByMaDoi(doiBaoTri.getMaDoi())) {
            throw new NgoaiLeDoiBaoTri("Mã đội đã tồn tại: " + doiBaoTri.getMaDoi());
        }

        return doiBaoTriRepository.save(doiBaoTri);
    }

    public DoiBaoTri capNhatDoiBaoTri(Long idDoi, DoiBaoTri doiBaoTriCapNhat) {
        log.info("Đang cập nhật đội bảo trì ID: {}", idDoi);

        DoiBaoTri doiBaoTri = doiBaoTriRepository.findById(idDoi)
                .orElseThrow(() -> new NgoaiLeDoiBaoTri("Không tìm thấy đội với ID: " + idDoi));

        doiBaoTri.setTenDoi(doiBaoTriCapNhat.getTenDoi());
        doiBaoTri.setMoTa(doiBaoTriCapNhat.getMoTa());
        doiBaoTri.setChuyenMon(doiBaoTriCapNhat.getChuyenMon());
        doiBaoTri.setKhuVucHoatDong(doiBaoTriCapNhat.getKhuVucHoatDong());
        doiBaoTri.setCaLamViec(doiBaoTriCapNhat.getCaLamViec());
        doiBaoTri.setSoThanhVienToiDa(doiBaoTriCapNhat.getSoThanhVienToiDa());

        return doiBaoTriRepository.save(doiBaoTri);
    }

    public void themThanhVienVaoDoi(Long idDoi, Long idNguoiDung, String chucVu) {
        log.info("Đang thêm thành viên {} vào đội {}", idNguoiDung, idDoi);

        DoiBaoTri doiBaoTri = doiBaoTriRepository.findById(idDoi)
                .orElseThrow(() -> new NgoaiLeDoiBaoTri("Không tìm thấy đội với ID: " + idDoi));

        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new NgoaiLeDoiBaoTri("Không tìm thấy người dùng với ID: " + idNguoiDung));

        doiBaoTri.themThanhVien(nguoiDung, chucVu);
        doiBaoTriRepository.save(doiBaoTri);
    }

    public void xoaThanhVienKhoiDoi(Long idDoi, Long idNguoiDung) {
        log.info("Đang xóa thành viên {} khỏi đội {}", idNguoiDung, idDoi);

        DoiBaoTri doiBaoTri = doiBaoTriRepository.findById(idDoi)
                .orElseThrow(() -> new NgoaiLeDoiBaoTri("Không tìm thấy đội với ID: " + idDoi));

        NguoiDung nguoiDung = nguoiDungRepository.findById(idNguoiDung)
                .orElseThrow(() -> new NgoaiLeDoiBaoTri("Không tìm thấy người dùng với ID: " + idNguoiDung));

        doiBaoTri.xoaThanhVien(nguoiDung);
        doiBaoTriRepository.save(doiBaoTri);
    }

    @Transactional(readOnly = true)
    public Page<DoiBaoTri> layDanhSachDoiBaoTri(Pageable pageable) {
        return doiBaoTriRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<DoiBaoTri> layDoiBaoTriTheoTrangThai(Boolean trangThaiHoatDong, Pageable pageable) {
        return doiBaoTriRepository.findByTrangThaiHoatDong(trangThaiHoatDong, pageable);
    }

    @Transactional(readOnly = true)
    public List<DoiBaoTri> layDoiBaoTriTheoChuyenMon(String chuyenMon) {
        return doiBaoTriRepository.findByChuyenMon(chuyenMon);
    }

    @Transactional(readOnly = true)
    public List<DoiBaoTri> layDoiBaoTriCoTheNhanCongViec() {
        return doiBaoTriRepository.findAll().stream()
                .filter(DoiBaoTri::coTheNhanCongViec)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DoiBaoTri> timDoiBaoTriTheoId(Long idDoi) {
        return doiBaoTriRepository.findById(idDoi);
    }

    @Transactional(readOnly = true)
    public Optional<DoiBaoTri> timDoiBaoTriTheoMa(String maDoi) {
        return doiBaoTriRepository.findByMaDoi(maDoi);
    }

    @Transactional(readOnly = true)
    public long demTongSoDoiBaoTri() {
        return doiBaoTriRepository.count();
    }

    @Transactional(readOnly = true)
    public long demDoiBaoTriHoatDong() {
        return doiBaoTriRepository.countByTrangThaiHoatDong(true);
    }
}