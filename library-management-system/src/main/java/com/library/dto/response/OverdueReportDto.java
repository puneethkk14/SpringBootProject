package com.library.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OverdueReportDto {

    private Long borrowRecordId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private String membershipNumber;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private Long daysOverdue;
    private BigDecimal estimatedFine;
}
