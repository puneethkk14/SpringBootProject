package com.library.dto.response;

import com.library.entity.enums.BorrowStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecordResponse {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String membershipNumber;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private String remarks;
    private FineResponse fine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
