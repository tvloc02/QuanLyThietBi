package com.hethong.baotri.ngoai_le;

/**
 * Exception cho các lỗi liên quan đến người dùng
 */
public class NgoaiLeNguoiDung extends RuntimeException {

    public NgoaiLeNguoiDung(String message) {
        super(message);
    }

    public NgoaiLeNguoiDung(String message, Throwable cause) {
        super(message, cause);
    }
}