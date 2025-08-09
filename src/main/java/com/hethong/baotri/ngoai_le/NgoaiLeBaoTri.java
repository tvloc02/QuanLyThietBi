package com.hethong.baotri.ngoai_le;

/**
 * Exception cho các lỗi liên quan đến module bảo trì
 */
public class NgoaiLeBaoTri extends RuntimeException {

    public NgoaiLeBaoTri(String message) {
        super(message);
    }

    public NgoaiLeBaoTri(String message, Throwable cause) {
        super(message, cause);
    }
}