package com.library.service;

import com.library.dto.request.MemberRequest;
import com.library.dto.response.MemberResponse;
import com.library.dto.response.PagedResponse;
import com.library.entity.enums.MemberStatus;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    MemberResponse createMember(MemberRequest request);

    MemberResponse getMemberById(Long id);

    MemberResponse getMemberByMembershipNumber(String membershipNumber);

    PagedResponse<MemberResponse> getAllMembers(Pageable pageable);

    PagedResponse<MemberResponse> searchMembers(String query, Pageable pageable);

    MemberResponse updateMember(Long id, MemberRequest request);

    MemberResponse updateMemberStatus(Long id, MemberStatus status);

    void deleteMember(Long id);
}
