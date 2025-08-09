package com.hethong.baotri.dich_vu.bao_tri;

import com.hethong.baotri.kho_du_lieu.bao_tri.YeuCauBaoTriRepository;
import com.hethong.baotri.kho_du_lieu.bao_tri.HinhAnhYeuCauRepository;
import com.hethong.baotri.kho_du_lieu.thiet_bi.ThietBiRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.kho_du_lieu.lop_hoc.LopHocRepository;
import com.hethong.baotri.kho_du_lieu.doi_bao_tri.DoiBaoTriRepository;
import com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri;
import com.hethong.baotri.thuc_the.bao_tri.HinhAnhYeuCau;
import com.hethong.baotri.thuc_the.thiet_bi.ThietBi;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.thuc_the.lop_hoc.LopHoc;
import com.hethong.baotri.thuc_the.doi_bao_tri.DoiBaoTri;
import com.hethong.baotri.dto.bao_tri.YeuCauBaoTriDTO;
import com.hethong.baotri.ngoai_le.NgoaiLeBaoTri;
import com.hethong.baotri.dich_vu.thong_bao.NotificationService;
import com.hethong.baotri.dich_vu.file.FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class YeuCauBaoTriService {

    private final YeuCauBaoTriRepository yeuCauBaoTriRepository;
    private final HinhAnhYeuCauRepository hinhAnhYeuCauRepository;
    private final ThietBiRepository thietBiRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final LopHocRepository lopHocRepository;
    private final DoiBaoTriRepository doiBaoTriRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;
    private final FileUploadService fileUploadService;

    /**
     * Tạo yêu cầu bảo trì mới (Giáo viên)
     */
    public YeuCauBaoTriDTO taoYeuCauBaoTri(YeuCauBaoTriDTO yeuCauDTO, List<MultipartFile> hinhAnhList) {
        log.info("Đang tạo yêu cầu bảo trì mới: {}", yeuCauDTO.getTieuDe());

        // Tạo mã yêu cầu tự động
        String maYeuCau = taoMaYeuCauTuDong();

        // Kiểm tra mã yêu cầu đã tồn tại
        while (yeuCauBaoTriRepository.existsByMaYeuCau(maYeuCau)) {
            maYeuCau = taoMaYeuCauTuDong();
        }

        YeuCauBaoTri yeuCau = modelMapper.map(yeuCauDTO, YeuCauBaoTri.class);
        yeuCau.setMaYeuCau(maYeuCau);
        yeuCau.setNgayTao(LocalDateTime.now());
        yeuCau.setTrangThai("CHO_DUYET");

        // Validate và set người tạo
        NguoiDung nguoiTao = nguoiDungRepository.findById(yeuCauDTO.getIdNguoiTao())
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy người tạo với ID: " + yeuCauDTO.getIdNguoiTao()));
        yeuCau.setNguoiTao(nguoiTao);

        // Validate và set thiết bị
        if (yeuCauDTO.getIdThietBi() != null) {
            ThietBi thietBi = thietBiRepository.findById(yeuCauDTO.getIdThietBi())
                    .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy thiết bị với ID: " + yeuCauDTO.getIdThietBi()));
            yeuCau.setThietBi(thietBi);
        }

        // Validate và set lớp học
        if (yeuCauDTO.getIdLopHoc() != null) {
            LopHoc lopHoc = lopHocRepository.findById(yeuCauDTO.getIdLopHoc())
                    .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy lớp học với ID: " + yeuCauDTO.getIdLopHoc()));
            yeuCau.setLopHoc(lopHoc);

            // Kiểm tra quyền: Giáo viên chỉ có thể tạo yêu cầu cho lớp mình chủ nhiệm
            if (nguoiTao.coVaiTro("GIAO_VIEN") &&
                    !lopHoc.getGiaoVienChuNhiem().getIdNguoiDung().equals(nguoiTao.getIdNguoiDung())) {
                throw new NgoaiLeBaoTri("Giáo viên chỉ có thể tạo yêu cầu cho lớp mình chủ nhiệm");
            }
        }

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        // Xử lý upload hình ảnh
        if (hinhAnhList != null && !hinhAnhList.isEmpty()) {
            for (MultipartFile file : hinhAnhList) {
                if (!file.isEmpty()) {
                    String fileName = fileUploadService.uploadFile(file, "yeu-cau-bao-tri");

                    HinhAnhYeuCau hinhAnh = new HinhAnhYeuCau();
                    hinhAnh.setYeuCauBaoTri(savedYeuCau);
                    hinhAnh.setTenFile(file.getOriginalFilename());
                    hinhAnh.setDuongDan(fileName);
                    hinhAnh.setKichThuocFile(file.getSize());
                    hinhAnh.setLoaiHinhAnh(getFileExtension(file.getOriginalFilename()));
                    hinhAnh.setNgayUpload(LocalDateTime.now());

                    hinhAnhYeuCauRepository.save(hinhAnh);
                }
            }
        }

        // Gửi thông báo cho admin/CSVC
        notificationService.guiThongBaoYeuCauMoi(savedYeuCau);

        log.info("Tạo yêu cầu bảo trì thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Duyệt yêu cầu bảo trì (Admin/CSVC)
     */
    public YeuCauBaoTriDTO duyetYeuCau(Long idYeuCau, Long idNguoiDuyet, String ghiChuDuyet) {
        log.info("Đang duyệt yêu cầu bảo trì ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        if (!"CHO_DUYET".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Yêu cầu không ở trạng thái chờ duyệt");
        }

        NguoiDung nguoiDuyet = nguoiDungRepository.findById(idNguoiDuyet)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy người duyệt với ID: " + idNguoiDuyet));

        yeuCau.setTrangThai("DA_DUYET");
        yeuCau.setNguoiDuyet(nguoiDuyet);
        yeuCau.setNgayDuyet(LocalDateTime.now());
        yeuCau.setGhiChuDuyet(ghiChuDuyet);

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        // Gửi thông báo cho người tạo yêu cầu
        notificationService.guiThongBaoYeuCauDuocDuyet(savedYeuCau);

        log.info("Duyệt yêu cầu thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Từ chối yêu cầu bảo trì (Admin/CSVC)
     */
    public YeuCauBaoTriDTO tuChoiYeuCau(Long idYeuCau, Long idNguoiDuyet, String lyDoTuChoi) {
        log.info("Đang từ chối yêu cầu bảo trì ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        if (!"CHO_DUYET".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Yêu cầu không ở trạng thái chờ duyệt");
        }

        NguoiDung nguoiDuyet = nguoiDungRepository.findById(idNguoiDuyet)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy người duyệt với ID: " + idNguoiDuyet));

        yeuCau.setTrangThai("TU_CHOI");
        yeuCau.setNguoiDuyet(nguoiDuyet);
        yeuCau.setNgayDuyet(LocalDateTime.now());
        yeuCau.setGhiChuDuyet(lyDoTuChoi);

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        // Gửi thông báo cho người tạo yêu cầu
        notificationService.guiThongBaoYeuCauBiTuChoi(savedYeuCau);

        log.info("Từ chối yêu cầu thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Phân công đội bảo trì xử lý yêu cầu
     */
    public YeuCauBaoTriDTO phanCongDoiBaoTri(Long idYeuCau, Long idDoiBaoTri, Long idNguoiXuLy) {
        log.info("Đang phân công đội bảo trì cho yêu cầu ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        if (!"DA_DUYET".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Yêu cầu phải được duyệt trước khi phân công");
        }

        DoiBaoTri doiBaoTri = doiBaoTriRepository.findById(idDoiBaoTri)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy đội bảo trì với ID: " + idDoiBaoTri));

        NguoiDung nguoiXuLy = nguoiDungRepository.findById(idNguoiXuLy)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy người xử lý với ID: " + idNguoiXuLy));

        // Kiểm tra người xử lý có thuộc đội bảo trì không
        if (!doiBaoTri.laThanhVien(nguoiXuLy)) {
            throw new NgoaiLeBaoTri("Người xử lý không thuộc đội bảo trì được chỉ định");
        }

        yeuCau.setDoiBaoTri(doiBaoTri);
        yeuCau.setNguoiXuLy(nguoiXuLy);

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        // Gửi thông báo cho người xử lý
        notificationService.guiThongBaoYeuCauDuocPhanCong(savedYeuCau);

        log.info("Phân công đội bảo trì thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Bắt đầu xử lý yêu cầu (Kỹ thuật viên)
     */
    public YeuCauBaoTriDTO batDauXuLy(Long idYeuCau, Long idNguoiXuLy) {
        log.info("Đang bắt đầu xử lý yêu cầu ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        if (!"DA_DUYET".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Yêu cầu phải được duyệt trước khi xử lý");
        }

        // Kiểm tra quyền xử lý
        if (!yeuCau.getNguoiXuLy().getIdNguoiDung().equals(idNguoiXuLy)) {
            throw new NgoaiLeBaoTri("Bạn không có quyền xử lý yêu cầu này");
        }

        yeuCau.setTrangThai("DANG_XU_LY");
        yeuCau.setNgayBatDauXuLy(LocalDateTime.now());

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        // Gửi thông báo cập nhật tiến độ
        notificationService.guiThongBaoCapNhatTienDo(savedYeuCau);

        log.info("Bắt đầu xử lý yêu cầu thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Hoàn thành yêu cầu bảo trì (Kỹ thuật viên)
     */
    public YeuCauBaoTriDTO hoanThanhYeuCau(Long idYeuCau, String ketQuaXuLy, BigDecimal chiPhiSuaChua) {
        log.info("Đang hoàn thành yêu cầu ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        if (!"DANG_XU_LY".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Yêu cầu phải ở trạng thái đang xử lý");
        }

        yeuCau.setTrangThai("HOAN_THANH");
        yeuCau.setNgayHoanThanh(LocalDateTime.now());
        yeuCau.setKetQuaXuLy(ketQuaXuLy);
        yeuCau.setChiPhiSuaChua(chiPhiSuaChua);

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        // Gửi thông báo hoàn thành cho người tạo yêu cầu
        notificationService.guiThongBaoYeuCauHoanThanh(savedYeuCau);

        log.info("Hoàn thành yêu cầu thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Đánh giá yêu cầu bảo trì (Giáo viên)
     */
    public YeuCauBaoTriDTO danhGiaYeuCau(Long idYeuCau, Integer danhGia, String nhanXet) {
        log.info("Đang đánh giá yêu cầu ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        if (!"HOAN_THANH".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Chỉ có thể đánh giá yêu cầu đã hoàn thành");
        }

        if (danhGia < 1 || danhGia > 5) {
            throw new NgoaiLeBaoTri("Đánh giá phải từ 1 đến 5 sao");
        }

        yeuCau.setDanhGiaCuaNguoiTao(danhGia);
        yeuCau.setNhanXetCuaNguoiTao(nhanXet);

        YeuCauBaoTri savedYeuCau = yeuCauBaoTriRepository.save(yeuCau);

        log.info("Đánh giá yêu cầu thành công: {}", savedYeuCau.getMaYeuCau());
        return modelMapper.map(savedYeuCau, YeuCauBaoTriDTO.class);
    }

    /**
     * Lấy danh sách yêu cầu bảo trì
     */
    @Transactional(readOnly = true)
    public Page<YeuCauBaoTriDTO> layDanhSachYeuCau(Pageable pageable) {
        Page<YeuCauBaoTri> yeuCauPage = yeuCauBaoTriRepository.findAll(pageable);
        return yeuCauPage.map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class));
    }

    /**
     * Lấy yêu cầu theo trạng thái
     */
    @Transactional(readOnly = true)
    public Page<YeuCauBaoTriDTO> layYeuCauTheoTrangThai(String trangThai, Pageable pageable) {
        Page<YeuCauBaoTri> yeuCauPage = yeuCauBaoTriRepository.findByTrangThai(trangThai, pageable);
        return yeuCauPage.map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class));
    }

    /**
     * Lấy yêu cầu của giáo viên
     */
    @Transactional(readOnly = true)
    public Page<YeuCauBaoTriDTO> layYeuCauCuaGiaoVien(Long idGiaoVien, Pageable pageable) {
        Page<YeuCauBaoTri> yeuCauPage = yeuCauBaoTriRepository.findByNguoiTao_IdNguoiDung(idGiaoVien, pageable);
        return yeuCauPage.map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class));
    }

    /**
     * Lấy yêu cầu được phân công cho kỹ thuật viên
     */
    @Transactional(readOnly = true)
    public Page<YeuCauBaoTriDTO> layYeuCauCuaKyThuatVien(Long idKyThuatVien, Pageable pageable) {
        Page<YeuCauBaoTri> yeuCauPage = yeuCauBaoTriRepository.findByNguoiXuLy_IdNguoiDung(idKyThuatVien, pageable);
        return yeuCauPage.map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class));
    }

    /**
     * Lấy yêu cầu theo lớp học
     */
    @Transactional(readOnly = true)
    public List<YeuCauBaoTriDTO> layYeuCauTheoLopHoc(Long idLopHoc) {
        List<YeuCauBaoTri> yeuCauList = yeuCauBaoTriRepository.findByLopHoc_IdLopHoc(idLopHoc);
        return yeuCauList.stream()
                .map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class))
                .toList();
    }

    /**
     * Lấy yêu cầu theo thiết bị
     */
    @Transactional(readOnly = true)
    public List<YeuCauBaoTriDTO> layYeuCauTheoThietBi(Long idThietBi) {
        List<YeuCauBaoTri> yeuCauList = yeuCauBaoTriRepository.findByThietBi_IdThietBi(idThietBi);
        return yeuCauList.stream()
                .map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class))
                .toList();
    }

    /**
     * Lấy yêu cầu ưu tiên
     */
    @Transactional(readOnly = true)
    public List<YeuCauBaoTriDTO> layYeuCauUuTien(String trangThai, String mucDoUuTien) {
        List<YeuCauBaoTri> yeuCauList = yeuCauBaoTriRepository.findByTrangThaiAndMucDoUuTienOrderByNgayTaoDesc(trangThai, mucDoUuTien);
        return yeuCauList.stream()
                .map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class))
                .toList();
    }

    /**
     * Lấy yêu cầu quá hạn
     */
    @Transactional(readOnly = true)
    public List<YeuCauBaoTriDTO> layYeuCauQuaHan() {
        LocalDateTime ngayHienTai = LocalDateTime.now();
        List<YeuCauBaoTri> yeuCauList = yeuCauBaoTriRepository.findYeuCauQuaHan(ngayHienTai);
        return yeuCauList.stream()
                .map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class))
                .toList();
    }

    /**
     * Tìm kiếm yêu cầu bảo trì
     */
    @Transactional(readOnly = true)
    public Page<YeuCauBaoTriDTO> timKiemYeuCau(String tuKhoa, Pageable pageable) {
        Page<YeuCauBaoTri> yeuCauPage = yeuCauBaoTriRepository.timKiemYeuCau(tuKhoa, pageable);
        return yeuCauPage.map(yeuCau -> modelMapper.map(yeuCau, YeuCauBaoTriDTO.class));
    }

    /**
     * Thống kê yêu cầu theo trạng thái
     */
    @Transactional(readOnly = true)
    public List<Object[]> thongKeYeuCauTheoTrangThai() {
        return yeuCauBaoTriRepository.thongKeYeuCauTheoTrangThai();
    }

    /**
     * Thống kê yêu cầu theo mức độ ưu tiên
     */
    @Transactional(readOnly = true)
    public List<Object[]> thongKeYeuCauTheoMucDoUuTien() {
        return yeuCauBaoTriRepository.thongKeYeuCauTheoMucDoUuTien();
    }

    /**
     * Đếm yêu cầu theo trạng thái
     */
    @Transactional(readOnly = true)
    public long demYeuCauTheoTrangThai(String trangThai) {
        return yeuCauBaoTriRepository.demYeuCauTheoTrangThai(trangThai);
    }

    /**
     * Lấy hình ảnh của yêu cầu
     */
    @Transactional(readOnly = true)
    public List<HinhAnhYeuCau> layHinhAnhYeuCau(Long idYeuCau) {
        return hinhAnhYeuCauRepository.findByYeuCauBaoTri_IdYeuCau(idYeuCau);
    }

    /**
     * Xóa yêu cầu bảo trì
     */
    public void xoaYeuCau(Long idYeuCau) {
        log.info("Đang xóa yêu cầu bảo trì ID: {}", idYeuCau);

        YeuCauBaoTri yeuCau = yeuCauBaoTriRepository.findById(idYeuCau)
                .orElseThrow(() -> new NgoaiLeBaoTri("Không tìm thấy yêu cầu với ID: " + idYeuCau));

        // Chỉ cho phép xóa yêu cầu chưa được xử lý
        if ("DANG_XU_LY".equals(yeuCau.getTrangThai()) || "HOAN_THANH".equals(yeuCau.getTrangThai())) {
            throw new NgoaiLeBaoTri("Không thể xóa yêu cầu đang xử lý hoặc đã hoàn thành");
        }

        // Xóa hình ảnh liên quan
        List<HinhAnhYeuCau> hinhAnhList = hinhAnhYeuCauRepository.findByYeuCauBaoTri_IdYeuCau(idYeuCau);
        for (HinhAnhYeuCau hinhAnh : hinhAnhList) {
            fileUploadService.deleteFile(hinhAnh.getDuongDan());
            hinhAnhYeuCauRepository.delete(hinhAnh);
        }

        yeuCauBaoTriRepository.delete(yeuCau);
        log.info("Xóa yêu cầu bảo trì thành công: {}", yeuCau.getMaYeuCau());
    }

    // Helper methods
    private String taoMaYeuCauTuDong() {
        String prefix = "YC" + LocalDateTime.now().getYear();
        String suffix = String.format("%06d", (int) (Math.random() * 1000000));
        return prefix + suffix;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    }
}