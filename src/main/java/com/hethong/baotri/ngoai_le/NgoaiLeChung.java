package com.hethong.baotri.ngoai_le;

/**
 * Exception chung cho toàn hệ thống
 */
public class NgoaiLeChung extends RuntimeException {

    public NgoaiLeChung(String message) {
        super(message);
    }

    public NgoaiLeChung(String message, Throwable cause) {
        super(message, cause);
    }
}