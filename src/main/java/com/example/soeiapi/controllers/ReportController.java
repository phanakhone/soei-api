package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.services.ReportService;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // get user count report
    @GetMapping("/user-count")
    public ResponseEntity<ApiResponse<Map<String, Map<Object, Object>>>> getUserCountReport() {
        Map<String, Map<Object, Object>> userCounts = reportService.getUserCountReport();

        return ResponseEntity.ok(ApiResponse.success("report", userCounts));
    }

}
