package com.library.service.impl;

import com.library.dto.response.FineResponse;
import com.library.entity.Fine;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.FineMapper;
import com.library.repository.FineRepository;
import com.library.repository.MemberRepository;
import com.library.service.FineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final MemberRepository memberRepository;
    private final FineMapper fineMapper;

    @Override
    @Transactional
    public FineResponse payFine(Long fineId, String paymentReference) {
        log.info("Processing fine payment for ID: {}", fineId);
        Fine fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found with ID: " + fineId));

        if (Boolean.TRUE.equals(fine.getPaid())) {
            throw new IllegalStateException("Fine with ID " + fineId + " has already been paid.");
        }

        fine.setPaid(true);
        fine.setPaidDate(LocalDate.now());
        fine.setPaymentReference(
                (paymentReference != null && !paymentReference.isBlank())
                        ? paymentReference
                        : "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()
        );

        Fine updated = fineRepository.save(fine);
        log.info("Successfully settled fine ID: {} with reference: {}", fineId, updated.getPaymentReference());
        return fineMapper.toResponse(updated);
    }

    @Override
    public List<FineResponse> getAllPendingFines() {
        log.debug("Fetching all unpaid fines");
        return fineRepository.findByPaidFalse().stream()
                .map(fineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FineResponse> getPendingFinesByMember(Long memberId) {
        log.debug("Fetching pending fines for member ID: {}", memberId);
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId);
        }
        return fineRepository.findPendingFinesByMemberId(memberId).stream()
                .map(fineMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalPendingFineByMember(Long memberId) {
        log.debug("Calculating total pending fine for member ID: {}", memberId);
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId);
        }
        return fineRepository.getTotalPendingFineByMemberId(memberId);
    }

    @Override
    public FineResponse getFineById(Long id) {
        log.debug("Fetching fine by ID: {}", id);
        Fine fine = fineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Fine not found with ID: " + id));
        return fineMapper.toResponse(fine);
    }
}
