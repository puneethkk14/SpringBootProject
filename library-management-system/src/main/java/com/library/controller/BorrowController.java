package com.library.controller;

import com.library.dto.request.IssueBookRequest;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BorrowRecordResponse;
import com.library.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/borrow")
@RequiredArgsConstructor
@Tag(name = "Borrow & Return Operations", description = "Core circulation APIs for issuing and returning books")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping("/issue")
    @Operation(summary = "Issue Book to Member", description = "Issues a book copy to a member with eligibility and inventory checks.")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> issueBook(@Valid @RequestBody IssueBookRequest request) {
        log.info("REST request to issue book ID {} to member ID {}", request.getBookId(), request.getMemberId());
        BorrowRecordResponse response = borrowService.issueBook(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Book issued successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/return/{recordId}")
    @Operation(summary = "Return Book", description = "Returns an issued book, restocks inventory, and calculates overdue fines if applicable.")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> returnBook(
            @PathVariable Long recordId,
            @RequestBody(required = false) ReturnBookRequest request) {
        log.info("REST request to return book for borrow record ID: {}", recordId);
        BorrowRecordResponse response = borrowService.returnBook(recordId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Book returned successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Borrow Record", description = "Retrieves a specific borrow record by ID.")
    public ResponseEntity<ApiResponse<BorrowRecordResponse>> getBorrowRecord(@PathVariable Long id) {
        log.info("REST request to get borrow record ID: {}", id);
        BorrowRecordResponse response = borrowService.getBorrowRecordById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Get Member Borrow History", description = "Retrieves complete borrowing history for a specific member.")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getMemberHistory(@PathVariable Long memberId) {
        log.info("REST request to get borrow history for member ID: {}", memberId);
        List<BorrowRecordResponse> response = borrowService.getMemberBorrowHistory(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get Book Borrow History", description = "Retrieves all historical circulation records for a book.")
    public ResponseEntity<ApiResponse<List<BorrowRecordResponse>>> getBookHistory(@PathVariable Long bookId) {
        log.info("REST request to get circulation history for book ID: {}", bookId);
        List<BorrowRecordResponse> response = borrowService.getBookBorrowHistory(bookId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
