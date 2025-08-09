package com.hethong.baotri.thuc_the.bao_tri;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Thực thể hình ảnh yêu cầu bảo trì
 *
 * @author Đội phát triển hệ thống bảo trì
 * @version 1.0
 */
@Entity
@Table(name = "hinh_anh_yeu_cau")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"yeuCauBaoTri"})
public class HinhAnhYeuCau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hinh_anh")
    private Long idHinhAnh;

    @Size(max = 255, message = "Tên file không được vượt quá 255 ký tự")
    @Column(name = "ten_file", length = 255)
    private String tenFile;

    @Size(max = 500, message = "Đường dẫn không được vượt quá 500 ký tự")
    @Column(name = "duong_dan", length = 500)
    private String duongDan;

    @Column(name = "kich_thuoc_file")
    private Long kichThuocFile; // bytes

    @Size(max = 10, message = "Loại hình ảnh không được vượt quá 10 ký tự")
    @Column(name = "loai_hinh_anh", length = 10)
    private String loaiHinhAnh; // jpg, png, pdf

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    @Column(name = "mo_ta", length = 255)
    private String moTa;

    @Column(name = "ngay_upload", nullable = false)
    private LocalDateTime ngayUpload;

    @Column(name = "thu_tu_hien_thi")
    private Integer thuTuHienThi = 1;

    @Column(name = "la_hinh_chinh")
    private Boolean laHinhChinh = false;

    // Quan hệ với YeuCauBaoTri
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_yeu_cau", nullable = false)
    @NotNull(message = "Yêu cầu bảo trì không được để trống")
    private YeuCauBaoTri yeuCauBaoTri;

    // Quan hệ với NguoiDung (người upload)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nguoi_upload")
    private com.hethong.baotri.thuc_the.nguoi_dung.NguoiDung nguoiUpload;

    @PrePersist
    protected void onCreate() {
        ngayUpload = LocalDateTime.now();
    }

    /**
     * Constructor với thông tin cơ bản
     */
    public HinhAnhYeuCau(String tenFile, String duongDan, String loaiHinhAnh, YeuCauBaoTri yeuCauBaoTri) {
        this.tenFile = tenFile;
        this.duongDan = duongDan;
        this.loaiHinhAnh = loaiHinhAnh;
        this.yeuCauBaoTri = yeuCauBaoTri;
    }

    /**
     * Lấy kích thước file dạng readable
     */
    public String getKichThuocFileReadable() {
        if (kichThuocFile == null) return "0 B";

        String[] units = {"B", "KB", "MB", "GB"};
        int unitIndex = 0;
        double size = kichThuocFile;

        while (size >= 1024 && unitIndex < units.length - 1) {
            size /= 1024;
            unitIndex++;
        }

        return String.format("%.1f %s", size, units[unitIndex]);
    }

    /**
     * Kiểm tra có phải file ảnh không
     */
    public boolean laFileAnh() {
        return loaiHinhAnh != null && (
                loaiHinhAnh.equalsIgnoreCase("jpg") ||
                        loaiHinhAnh.equalsIgnoreCase("jpeg") ||
                        loaiHinhAnh.equalsIgnoreCase("png") ||
                        loaiHinhAnh.equalsIgnoreCase("gif") ||
                        loaiHinhAnh.equalsIgnoreCase("bmp")
        );
    }

    /**
     * Kiểm tra có phải file PDF không
     */
    public boolean laFilePdf() {
        return loaiHinhAnh != null && loaiHinhAnh.equalsIgnoreCase("pdf");
    }

    /**
     * Lấy URL hiển thị
     */
    public String getUrlHienThi() {
        if (duongDan == null) return "";
        return duongDan.startsWith("/") ? duongDan : "/" + duongDan;
    }

    /**
     * Lấy icon hiển thị cho file
     */
    public String getIconClass() {
        if (laFileAnh()) return "bi-image";
        if (laFilePdf()) return "bi-file-pdf";
        return "bi-file-earmark";
    }

    /**
     * Các loại hình ảnh hỗ trợ
     */
    public static final String LOAI_JPG = "jpg";
    public static final String LOAI_JPEG = "jpeg";
    public static final String LOAI_PNG = "png";
    public static final String LOAI_GIF = "gif";
    public static final String LOAI_PDF = "pdf";
    public static final String LOAI_BMP = "bmp";
}