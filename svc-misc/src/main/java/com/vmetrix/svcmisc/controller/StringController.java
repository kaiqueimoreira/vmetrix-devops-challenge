package com.vmetrix.svcmisc.controller;

import com.vmetrix.misc.StringUtils;
import com.vmetrix.svcmisc.model.ApiResponse;
import com.vmetrix.svcmisc.model.TextRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/string")
public class StringController {

    @PostMapping("/reverse")
    public ResponseEntity<ApiResponse<Map<String, String>>> reverse(@RequestBody TextRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "input", req.value(),
                "result", StringUtils.reverse(req.value())
        )));
    }

    @PostMapping("/palindrome")
    public ResponseEntity<ApiResponse<Map<String, Object>>> palindrome(@RequestBody TextRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "input", req.value(),
                "isPalindrome", StringUtils.isPalindrome(req.value())
        )));
    }

    @PostMapping("/word-count")
    public ResponseEntity<ApiResponse<Map<String, Object>>> wordCount(@RequestBody TextRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "input", req.value(),
                "wordCount", StringUtils.wordCount(req.value())
        )));
    }

    @PostMapping("/title-case")
    public ResponseEntity<ApiResponse<Map<String, String>>> titleCase(@RequestBody TextRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "input", req.value(),
                "result", StringUtils.toTitleCase(req.value())
        )));
    }
}
