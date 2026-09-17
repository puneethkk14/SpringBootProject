package com.library.dto.response;

import com.library.entity.enums.MemberStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponse {

    private Long id;
    private String membershipNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate membershipDate;
    private MemberStatus status;
    private int activeBorrowsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
