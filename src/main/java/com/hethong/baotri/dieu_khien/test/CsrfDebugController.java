package com.hethong.baotri.dieu_khien.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/csrf-debug")
@Slf4j
public class CsrfDebugController {

    @GetMapping("/token")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCsrfToken(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            response.put("success", true);
            response.put("parameterName", csrfToken.getParameterName());
            response.put("headerName", csrfToken.getHeaderName());
            response.put("token", csrfToken.getToken());
        } else {
            response.put("success", false);
            response.put("message", "CSRF token not found");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-form")
    public String testForm(Model model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        if (csrfToken != null) {
            model.addAttribute("csrfParameterName", csrfToken.getParameterName());
            model.addAttribute("csrfToken", csrfToken.getToken());
        }

        return "test-csrf-form";
    }

    @PostMapping("/test-submit")
    @ResponseBody
    public ResponseEntity<Map<String, String>> testSubmit(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "CSRF token validation passed!");

        log.info("✅ CSRF test form submitted successfully");
        return ResponseEntity.ok(response);
    }
}