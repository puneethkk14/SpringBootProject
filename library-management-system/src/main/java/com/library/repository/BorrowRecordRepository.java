package com.library.repository;

import com.library.entity.BorrowRecord;
import com.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("SELECT COUNT(br) FROM BorrowRecord br WHERE br.member.id = :memberId AND br.status IN ('ISSUED', 'OVERDUE')")
    long countActiveBorrowsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT br FROM BorrowRecord br WHERE br.status IN ('ISSUED', 'OVERDUE') AND br.dueDate < :currentDate")
    List<BorrowRecord> findOverdueRecords(@Param("currentDate") LocalDate currentDate);

    List<BorrowRecord> findByMemberIdOrderByBorrowDateDesc(Long memberId);

    List<BorrowRecord> findByBookIdOrderByBorrowDateDesc(Long bookId);

    List<BorrowRecord> findByStatus(BorrowStatus status);
}
