package com.library.mapper;

import com.library.dto.response.FineResponse;
import com.library.entity.Fine;
import org.springframework.stereotype.Component;

@Component
public class FineMapper {

    public FineResponse toResponse(Fine fine) {
        if (fine == null) return null;
        return FineResponse.builder()
                .id(fine.getId())
                .borrowRecordId(fine.getBorrowRecord() != null ? fine.getBorrowRecord().getId() : null)
                .amount(fine.getAmount())
                .overdueDays(fine.getOverdueDays())
                .paid(fine.getPaid())
                .paidDate(fine.getPaidDate())
                .paymentReference(fine.getPaymentReference())
                .remarks(fine.getRemarks())
                .createdAt(fine.getCreatedAt())
                .build();
    }
}
