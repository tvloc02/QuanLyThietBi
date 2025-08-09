package com.hethong.baotri.dich_vu.lop_hoc;

import com.hethong.baotri.kho_du_lieu.lop_hoc.LopHocRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.kho_du_lieu.thiet_bi.ThietBiRepository;
import com.hethong.baotri.thuc_the.lop_hoc.LopHoc;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.thiet_bi.ThietBi;
import com.hethong.baotri.dto.lop_hoc.LopHocDTO;
import com.hethong.baotri.ngoai_le.NgoaiLeLopHoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LopHocService {

    private final LopHocRepository lopHocRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final ThietBiRepository thietBiRepository;
    private final ModelMapper modelMapper;

    /**
     * Tạo lớp học mới
     */
    public LopHocDTO taoLopHoc(LopHocDTO lopHocDTO) {
        log.info("Đang tạo lớp học mới: {}", lopHocDTO.getMaLop());

        // Kiểm tra mã lớp đã tồn tại
        if (lopHocRepository.existsByMaLop(lopHocDTO.getMaLop())) {
            throw new NgoaiLeLopHoc("Mã lớp đã tồn tại: " + lopHocDTO.getMaLop());
        }

        LopHoc lopHoc = modelMapper.map(lopHocDTO, LopHoc.class);
        lopHoc.setNgayTao(LocalDateTime.now());
        lopHoc.setTrangThaiHoatDong(true);

        // Gán giáo viên chủ nhiệm nếu có
        if (lopHocDTO.getIdGiaoVienChuNhiem() != null) {
            NguoiDung giaoVien = nguoiDungRepository.findById(lopHocDTO.getIdGiaoVienChuNhiem())
                    .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy giáo viên với ID: " + lopHocDTO.getIdGiaoVienChuNhiem()));

            if (!giaoVien.coVaiTro("GIAO_VIEN")) {
                throw new NgoaiLeLopHoc("Người dùng không có vai trò giáo viên");
            }

            lopHoc.setGiaoVienChuNhiem(giaoVien);
        }

        LopHoc savedLopHoc = lopHocRepository.save(lopHoc);
        log.info("Tạo lớp học thành công: {}", savedLopHoc.getMaLop());

        return modelMapper.map(savedLopHoc, LopHocDTO.class);
    }

    /**
     * Cập nhật thông tin lớp học
     */
    public LopHocDTO capNhatLopHoc(Long idLopHoc, LopHocDTO lopHocDTO) {
        log.info("Đang cập nhật lớp học ID: {}", idLopHoc);

        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        // Kiểm tra mã lớp nếu thay đổi
        if (!lopHoc.getMaLop().equals(lopHocDTO.getMaLop()) &&
                lopHocRepository.existsByMaLop(lopHocDTO.getMaLop())) {
            throw new NgoaiLeLopHoc("Mã lớp đã tồn tại: " + lopHocDTO.getMaLop());
        }

        // Cập nhật thông tin
        lopHoc.setMaLop(lopHocDTO.getMaLop());
        lopHoc.setTenLop(lopHocDTO.getTenLop());
        lopHoc.setKhoi(lopHocDTO.getKhoi());
        lopHoc.setPhongHoc(lopHocDTO.getPhongHoc());
        lopHoc.setSiSo(lopHocDTO.getSiSo());
        lopHoc.setNamHoc(lopHocDTO.getNamHoc());

        // Cập nhật giáo viên chủ nhiệm
        if (lopHocDTO.getIdGiaoVienChuNhiem() != null) {
            NguoiDung giaoVien = nguoiDungRepository.findById(lopHocDTO.getIdGiaoVienChuNhiem())
                    .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy giáo viên với ID: " + lopHocDTO.getIdGiaoVienChuNhiem()));
            lopHoc.setGiaoVienChuNhiem(giaoVien);
        } else {
            lopHoc.setGiaoVienChuNhiem(null);
        }

        LopHoc updatedLopHoc = lopHocRepository.save(lopHoc);
        return modelMapper.map(updatedLopHoc, LopHocDTO.class);
    }

    /**
     * Xóa lớp học
     */
    public void xoaLopHoc(Long idLopHoc) {
        log.info("Đang xóa lớp học ID: {}", idLopHoc);

        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        // Kiểm tra có thiết bị liên quan không
        List<ThietBi> thietBiTrongLop = thietBiRepository.findByLopHoc_IdLopHoc(idLopHoc);
        if (!thietBiTrongLop.isEmpty()) {
            throw new NgoaiLeLopHoc("Không thể xóa lớp học đang có thiết bị");
        }

        lopHocRepository.delete(lopHoc);
        log.info("Xóa lớp học thành công ID: {}", idLopHoc);
    }

    /**
     * Tìm lớp học theo ID
     */
    @Transactional(readOnly = true)
    public LopHocDTO timLopHocTheoId(Long idLopHoc) {
        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        LopHocDTO dto = modelMapper.map(lopHoc, LopHocDTO.class);

        // Thêm thông tin giáo viên chủ nhiệm
        if (lopHoc.getGiaoVienChuNhiem() != null) {
            dto.setTenGiaoVienChuNhiem(lopHoc.getGiaoVienChuNhiem().getHoVaTen());
        }

        return dto;
    }

    /**
     * Lấy danh sách lớp học
     */
    @Transactional(readOnly = true)
    public Page<LopHocDTO> layDanhSachLopHoc(Pageable pageable) {
        Page<LopHoc> lopHocPage = lopHocRepository.findAll(pageable);

        return lopHocPage.map(lopHoc -> {
            LopHocDTO dto = modelMapper.map(lopHoc, LopHocDTO.class);
            if (lopHoc.getGiaoVienChuNhiem() != null) {
                dto.setTenGiaoVienChuNhiem(lopHoc.getGiaoVienChuNhiem().getHoVaTen());
            }
            return dto;
        });
    }

    /**
     * Tìm kiếm lớp học
     */
    @Transactional(readOnly = true)
    public Page<LopHocDTO> timKiemLopHoc(String tuKhoa, Pageable pageable) {
        Page<LopHoc> ketQua = lopHocRepository.timKiemLopHoc(tuKhoa, pageable);

        return ketQua.map(lopHoc -> {
            LopHocDTO dto = modelMapper.map(lopHoc, LopHocDTO.class);
            if (lopHoc.getGiaoVienChuNhiem() != null) {
                dto.setTenGiaoVienChuNhiem(lopHoc.getGiaoVienChuNhiem().getHoVaTen());
            }
            return dto;
        });
    }

    /**
     * Lấy lớp học theo giáo viên chủ nhiệm
     */
    @Transactional(readOnly = true)
    public List<LopHocDTO> layLopHocTheoGiaoVien(Long idGiaoVien) {
        List<LopHoc> lopHocList = lopHocRepository.findByGiaoVienChuNhiem_IdNguoiDung(idGiaoVien);

        return lopHocList.stream()
                .map(lopHoc -> {
                    LopHocDTO dto = modelMapper.map(lopHoc, LopHocDTO.class);
                    dto.setTenGiaoVienChuNhiem(lopHoc.getGiaoVienChuNhiem().getHoVaTen());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Lấy lớp học theo khối
     */
    @Transactional(readOnly = true)
    public List<LopHocDTO> layLopHocTheoKhoi(String khoi) {
        List<LopHoc> lopHocList = lopHocRepository.findByKhoi(khoi);

        return lopHocList.stream()
                .map(lopHoc -> modelMapper.map(lopHoc, LopHocDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Gán thiết bị vào lớp học
     */
    public void ganThietBiVaoLop(Long idLopHoc, Long idThietBi) {
        log.info("Đang gán thiết bị {} vào lớp {}", idThietBi, idLopHoc);

        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        ThietBi thietBi = thietBiRepository.findById(idThietBi)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy thiết bị với ID: " + idThietBi));

        // Kiểm tra thiết bị đã được gán chưa
        if (thietBi.getLopHoc() != null) {
            throw new NgoaiLeLopHoc("Thiết bị đã được gán cho lớp khác");
        }

        thietBi.setLopHoc(lopHoc);
        thietBiRepository.save(thietBi);

        log.info("Gán thiết bị thành công");
    }

    /**
     * Chuyển thiết bị giữa các lớp
     */
    public void chuyenThietBi(Long idThietBi, Long idLopHocMoi) {
        log.info("Đang chuyển thiết bị {} sang lớp {}", idThietBi, idLopHocMoi);

        ThietBi thietBi = thietBiRepository.findById(idThietBi)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy thiết bị với ID: " + idThietBi));

        LopHoc lopHocMoi = lopHocRepository.findById(idLopHocMoi)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHocMoi));

        thietBi.setLopHoc(lopHocMoi);
        thietBiRepository.save(thietBi);

        log.info("Chuyển thiết bị thành công");
    }

    /**
     * Lấy thiết bị trong lớp
     */
    @Transactional(readOnly = true)
    public List<ThietBi> layThietBiTrongLop(Long idLopHoc) {
        return thietBiRepository.findByLopHoc_IdLopHoc(idLopHoc);
    }

    /**
     * Đếm tổng số lớp học
     */
    @Transactional(readOnly = true)
    public long demTongSoLopHoc() {
        return lopHocRepository.count();
    }

    /**
     * Đếm lớp học theo khối
     */
    @Transactional(readOnly = true)
    public long demLopHocTheoKhoi(String khoi) {
        return lopHocRepository.countByKhoi(khoi);
    }

    /**
     * Thống kê lớp học theo năm học
     */
    @Transactional(readOnly = true)
    public List<Object[]> thongKeLopHocTheoNamHoc() {
        return lopHocRepository.thongKeLopHocTheoNamHoc();
    }

    /**
     * Lấy danh sách giáo viên chưa làm chủ nhiệm
     */
    @Transactional(readOnly = true)
    public List<NguoiDung> layGiaoVienChuaLamChuNhiem() {
        return nguoiDungRepository.layGiaoVienChuaLamChuNhiem();
    }

    /**
     * Phân công giáo viên chủ nhiệm
     */
    public void phanCongGiaoVienChuNhiem(Long idLopHoc, Long idGiaoVien) {
        log.info("Đang phân công giáo viên {} làm chủ nhiệm lớp {}", idGiaoVien, idLopHoc);

        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        NguoiDung giaoVien = nguoiDungRepository.findById(idGiaoVien)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy giáo viên với ID: " + idGiaoVien));

        if (!giaoVien.coVaiTro("GIAO_VIEN")) {
            throw new NgoaiLeLopHoc("Người dùng không có vai trò giáo viên");
        }

        // Kiểm tra giáo viên đã làm chủ nhiệm lớp khác chưa
        Optional<LopHoc> lopHocCu = lopHocRepository.findByGiaoVienChuNhiem_IdNguoiDung(idGiaoVien)
                .stream().findFirst();

        if (lopHocCu.isPresent() && !lopHocCu.get().getIdLopHoc().equals(idLopHoc)) {
            throw new NgoaiLeLopHoc("Giáo viên đã làm chủ nhiệm lớp khác");
        }

        lopHoc.setGiaoVienChuNhiem(giaoVien);
        lopHocRepository.save(lopHoc);

        log.info("Phân công giáo viên chủ nhiệm thành công");
    }

    /**
     * Hủy phân công giáo viên chủ nhiệm
     */
    public void huyPhanCongGiaoVienChuNhiem(Long idLopHoc) {
        log.info("Đang hủy phân công giáo viên chủ nhiệm lớp {}", idLopHoc);

        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        lopHoc.setGiaoVienChuNhiem(null);
        lopHocRepository.save(lopHoc);

        log.info("Hủy phân công giáo viên chủ nhiệm thành công");
    }

    /**
     * Cập nhật trạng thái hoạt động lớp học
     */
    public void capNhatTrangThaiHoatDong(Long idLopHoc, Boolean trangThaiHoatDong) {
        log.info("Đang cập nhật trạng thái hoạt động lớp {} thành {}", idLopHoc, trangThaiHoatDong);

        LopHoc lopHoc = lopHocRepository.findById(idLopHoc)
                .orElseThrow(() -> new NgoaiLeLopHoc("Không tìm thấy lớp học với ID: " + idLopHoc));

        lopHoc.setTrangThaiHoatDong(trangThaiHoatDong);
        lopHocRepository.save(lopHoc);

        log.info("Cập nhật trạng thái hoạt động thành công");
    }
}