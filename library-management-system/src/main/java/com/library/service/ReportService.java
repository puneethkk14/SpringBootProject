package com.library.service;

import com.library.dto.response.OverdueReportDto;

import java.util.List;

public interface ReportService {

    List<OverdueReportDto> getOverdueBooksReport();
}
