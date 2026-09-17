package com.library.service;

import com.library.dto.request.IssueBookRequest;
import com.library.dto.request.ReturnBookRequest;
import com.library.dto.response.BorrowRecordResponse;

import java.util.List;

public interface BorrowService {

    BorrowRecordResponse issueBook(IssueBookRequest request);

    BorrowRecordResponse returnBook(Long borrowRecordId, ReturnBookRequest request);

    BorrowRecordResponse getBorrowRecordById(Long id);

    List<BorrowRecordResponse> getMemberBorrowHistory(Long memberId);

    List<BorrowRecordResponse> getBookBorrowHistory(Long bookId);
}
