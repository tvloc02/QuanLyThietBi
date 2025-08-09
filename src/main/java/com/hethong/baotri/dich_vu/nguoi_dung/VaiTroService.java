package com.hethong.baotri.dich_vu.nguoi_dung;

import com.hethong.baotri.dto.nguoi_dung.VaiTroDTO;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.VaiTroRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.QuyenRepository;
import com.hethong.baotri.thuc_the.nguoi_dung.VaiTro;
import com.hethong.baotri.thuc_the.nguoi_dung.Quyen;
import com.hethong.baotri.ngoai_le.NgoaiLeNguoiDung;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VaiTroService {

    private final VaiTroRepository vaiTroRepository;
    private final QuyenRepository quyenRepository;
    private final ModelMapper modelMapper;

    public VaiTroDTO taoVaiTro(VaiTroDTO vaiTroDTO) {
        log.info("Đang tạo vai trò mới: {}", vaiTroDTO.getTenVaiTro());

        if (vaiTroRepository.existsByTenVaiTro(vaiTroDTO.getTenVaiTro())) {
            throw new NgoaiLeNguoiDung("Tên vai trò đã tồn tại: " + vaiTroDTO.getTenVaiTro());
        }

        VaiTro vaiTro = modelMapper.map(vaiTroDTO, VaiTro.class);
        vaiTro = vaiTroRepository.save(vaiTro);

        return modelMapper.map(vaiTro, VaiTroDTO.class);
    }

    public VaiTroDTO capNhatVaiTro(Long id, VaiTroDTO vaiTroDTO) {
        log.info("Đang cập nhật vai trò ID: {}", id);

        VaiTro vaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy vai trò với ID: " + id));

        if (!vaiTro.getTenVaiTro().equals(vaiTroDTO.getTenVaiTro()) &&
                vaiTroRepository.existsByTenVaiTro(vaiTroDTO.getTenVaiTro())) {
            throw new NgoaiLeNguoiDung("Tên vai trò đã tồn tại: " + vaiTroDTO.getTenVaiTro());
        }

        vaiTro.setTenVaiTro(vaiTroDTO.getTenVaiTro());
        vaiTro.setMoTa(vaiTroDTO.getMoTa());
        vaiTro.setTrangThaiHoatDong(vaiTroDTO.getTrangThaiHoatDong());

        vaiTro = vaiTroRepository.save(vaiTro);
        return modelMapper.map(vaiTro, VaiTroDTO.class);
    }

    public void xoaVaiTro(Long id) {
        log.info("Đang xóa vai trò ID: {}", id);

        VaiTro vaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy vai trò với ID: " + id));

        if (vaiTro.dangDuocSuDung()) {
            throw new NgoaiLeNguoiDung("Không thể xóa vai trò đang được sử dụng");
        }

        vaiTroRepository.delete(vaiTro);
    }

    @Transactional(readOnly = true)
    public VaiTroDTO timVaiTroTheoId(Long id) {
        VaiTro vaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy vai trò với ID: " + id));

        return modelMapper.map(vaiTro, VaiTroDTO.class);
    }

    @Transactional(readOnly = true)
    public Optional<VaiTroDTO> timVaiTroTheoTen(String tenVaiTro) {
        return vaiTroRepository.findByTenVaiTro(tenVaiTro)
                .map(vaiTro -> modelMapper.map(vaiTro, VaiTroDTO.class));
    }

    @Transactional(readOnly = true)
    public Page<VaiTroDTO> layDanhSachVaiTro(Pageable pageable) {
        return vaiTroRepository.findAll(pageable)
                .map(vaiTro -> modelMapper.map(vaiTro, VaiTroDTO.class));
    }

    @Transactional(readOnly = true)
    public Page<VaiTroDTO> timKiemVaiTro(String tuKhoa, Pageable pageable) {
        return vaiTroRepository.timKiemVaiTro(tuKhoa, pageable)
                .map(vaiTro -> modelMapper.map(vaiTro, VaiTroDTO.class));
    }

    public void themQuyenVaoVaiTro(Long idVaiTro, Long idQuyen) {
        VaiTro vaiTro = vaiTroRepository.findById(idVaiTro)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy vai trò với ID: " + idVaiTro));

        Quyen quyen = quyenRepository.findById(idQuyen)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy quyền với ID: " + idQuyen));

        vaiTro.themQuyen(quyen);
        vaiTroRepository.save(vaiTro);
    }

    public void xoaQuyenKhoiVaiTro(Long idVaiTro, Long idQuyen) {
        VaiTro vaiTro = vaiTroRepository.findById(idVaiTro)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy vai trò với ID: " + idVaiTro));

        Quyen quyen = quyenRepository.findById(idQuyen)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy quyền với ID: " + idQuyen));

        vaiTro.xoaQuyen(quyen);
        vaiTroRepository.save(vaiTro);
    }

    @Transactional(readOnly = true)
    public List<VaiTroDTO> layVaiTroTheoQuyen(String tenQuyen) {
        return vaiTroRepository.findByQuyen(tenQuyen)
                .stream()
                .map(vaiTro -> modelMapper.map(vaiTro, VaiTroDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long demVaiTroHoatDong() {
        return vaiTroRepository.countByTrangThaiHoatDong(true);
    }

    public void capNhatTrangThaiHoatDong(Long id, Boolean trangThaiHoatDong) {
        VaiTro vaiTro = vaiTroRepository.findById(id)
                .orElseThrow(() -> new NgoaiLeNguoiDung("Không tìm thấy vai trò với ID: " + id));

        vaiTro.setTrangThaiHoatDong(trangThaiHoatDong);
        vaiTroRepository.save(vaiTro);
    }
}