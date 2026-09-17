package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.dto.response.OverdueReportDto;
import com.library.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports & Analytics", description = "Administrative reports for library operations")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/overdue")
    @Operation(summary = "Generate Overdue Books Report", description = "Generates a detailed report of all currently overdue books and borrower details.")
    public ResponseEntity<ApiResponse<List<OverdueReportDto>>> getOverdueBooksReport() {
        log.info("REST request to generate overdue books report");
        List<OverdueReportDto> report = reportService.getOverdueBooksReport();
        return ResponseEntity.ok(ApiResponse.success(report, "Overdue report generated with " + report.size() + " record(s)"));
    }
}
