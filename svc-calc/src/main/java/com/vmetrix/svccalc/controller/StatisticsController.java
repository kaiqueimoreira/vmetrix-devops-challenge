package com.vmetrix.svccalc.controller;

import com.vmetrix.calc.StatisticsCalc;
import com.vmetrix.svccalc.model.ApiResponse;
import com.vmetrix.svccalc.model.StatisticsRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @PostMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> summary(
            @RequestBody StatisticsRequest req) {
        var values = req.values();
        return ResponseEntity.ok(ApiResponse.ok(Map.of(
                "count",  values.size(),
                "mean",   StatisticsCalc.mean(values),
                "median", StatisticsCalc.median(values),
                "stdDev", StatisticsCalc.standardDeviation(values),
                "min",    StatisticsCalc.min(values),
                "max",    StatisticsCalc.max(values),
                "sum",    StatisticsCalc.sum(values)
        )));
    }
}
