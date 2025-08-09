package com.hethong.baotri.dich_vu.thong_bao;

import com.hethong.baotri.kho_du_lieu.thong_bao.ThongBaoRepository;
import com.hethong.baotri.kho_du_lieu.nguoi_dung.NguoiDungRepository;
import com.hethong.baotri.thuc_the.bao_tri.YeuCauBaoTri;
import com.hethong.baotri.thuc_the.thong_bao.ThongBao;
import com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung;
import com.hethong.baotri.dich_vu.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

    private final ThongBaoRepository thongBaoRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final EmailService emailService;

    /**
     * Gửi thông báo yêu cầu bảo trì mới
     */
    public void guiThongBaoYeuCauMoi(YeuCauBaoTri yeuCau) {
        log.info("Gửi thông báo yêu cầu mới: {}", yeuCau.getMaYeuCau());

        // Gửi cho admin và CSVC
        List<NguoiDung> nguoiNhanList = nguoiDungRepository.findByVaiTroIn(
                List.of("QUAN_TRI_VIEN", "TRUONG_PHONG_CSVC", "NHAN_VIEN_CSVC")
        );

        String tieuDe = "Yêu cầu bảo trì mới: " + yeuCau.getTieuDe();
        String noiDung = String.format(
                "Có yêu cầu bảo trì mới cần được duyệt:\n\n" +
                        "Mã yêu cầu: %s\n" +
                        "Tiêu đề: %s\n" +
                        "Lớp học: %s\n" +
                        "Thiết bị: %s\n" +
                        "Mức độ ưu tiên: %s\n" +
                        "Người tạo: %s\n" +
                        "Ngày tạo: %s\n\n" +
                        "Vui lòng đăng nhập hệ thống để xem chi tiết và duyệt yêu cầu.",
                yeuCau.getMaYeuCau(),
                yeuCau.getTieuDe(),
                yeuCau.getLopHoc() != null ? yeuCau.getLopHoc().getTenLop() : "N/A",
                yeuCau.getThietBi() != null ? yeuCau.getThietBi().getTenThietBi() : "N/A",
                yeuCau.getMucDoUuTien(),
                yeuCau.getNguoiTao().getHoVaTen(),
                yeuCau.getNgayTao()
        );

        for (NguoiDung nguoiNhan : nguoiNhanList) {
            taoThongBao(nguoiNhan, tieuDe, noiDung, "YEU_CAU_MOI", yeuCau.getIdYeuCau());

            // Gửi email nếu có
            if (nguoiNhan.getEmail() != null && !nguoiNhan.getEmail().isEmpty()) {
                emailService.guiEmailThongBao(nguoiNhan.getEmail(), tieuDe, noiDung);
            }
        }
    }

    /**
     * Gửi thông báo yêu cầu được duyệt
     */
    public void guiThongBaoYeuCauDuocDuyet(YeuCauBaoTri yeuCau) {
        log.info("Gửi thông báo yêu cầu được duyệt: {}", yeuCau.getMaYeuCau());

        String tieuDe = "Yêu cầu bảo trì đã được duyệt: " + yeuCau.getMaYeuCau();
        String noiDung = String.format(
                "Yêu cầu bảo trì của bạn đã được duyệt:\n\n" +
                        "Mã yêu cầu: %s\n" +
                        "Tiêu đề: %s\n" +
                        "Người duyệt: %s\n" +
                        "Ngày duyệt: %s\n" +
                        "Ghi chú: %s\n\n" +
                        "Yêu cầu sẽ sớm được phân công xử lý.",
                yeuCau.getMaYeuCau(),
                yeuCau.getTieuDe(),
                yeuCau.getNguoiDuyet().getHoVaTen(),
                yeuCau.getNgayDuyet(),
                yeuCau.getGhiChuDuyet() != null ? yeuCau.getGhiChuDuyet() : "Không có ghi chú"
        );

        taoThongBao(yeuCau.getNguoiTao(), tieuDe, noiDung, "YEU_CAU_DUYET", yeuCau.getIdYeuCau());

        if (yeuCau.getNguoiTao().getEmail() != null) {
            emailService.guiEmailThongBao(yeuCau.getNguoiTao().getEmail(), tieuDe, noiDung);
        }
    }

    /**
     * Gửi thông báo yêu cầu bị từ chối
     */
    public void guiThongBaoYeuCauBiTuChoi(YeuCauBaoTri yeuCau) {
        log.info("Gửi thông báo yêu cầu bị từ chối: {}", yeuCau.getMaYeuCau());

        String tieuDe = "Yêu cầu bảo trì bị từ chối: " + yeuCau.getMaYeuCau();
        String noiDung = String.format(
                "Yêu cầu bảo trì của bạn đã bị từ chối:\n\n" +
                        "Mã yêu cầu: %s\n" +
                        "Tiêu đề: %s\n" +
                        "Người từ chối: %s\n" +
                        "Ngày từ chối: %s\n" +
                        "Lý do từ chối: %s\n\n" +
                        "Vui lòng liên hệ với bộ phận CSVC để biết thêm thông tin.",
                yeuCau.getMaYeuCau(),
                yeuCau.getTieuDe(),
                yeuCau.getNguoiDuyet().getHoVaTen(),
                yeuCau.getNgayDuyet(),
                yeuCau.getGhiChuDuyet()
        );

        taoThongBao(yeuCau.getNguoiTao(), tieuDe, noiDung, "YEU_CAU_TU_CHOI", yeuCau.getIdYeuCau());

        if (yeuCau.getNguoiTao().getEmail() != null) {
            emailService.guiEmailThongBao(yeuCau.getNguoiTao().getEmail(), tieuDe, noiDung);
        }
    }

    /**
     * Gửi thông báo yêu cầu được phân công
     */
    public void guiThongBaoYeuCauDuocPhanCong(YeuCauBaoTri yeuCau) {
        log.info("Gửi thông báo yêu cầu được phân công: {}", yeuCau.getMaYeuCau());

        String tieuDe = "Có yêu cầu bảo trì mới được phân công: " + yeuCau.getMaYeuCau();
        String noiDung = String.format(
                "Bạn đã được phân công xử lý yêu cầu bảo trì:\n\n" +
                        "Mã yêu cầu: %s\n" +
                        "Tiêu đề: %s\n" +
                        "Lớp học: %s\n" +
                        "Thiết bị: %s\n" +
                        "Mức độ ưu tiên: %s\n" +
                        "Đội bảo trì: %s\n" +
                        "Mô tả: %s\n\n" +
                        "Vui lòng đăng nhập hệ thống để xem chi tiết và bắt đầu xử lý.",
                yeuCau.getMaYeuCau(),
                yeuCau.getTieuDe(),
                yeuCau.getLopHoc() != null ? yeuCau.getLopHoc().getTenLop() : "N/A",
                yeuCau.getThietBi() != null ? yeuCau.getThietBi().getTenThietBi() : "N/A",
                yeuCau.getMucDoUuTien(),
                yeuCau.getDoiBaoTri() != null ? yeuCau.getDoiBaoTri().getTenDoi() : "N/A",
                yeuCau.getMoTaChiTiet()
        );

        taoThongBao(yeuCau.getNguoiXuLy(), tieuDe, noiDung, "PHAN_CONG_CONG_VIEC", yeuCau.getIdYeuCau());

        if (yeuCau.getNguoiXuLy().getEmail() != null) {
            emailService.guiEmailThongBao(yeuCau.getNguoiXuLy().getEmail(), tieuDe, noiDung);
        }
    }

    /**
     * Gửi thông báo cập nhật tiến độ
     */
    public void guiThongBaoCapNhatTienDo(YeuCauBaoTri yeuCau) {
        log.info("Gửi thông báo cập nhật tiến độ: {}", yeuCau.getMaYeuCau());

        String tieuDe = "Cập nhật tiến độ yêu cầu bảo trì: " + yeuCau.getMaYeuCau();
        String noiDung = String.format(
                "Yêu cầu bảo trì của bạn đã có cập nhật tiến độ:\n\n" +
                        "Mã yêu cầu: %s\n" +
                        "Tiêu đề: %s\n" +
                        "Trạng thái: %s\n" +
                        "Người xử lý: %s\n" +
                        "Ngày bắt đầu xử lý: %s\n\n" +
                        "Chúng tôi sẽ thông báo khi có cập nhật tiếp theo.",
                yeuCau.getMaYeuCau(),
                yeuCau.getTieuDe(),
                getTrangThaiDisplay(yeuCau.getTrangThai()),
                yeuCau.getNguoiXuLy() != null ? yeuCau.getNguoiXuLy().getHoVaTen() : "N/A",
                yeuCau.getNgayBatDauXuLy()
        );

        taoThongBao(yeuCau.getNguoiTao(), tieuDe, noiDung, "CAP_NHAT_TIEN_DO", yeuCau.getIdYeuCau());

        if (yeuCau.getNguoiTao().getEmail() != null) {
            emailService.guiEmailThongBao(yeuCau.getNguoiTao().getEmail(), tieuDe, noiDung);
        }
    }

    /**
     * Gửi thông báo yêu cầu hoàn thành
     */
    public void guiThongBaoYeuCauHoanThanh(YeuCauBaoTri yeuCau) {
        log.info("Gửi thông báo yêu cầu hoàn thành: {}", yeuCau.getMaYeuCau());

        String tieuDe = "Yêu cầu bảo trì đã hoàn thành: " + yeuCau.getMaYeuCau();
        String noiDung = String.format(
                "Yêu cầu bảo trì của bạn đã được hoàn thành:\n\n" +
                        "Mã yêu cầu: %s\n" +
                        "Tiêu đề: %s\n" +
                        "Người xử lý: %s\n" +
                        "Ngày hoàn thành: %s\n" +
                        "Kết quả xử lý: %s\n" +
                        "Chi phí sửa chữa: %s\n\n" +
                        "Vui lòng kiểm tra và đánh giá chất lượng dịch vụ.",
                yeuCau.getMaYeuCau(),
                yeuCau.getTieuDe(),
                yeuCau.getNguoiXuLy().getHoVaTen(),
                yeuCau.getNgayHoanThanh(),
                yeuCau.getKetQuaXuLy(),
                yeuCau.getChiPhiSuaChua() != null ?
                        String.format("%,.0f VNĐ", yeuCau.getChiPhiSuaChua()) : "Không có"
        );

        taoThongBao(yeuCau.getNguoiTao(), tieuDe, noiDung, "YEU_CAU_HOAN_THANH", yeuCau.getIdYeuCau());

        if (yeuCau.getNguoiTao().getEmail() != null) {
            emailService.guiEmailThongBao(yeuCau.getNguoiTao().getEmail(), tieuDe, noiDung);
        }
    }

    /**
     * Tạo thông báo trong hệ thống
     */
    public ThongBao taoThongBao(NguoiDung nguoiNhan, String tieuDe, String noiDung,
                                String loaiThongBao, Long idLienQuan) {
        ThongBao thongBao = new ThongBao();
        thongBao.setNguoiNhan(nguoiNhan);
        thongBao.setTieuDe(tieuDe);
        thongBao.setNoiDung(noiDung);
        thongBao.setLoaiThongBao(loaiThongBao);
        thongBao.setIdLienQuan(idLienQuan);
        thongBao.setNgayTao(LocalDateTime.now());
        thongBao.setDaDoc(false);

        return thongBaoRepository.save(thongBao);
    }

    /**
     * Lấy danh sách thông báo của người dùng
     */
    @Transactional(readOnly = true)
    public Page<ThongBao> layThongBaoCuaNguoiDung(Long idNguoiDung, Pageable pageable) {
        return thongBaoRepository.findByNguoiNhan_IdNguoiDungOrderByNgayTaoDesc(idNguoiDung, pageable);
    }

    /**
     * Lấy thông báo chưa đọc
     */
    @Transactional(readOnly = true)
    public List<ThongBao> layThongBaoChuaDoc(Long idNguoiDung) {
        return thongBaoRepository.findByNguoiNhan_IdNguoiDungAndDaDocFalseOrderByNgayTaoDesc(idNguoiDung);
    }

    /**
     * Đánh dấu đã đọc thông báo
     */
    public void danhDauDaDoc(Long idThongBao) {
        ThongBao thongBao = thongBaoRepository.findById(idThongBao)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo với ID: " + idThongBao));

        thongBao.setDaDoc(true);
        thongBao.setNgayDoc(LocalDateTime.now());
        thongBaoRepository.save(thongBao);
    }

    /**
     * Đánh dấu tất cả thông báo đã đọc
     */
    public void danhDauTatCaDaDoc(Long idNguoiDung) {
        List<ThongBao> thongBaoList = thongBaoRepository
                .findByNguoiNhan_IdNguoiDungAndDaDocFalse(idNguoiDung);

        for (ThongBao thongBao : thongBaoList) {
            thongBao.setDaDoc(true);
            thongBao.setNgayDoc(LocalDateTime.now());
        }

        thongBaoRepository.saveAll(thongBaoList);
    }

    /**
     * Đếm thông báo chưa đọc
     */
    @Transactional(readOnly = true)
    public long demThongBaoChuaDoc(Long idNguoiDung) {
        return thongBaoRepository.countByNguoiNhan_IdNguoiDungAndDaDocFalse(idNguoiDung);
    }

    /**
     * Xóa thông báo
     */
    public void xoaThongBao(Long idThongBao) {
        if (!thongBaoRepository.existsById(idThongBao)) {
            throw new RuntimeException("Không tìm thấy thông báo với ID: " + idThongBao);
        }

        thongBaoRepository.deleteById(idThongBao);
    }

    /**
     * Xóa thông báo cũ (quá 30 ngày)
     */
    public void xoaThongBaoCu() {
        LocalDateTime ngayCu = LocalDateTime.now().minusDays(30);
        List<ThongBao> thongBaoCu = thongBaoRepository.findByNgayTaoBefore(ngayCu);

        if (!thongBaoCu.isEmpty()) {
            thongBaoRepository.deleteAll(thongBaoCu);
            log.info("Đã xóa {} thông báo cũ", thongBaoCu.size());
        }
    }

    /**
     * Gửi thông báo tùy chỉnh
     */
    public void guiThongBaoTuyChon(List<Long> danhSachNguoiNhan, String tieuDe, String noiDung, String loaiThongBao) {
        List<NguoiDung> nguoiDungList = nguoiDungRepository.findAllById(danhSachNguoiNhan);

        for (NguoiDung nguoiDung : nguoiDungList) {
            taoThongBao(nguoiDung, tieuDe, noiDung, loaiThongBao, null);

            // Gửi email nếu có
            if (nguoiDung.getEmail() != null && !nguoiDung.getEmail().isEmpty()) {
                emailService.guiEmailThongBao(nguoiDung.getEmail(), tieuDe, noiDung);
            }
        }

        log.info("Đã gửi thông báo tùy chỉnh cho {} người dùng", nguoiDungList.size());
    }

    // Helper method
    private String getTrangThaiDisplay(String trangThai) {
        return switch (trangThai) {
            case "CHO_DUYET" -> "Chờ duyệt";
            case "DA_DUYET" -> "Đã duyệt";
            case "DANG_XU_LY" -> "Đang xử lý";
            case "HOAN_THANH" -> "Hoàn thành";
            case "TU_CHOI" -> "Từ chối";
            default -> trangThai;
        };
    }
}