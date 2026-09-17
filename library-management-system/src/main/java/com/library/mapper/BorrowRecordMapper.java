package com.library.mapper;

import com.library.dto.response.BorrowRecordResponse;
import com.library.dto.response.OverdueReportDto;
import com.library.entity.BorrowRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class BorrowRecordMapper {

    private final FineMapper fineMapper;

    public BorrowRecordResponse toResponse(BorrowRecord record) {
        if (record == null) return null;

        String memberName = record.getMember() != null
                ? record.getMember().getFirstName() + " " + record.getMember().getLastName()
                : null;

        return BorrowRecordResponse.builder()
                .id(record.getId())
                .bookId(record.getBook() != null ? record.getBook().getId() : null)
                .bookTitle(record.getBook() != null ? record.getBook().getTitle() : null)
                .bookIsbn(record.getBook() != null ? record.getBook().getIsbn() : null)
                .memberId(record.getMember() != null ? record.getMember().getId() : null)
                .memberName(memberName)
                .memberEmail(record.getMember() != null ? record.getMember().getEmail() : null)
                .membershipNumber(record.getMember() != null ? record.getMember().getMembershipNumber() : null)
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .remarks(record.getRemarks())
                .fine(fineMapper.toResponse(record.getFine()))
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }

    public OverdueReportDto toOverdueReportDto(BorrowRecord record, BigDecimal dailyFineRate) {
        if (record == null) return null;

        long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
        if (daysOverdue < 0) daysOverdue = 0;
        BigDecimal estimatedFine = dailyFineRate.multiply(BigDecimal.valueOf(daysOverdue));

        String memberName = record.getMember() != null
                ? record.getMember().getFirstName() + " " + record.getMember().getLastName()
                : null;

        return OverdueReportDto.builder()
                .borrowRecordId(record.getId())
                .bookId(record.getBook() != null ? record.getBook().getId() : null)
                .bookTitle(record.getBook() != null ? record.getBook().getTitle() : null)
                .bookIsbn(record.getBook() != null ? record.getBook().getIsbn() : null)
                .memberId(record.getMember() != null ? record.getMember().getId() : null)
                .memberName(memberName)
                .memberEmail(record.getMember() != null ? record.getMember().getEmail() : null)
                .memberPhone(record.getMember() != null ? record.getMember().getPhone() : null)
                .membershipNumber(record.getMember() != null ? record.getMember().getMembershipNumber() : null)
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .daysOverdue(daysOverdue)
                .estimatedFine(estimatedFine)
                .build();
    }
}
