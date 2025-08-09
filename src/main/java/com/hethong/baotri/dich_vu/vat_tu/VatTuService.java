package com.hethong.baotri.dich_vu.vat_tu;

import com.hethong.baotri.kho_du_lieu.vat_tu.VatTuRepository;
import com.hethong.baotri.kho_du_lieu.vat_tu.NhomVatTuRepository;
import com.hethong.baotri.kho_du_lieu.vat_tu.KhoVatTuRepository;
import com.hethong.baotri.thuc_the.vat_tu.VatTu;
import com.hethong.baotri.thuc_the.vat_tu.KhoVatTu;
import com.hethong.baotri.ngoai_le.NgoaiLeVatTu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VatTuService {

    private final VatTuRepository vatTuRepository;
    private final NhomVatTuRepository nhomVatTuRepository;
    private final KhoVatTuRepository khoVatTuRepository;

    public VatTu taoVatTu(VatTu vatTu) {
        log.info("Đang tạo vật tư mới: {}", vatTu.getMaVatTu());

        if (vatTuRepository.existsByMaVatTu(vatTu.getMaVatTu())) {
            throw new NgoaiLeVatTu("Mã vật tư đã tồn tại: " + vatTu.getMaVatTu());
        }

        return vatTuRepository.save(vatTu);
    }

    public VatTu capNhatVatTu(Long idVatTu, VatTu vatTuCapNhat) {
        log.info("Đang cập nhật vật tư ID: {}", idVatTu);

        VatTu vatTu = vatTuRepository.findById(idVatTu)
                .orElseThrow(() -> new NgoaiLeVatTu("Không tìm thấy vật tư với ID: " + idVatTu));

        if (!vatTu.getMaVatTu().equals(vatTuCapNhat.getMaVatTu()) &&
                vatTuRepository.existsByMaVatTu(vatTuCapNhat.getMaVatTu())) {
            throw new NgoaiLeVatTu("Mã vật tư đã tồn tại: " + vatTuCapNhat.getMaVatTu());
        }

        vatTu.setMaVatTu(vatTuCapNhat.getMaVatTu());
        vatTu.setTenVatTu(vatTuCapNhat.getTenVatTu());
        vatTu.setMoTa(vatTuCapNhat.getMoTa());
        vatTu.setDonViTinh(vatTuCapNhat.getDonViTinh());
        vatTu.setNhomVatTu(vatTuCapNhat.getNhomVatTu());

        return vatTuRepository.save(vatTu);
    }

    public void nhapKhoVatTu(Long idVatTu, Integer soLuong, BigDecimal giaNhap, String maKho) {
        log.info("Đang nhập kho vật tư ID: {}, số lượng: {}", idVatTu, soLuong);

        VatTu vatTu = vatTuRepository.findById(idVatTu)
                .orElseThrow(() -> new NgoaiLeVatTu("Không tìm thấy vật tư với ID: " + idVatTu));

        KhoVatTu khoVatTu = khoVatTuRepository.findByMaKhoAndVatTu(maKho, vatTu)
                .orElseGet(() -> {
                    KhoVatTu khoMoi = new KhoVatTu(maKho, "Kho " + vatTu.getTenVatTu(), vatTu);
                    return khoVatTuRepository.save(khoMoi);
                });

        khoVatTu.nhapKho(soLuong, giaNhap, "Nhập kho");
        vatTu.nhapKho(soLuong, giaNhap);

        khoVatTuRepository.save(khoVatTu);
        vatTuRepository.save(vatTu);
    }

    public boolean xuatKhoVatTu(Long idVatTu, Integer soLuong, BigDecimal giaXuat, String maKho) {
        log.info("Đang xuất kho vật tư ID: {}, số lượng: {}", idVatTu, soLuong);

        VatTu vatTu = vatTuRepository.findById(idVatTu)
                .orElseThrow(() -> new NgoaiLeVatTu("Không tìm thấy vật tư với ID: " + idVatTu));

        KhoVatTu khoVatTu = khoVatTuRepository.findByMaKhoAndVatTu(maKho, vatTu)
                .orElseThrow(() -> new NgoaiLeVatTu("Không tìm thấy kho: " + maKho));

        if (khoVatTu.xuatKho(soLuong, giaXuat, "Xuất kho")) {
            vatTu.xuatKho(soLuong, giaXuat);
            khoVatTuRepository.save(khoVatTu);
            vatTuRepository.save(vatTu);
            return true;
        }

        return false;
    }

    @Transactional(readOnly = true)
    public List<VatTu> layVatTuThieuHang() {
        return vatTuRepository.findVatTuThieuHang();
    }

    @Transactional(readOnly = true)
    public List<VatTu> layVatTuHetHan() {
        return vatTuRepository.findVatTuHetHan(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<VatTu> layVatTuCanKiemTra() {
        return vatTuRepository.findVatTuCanKiemTra(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public Page<VatTu> layDanhSachVatTu(Pageable pageable) {
        return vatTuRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<VatTu> timKiemVatTu(String tuKhoa, Pageable pageable) {
        return vatTuRepository.findByTenVatTuContainingIgnoreCase(tuKhoa, pageable);
    }

    @Transactional(readOnly = true)
    public BigDecimal tinhTongGiaTriTonKho() {
        return vatTuRepository.tinhTongGiaTriTonKho();
    }

    @Transactional(readOnly = true)
    public long demVatTuThieuHang() {
        return vatTuRepository.demVatTuThieuHang();
    }
}