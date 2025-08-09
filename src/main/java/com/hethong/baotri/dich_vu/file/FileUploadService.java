package com.hethong.baotri.dich_vu.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Value("${app.upload.max-file-size:5MB}")
    private String maxFileSize;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp"
    );

    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    /**
     * Upload file và trả về tên file đã lưu
     */
    public String uploadFile(MultipartFile file, String subFolder) {
        try {
            // Validate file
            validateFile(file);

            // Tạo thư mục nếu chưa tồn tại
            Path uploadPath = createUploadDirectory(subFolder);

            // Tạo tên file unique
            String fileName = generateUniqueFileName(file.getOriginalFilename());

            // Lưu file
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Upload file thành công: {}", fileName);
            return subFolder + "/" + fileName;

        } catch (IOException e) {
            log.error("Lỗi khi upload file: {}", e.getMessage());
            throw new RuntimeException("Không thể upload file: " + e.getMessage());
        }
    }

    /**
     * Upload nhiều file
     */
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String subFolder) {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(file -> uploadFile(file, subFolder))
                .toList();
    }

    /**
     * Xóa file
     */
    public boolean deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            boolean deleted = Files.deleteIfExists(path);

            if (deleted) {
                log.info("Xóa file thành công: {}", filePath);
            } else {
                log.warn("File không tồn tại: {}", filePath);
            }

            return deleted;
        } catch (IOException e) {
            log.error("Lỗi khi xóa file {}: {}", filePath, e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra file có tồn tại không
     */
    public boolean fileExists(String filePath) {
        Path path = Paths.get(uploadDir, filePath);
        return Files.exists(path);
    }

    /**
     * Lấy đường dẫn đầy đủ của file
     */
    public String getFullPath(String filePath) {
        return Paths.get(uploadDir, filePath).toString();
    }

    /**
     * Lấy kích thước file
     */
    public long getFileSize(String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            return Files.size(path);
        } catch (IOException e) {
            log.error("Lỗi khi lấy kích thước file {}: {}", filePath, e.getMessage());
            return 0;
        }
    }

    // Private helper methods

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File không được để trống");
        }

        // Kiểm tra kích thước file (5MB)
        long maxSizeBytes = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSizeBytes) {
            throw new RuntimeException("File quá lớn. Kích thước tối đa: 5MB");
        }

        // Kiểm tra loại file
        String contentType = file.getContentType();
        if (contentType == null ||
                (!ALLOWED_IMAGE_TYPES.contains(contentType) && !ALLOWED_DOCUMENT_TYPES.contains(contentType))) {
            throw new RuntimeException("Loại file không được hỗ trợ: " + contentType);
        }

        // Kiểm tra tên file
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new RuntimeException("Tên file không hợp lệ");
        }

        // Kiểm tra extension
        String extension = getFileExtension(fileName);
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "pdf", "doc", "docx");
        if (!allowedExtensions.contains(extension.toLowerCase())) {
            throw new RuntimeException("Phần mở rộng file không được hỗ trợ: " + extension);
        }
    }

    private Path createUploadDirectory(String subFolder) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (subFolder != null && !subFolder.trim().isEmpty()) {
            uploadPath = uploadPath.resolve(subFolder);
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Tạo thư mục upload: {}", uploadPath.toString());
        }

        return uploadPath;
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        String baseName = getBaseName(originalFileName);

        // Tạo timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        // Tạo UUID ngắn
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        // Clean base name (remove special characters)
        String cleanBaseName = baseName.replaceAll("[^a-zA-Z0-9]", "_");
        if (cleanBaseName.length() > 20) {
            cleanBaseName = cleanBaseName.substring(0, 20);
        }

        return String.format("%s_%s_%s.%s", cleanBaseName, timestamp, uuid, extension);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private String getBaseName(String fileName) {
        if (fileName == null) {
            return "file";
        }

        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return fileName;
        }

        return fileName.substring(0, lastDotIndex);
    }

    /**
     * Validate image file specifically
     */
    public void validateImageFile(MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();
        if (!ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new RuntimeException("File phải là hình ảnh (JPG, PNG, GIF, BMP)");
        }
    }

    /**
     * Validate document file specifically
     */
    public void validateDocumentFile(MultipartFile file) {
        validateFile(file);

        String contentType = file.getContentType();
        if (!ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
            throw new RuntimeException("File phải là tài liệu (PDF, DOC, DOCX)");
        }
    }

    /**
     * Get file info
     */
    public FileInfo getFileInfo(String filePath) {
        Path path = Paths.get(uploadDir, filePath);

        if (!Files.exists(path)) {
            return null;
        }

        try {
            FileInfo info = new FileInfo();
            info.setFileName(path.getFileName().toString());
            info.setSize(Files.size(path));
            info.setLastModified(Files.getLastModifiedTime(path).toInstant());
            info.setPath(filePath);

            return info;
        } catch (IOException e) {
            log.error("Lỗi khi lấy thông tin file {}: {}", filePath, e.getMessage());
            return null;
        }
    }

    // Inner class for file information
    public static class FileInfo {
        private String fileName;
        private long size;
        private java.time.Instant lastModified;
        private String path;

        // Getters and Setters
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }

        public long getSize() { return size; }
        public void setSize(long size) { this.size = size; }

        public java.time.Instant getLastModified() { return lastModified; }
        public void setLastModified(java.time.Instant lastModified) { this.lastModified = lastModified; }

        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
    }
}