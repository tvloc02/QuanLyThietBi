package com.hethong.baotri.ngoai_le;

/**
 * Exception cho các lỗi liên quan đến thiết bị
 */
public class NgoaiLeThietBi extends RuntimeException {

    public NgoaiLeThietBi(String message) {
        super(message);
    }

    public NgoaiLeThietBi(String message, Throwable cause) {
        super(message, cause);
    }
}