package com.library.service;

import com.library.dto.response.FineResponse;

import java.math.BigDecimal;
import java.util.List;

public interface FineService {

    FineResponse payFine(Long fineId, String paymentReference);

    List<FineResponse> getAllPendingFines();

    List<FineResponse> getPendingFinesByMember(Long memberId);

    BigDecimal getTotalPendingFineByMember(Long memberId);

    FineResponse getFineById(Long id);
}
