package com.vmetrix.svcmisc.controller;

import com.vmetrix.misc.ValidationUtils;
import com.vmetrix.svcmisc.model.ApiResponse;
import com.vmetrix.svcmisc.model.TextRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/validation")
public class ValidationController {

    @PostMapping("/email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateEmail(@RequestBody TextRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "input", req.value(),
                "valid", ValidationUtils.isValidEmail(req.value())
        )));
    }

    @PostMapping("/cpf")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validateCpf(@RequestBody TextRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "input", req.value(),
                "valid", ValidationUtils.isValidCpf(req.value())
        )));
    }
}
