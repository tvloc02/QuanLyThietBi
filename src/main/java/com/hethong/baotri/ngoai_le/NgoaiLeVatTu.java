package com.hethong.baotri.ngoai_le;

/**
 * Exception cho các lỗi liên quan đến vật tư
 */
public class NgoaiLeVatTu extends RuntimeException {

    public NgoaiLeVatTu(String message) {
        super(message);
    }

    public NgoaiLeVatTu(String message, Throwable cause) {
        super(message, cause);
    }
}