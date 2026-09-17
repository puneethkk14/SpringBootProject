package com.library.repository;

import com.library.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    List<Fine> findByPaidFalse();

    @Query("SELECT f FROM Fine f WHERE f.borrowRecord.member.id = :memberId AND f.paid = false")
    List<Fine> findPendingFinesByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM Fine f WHERE f.borrowRecord.member.id = :memberId AND f.paid = false")
    BigDecimal getTotalPendingFineByMemberId(@Param("memberId") Long memberId);
}
