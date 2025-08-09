package com.hethong.baotri.ngoai_le;

/**
 * Exception cho các lỗi liên quan đến đội bảo trì
 */
public class NgoaiLeDoiBaoTri extends RuntimeException {

    public NgoaiLeDoiBaoTri(String message) {
        super(message);
    }

    public NgoaiLeDoiBaoTri(String message, Throwable cause) {
        super(message, cause);
    }
}