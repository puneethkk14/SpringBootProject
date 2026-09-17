package com.library.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FineResponse {

    private Long id;
    private Long borrowRecordId;
    private BigDecimal amount;
    private Long overdueDays;
    private Boolean paid;
    private LocalDate paidDate;
    private String paymentReference;
    private String remarks;
    private LocalDateTime createdAt;
}
