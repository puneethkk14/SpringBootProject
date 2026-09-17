package com.library.controller;

import com.library.dto.response.ApiResponse;
import com.library.dto.response.FineResponse;
import com.library.service.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/fines")
@RequiredArgsConstructor
@Tag(name = "Fine Management", description = "APIs for viewing and settling overdue penalties")
public class FineController {

    private final FineService fineService;

    @GetMapping("/pending")
    @Operation(summary = "List All Pending Fines", description = "Retrieves all unpaid library fines across all members.")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getAllPendingFines() {
        log.info("REST request to get all pending fines");
        List<FineResponse> response = fineService.getAllPendingFines();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Fine Details", description = "Retrieves fine details by ID.")
    public ResponseEntity<ApiResponse<FineResponse>> getFineById(@PathVariable Long id) {
        log.info("REST request to get fine ID: {}", id);
        FineResponse response = fineService.getFineById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/member/{memberId}/pending")
    @Operation(summary = "Get Member Pending Fines", description = "Retrieves all unpaid fines for a specific member.")
    public ResponseEntity<ApiResponse<List<FineResponse>>> getMemberPendingFines(@PathVariable Long memberId) {
        log.info("REST request to get pending fines for member ID: {}", memberId);
        List<FineResponse> response = fineService.getPendingFinesByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/member/{memberId}/total")
    @Operation(summary = "Get Total Pending Fine", description = "Calculates the total monetary fine pending for a member.")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalPendingFine(@PathVariable Long memberId) {
        log.info("REST request to calculate total pending fine for member ID: {}", memberId);
        BigDecimal total = fineService.getTotalPendingFineByMember(memberId);
        return ResponseEntity.ok(ApiResponse.success(total, "Total pending fine calculated"));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Settle / Pay Fine", description = "Marks an overdue fine as paid with optional reference code.")
    public ResponseEntity<ApiResponse<FineResponse>> payFine(
            @PathVariable Long id,
            @RequestParam(required = false) String paymentReference) {
        log.info("REST request to pay fine ID: {} with reference: {}", id, paymentReference);
        FineResponse response = fineService.payFine(id, paymentReference);
        return ResponseEntity.ok(ApiResponse.success(response, "Fine settled successfully"));
    }
}
