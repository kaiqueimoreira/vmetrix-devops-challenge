package com.vmetrix.svccalc.controller;

import com.vmetrix.calc.FinancialCalc;
import com.vmetrix.svccalc.model.ApiResponse;
import com.vmetrix.svccalc.model.FinancialRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/financial")
public class FinancialController {

    @PostMapping("/compound-interest")
    public ResponseEntity<ApiResponse<Map<String, Object>>> compoundInterest(
            @RequestBody FinancialRequest req) {
        var result = FinancialCalc.compoundInterest(
                req.principal(), req.rate(), req.periods(), req.years());
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "principal", req.principal(),
                "annualRate", req.rate(),
                "periods", req.periods(),
                "years", req.years(),
                "finalAmount", result
        )));
    }

    @PostMapping("/simple-interest")
    public ResponseEntity<ApiResponse<Map<String, Object>>> simpleInterest(
            @RequestBody FinancialRequest req) {
        var result = FinancialCalc.simpleInterest(req.principal(), req.rate(), req.years());
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "principal", req.principal(),
                "annualRate", req.rate(),
                "years", req.years(),
                "interest", result
        )));
    }

    @PostMapping("/pmt")
    public ResponseEntity<ApiResponse<Map<String, Object>>> pmt(@RequestBody FinancialRequest req) {
        var result = FinancialCalc.pmt(req.principal(), req.rate(), req.periods());
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "principal", req.principal(),
                "monthlyRate", req.rate(),
                "months", req.periods(),
                "monthlyPayment", result
        )));
    }

    @GetMapping("/discount")
    public ResponseEntity<ApiResponse<Map<String, Object>>> discount(
            @RequestParam double value,
            @RequestParam double percentage) {
        var result = FinancialCalc.applyDiscount(value, percentage);
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "originalValue", value,
                "discountPercentage", percentage,
                "discountedValue", result
        )));
    }
}
