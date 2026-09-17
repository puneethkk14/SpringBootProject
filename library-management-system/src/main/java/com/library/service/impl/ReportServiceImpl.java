package com.library.service.impl;

import com.library.dto.response.OverdueReportDto;
import com.library.entity.BorrowRecord;
import com.library.mapper.BorrowRecordMapper;
import com.library.repository.BorrowRecordRepository;
import com.library.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowRecordMapper borrowRecordMapper;

    @Value("${library.borrow.daily-fine-rate:1.00}")
    private BigDecimal dailyFineRate;

    @Override
    public List<OverdueReportDto> getOverdueBooksReport() {
        LocalDate today = LocalDate.now();
        log.info("Generating overdue books report as of {}", today);

        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueRecords(today);
        log.info("Found {} overdue record(s)", overdueRecords.size());

        return overdueRecords.stream()
                .map(record -> borrowRecordMapper.toOverdueReportDto(record, dailyFineRate))
                .collect(Collectors.toList());
    }
}
