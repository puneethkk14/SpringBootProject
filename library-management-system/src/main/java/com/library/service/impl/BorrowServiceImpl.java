package com.library.service.impl;

import com.library.dto.request.IssueBookRequest;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.BorrowRecordResponse;
import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.Fine;
import com.library.entity.Member;
import com.library.entity.enums.BorrowStatus;
import com.library.entity.enums.MemberStatus;
import com.library.exception.*;
import com.library.mapper.BorrowRecordMapper;
import com.library.repository.BookRepository;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.FineRepository;
import com.library.repository.MemberRepository;
import com.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BorrowServiceImpl implements BorrowService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final FineRepository fineRepository;
    private final BorrowRecordMapper borrowRecordMapper;

    @Value("${library.borrow.default-period-days:14}")
    private int defaultPeriodDays;

    @Value("${library.borrow.daily-fine-rate:1.00}")
    private BigDecimal dailyFineRate;

    @Value("${library.borrow.max-active-limit:3}")
    private int maxActiveLimit;

    @Override
    @Transactional
    public BorrowRecordResponse issueBook(IssueBookRequest request) {
        log.info("Processing book issue: memberId={}, bookId={}", request.getMemberId(), request.getBookId());

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + request.getMemberId()));

        if (member.getStatus() != MemberStatus.ACTIVE) {
            throw new MemberNotActiveException(
                    "Cannot issue book. Member is currently " + member.getStatus() + ". Only ACTIVE members can borrow books.");
        }

        long activeBorrows = borrowRecordRepository.countActiveBorrowsByMemberId(member.getId());
        if (activeBorrows >= maxActiveLimit) {
            throw new BorrowLimitExceededException(
                    "Member has reached the maximum allowed limit of " + maxActiveLimit + " actively borrowed book(s).");
        }

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + request.getBookId()));

        if (book.getAvailableCopies() <= 0) {
            throw new BookUnavailableException(
                    "No available copies for book '" + book.getTitle() + "' (ISBN: " + book.getIsbn() + "). All copies are currently issued.");
        }

        // Decrement available copies atomically
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LocalDate borrowDate = LocalDate.now();
        int loanDays = (request.getBorrowDays() != null && request.getBorrowDays() > 0)
                ? request.getBorrowDays()
                : defaultPeriodDays;
        LocalDate dueDate = borrowDate.plusDays(loanDays);

        BorrowRecord record = BorrowRecord.builder()
                .book(book)
                .member(member)
                .borrowDate(borrowDate)
                .dueDate(dueDate)
                .status(BorrowStatus.ISSUED)
                .remarks(request.getRemarks())
                .build();

        BorrowRecord saved = borrowRecordRepository.save(record);
        log.info("Issued book '{}' to member '{}' (Due date: {}). Record ID: {}",
                book.getTitle(), member.getMembershipNumber(), dueDate, saved.getId());

        return borrowRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BorrowRecordResponse returnBook(Long borrowRecordId, ReturnBookRequest request) {
        log.info("Processing book return for borrow record ID: {}", borrowRecordId);

        BorrowRecord record = borrowRecordRepository.findById(borrowRecordId)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with ID: " + borrowRecordId));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new BookAlreadyReturnedException("Book from record " + borrowRecordId + " has already been returned.");
        }

        LocalDate returnDate = LocalDate.now();
        record.setReturnDate(returnDate);
        record.setStatus(BorrowStatus.RETURNED);

        if (request != null && request.getRemarks() != null) {
            record.setRemarks(request.getRemarks());
        }

        // Return copy back to available inventory
        Book book = record.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        // Check for overdue fines using Date/Time API
        if (returnDate.isAfter(record.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), returnDate);
            BigDecimal fineAmount = dailyFineRate.multiply(BigDecimal.valueOf(overdueDays));

            log.warn("Book returned {} days overdue. Calculated fine: ${}", overdueDays, fineAmount);

            Fine fine = Fine.builder()
                    .borrowRecord(record)
                    .amount(fineAmount)
                    .overdueDays(overdueDays)
                    .paid(false)
                    .remarks("Overdue by " + overdueDays + " day(s). Fine rate: $" + dailyFineRate + "/day")
                    .build();

            record.setFine(fine);
            fineRepository.save(fine);
        }

        BorrowRecord updated = borrowRecordRepository.save(record);
        log.info("Successfully returned book '{}' for record ID: {}", book.getTitle(), borrowRecordId);
        return borrowRecordMapper.toResponse(updated);
    }

    @Override
    public BorrowRecordResponse getBorrowRecordById(Long id) {
        log.debug("Fetching borrow record by ID: {}", id);
        BorrowRecord record = borrowRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Borrow record not found with ID: " + id));
        return borrowRecordMapper.toResponse(record);
    }

    @Override
    public List<BorrowRecordResponse> getMemberBorrowHistory(Long memberId) {
        log.debug("Fetching borrow history for member ID: {}", memberId);
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId);
        }
        return borrowRecordRepository.findByMemberIdOrderByBorrowDateDesc(memberId).stream()
                .map(borrowRecordMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BorrowRecordResponse> getBookBorrowHistory(Long bookId) {
        log.debug("Fetching borrow history for book ID: {}", bookId);
        if (!bookRepository.existsById(bookId)) {
            throw new ResourceNotFoundException("Book not found with ID: " + bookId);
        }
        return borrowRecordRepository.findByBookIdOrderByBorrowDateDesc(bookId).stream()
                .map(borrowRecordMapper::toResponse)
                .collect(Collectors.toList());
    }
}
