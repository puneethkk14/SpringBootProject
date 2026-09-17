package com.library.mapper;

import com.library.dto.request.MemberRequest;
import com.library.dto.response.MemberResponse;
import com.library.entity.Member;
import com.library.entity.enums.BorrowStatus;
import com.library.entity.enums.MemberStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@Component
public class MemberMapper {

    public Member toEntity(MemberRequest request) {
        if (request == null) return null;
        return Member.builder()
                .membershipNumber("MEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .firstName(request.getFirstName().trim())
                .lastName(request.getLastName().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .membershipDate(LocalDate.now())
                .status(MemberStatus.ACTIVE)
                .build();
    }

    public MemberResponse toResponse(Member member) {
        if (member == null) return null;

        int activeBorrows = 0;
        if (member.getBorrowRecords() != null) {
            activeBorrows = (int) member.getBorrowRecords().stream()
                    .filter(r -> r.getStatus() == BorrowStatus.ISSUED || r.getStatus() == BorrowStatus.OVERDUE)
                    .count();
        }

        return MemberResponse.builder()
                .id(member.getId())
                .membershipNumber(member.getMembershipNumber())
                .firstName(member.getFirstName())
                .lastName(member.getLastName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .membershipDate(member.getMembershipDate())
                .status(member.getStatus())
                .activeBorrowsCount(activeBorrows)
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    public void updateEntity(Member member, MemberRequest request) {
        if (member == null || request == null) return;
        member.setFirstName(request.getFirstName().trim());
        member.setLastName(request.getLastName().trim());
        member.setEmail(request.getEmail().trim().toLowerCase());
        member.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
    }
}
