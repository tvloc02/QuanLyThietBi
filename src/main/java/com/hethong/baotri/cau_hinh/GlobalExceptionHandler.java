package com.hethong.baotri.cau_hinh;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        String requestUrl = ex.getRequestURL();

        // ✅ Bỏ qua favicon và static resources
        if (requestUrl.contains("favicon.ico") ||
                requestUrl.contains("/static/css/") ||
                requestUrl.contains("/js/") ||
                requestUrl.contains("/images/")) {
            log.debug("🔇 Ignoring static resource: {}", requestUrl);
            return "error/404";
        }

        log.warn("🚫 404 Not Found: {}", requestUrl);
        model.addAttribute("errorMessage", "Trang không tồn tại: " + requestUrl);
        model.addAttribute("requestUrl", requestUrl);
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        // ✅ Không log cho favicon
        if (!ex.getMessage().contains("favicon")) {
            log.error("❌ Unexpected error occurred", ex);
        }

        model.addAttribute("errorMessage", "Đã xảy ra lỗi không mong muốn");
        model.addAttribute("errorDetails", ex.getMessage());
        return "error/404";
    }
}