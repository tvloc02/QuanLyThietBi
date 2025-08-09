package com.hethong.baotri.dieu_khien.debug;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * ✅ Controller xử lý các requests từ browser extension
 * Để tránh lỗi 404 và NoResourceFoundException
 */
@Controller
@RequestMapping("/hybridaction")
@Slf4j
public class StaticResourceController {

    /**
     * ✅ Xử lý request từ browser extension zybTrackerStatisticsAction
     */
    @GetMapping("/zybTrackerStatisticsAction")
    public ResponseEntity<String> handleZybTrackerStatistics(
            @RequestParam(value = "data", required = false) String data,
            @RequestParam(value = "__callback__", required = false) String callback) {

        log.debug("🔍 Browser extension request - Data: {}, Callback: {}", data, callback);

        // ✅ Trả về JSONP response cho browser extension
        String response = "{}"; // Empty JSON object
        if (callback != null && !callback.isEmpty()) {
            response = callback + "(" + response + ");";
        }

        return ResponseEntity.ok()
                .header("Content-Type", "application/javascript")
                .body(response);
    }

    /**
     * ✅ Fallback cho tất cả hybridaction requests khác
     */
    @GetMapping("/**")
    public ResponseEntity<String> handleAllHybridActions() {
        log.debug("🔍 Generic browser extension request");

        return ResponseEntity.ok()
                .header("Content-Type", "application/javascript")
                .body("{}");
    }
}