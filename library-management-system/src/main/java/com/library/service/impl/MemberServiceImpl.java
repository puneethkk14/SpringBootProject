package com.library.service.impl;

import com.library.dto.request.MemberRequest;
import com.library.dto.response.MemberResponse;
import com.library.dto.response.PagedResponse;
import com.library.entity.Member;
import com.library.entity.enums.MemberStatus;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.MemberMapper;
import com.library.repository.BorrowRecordRepository;
import com.library.repository.MemberRepository;
import com.library.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final MemberMapper memberMapper;

    @Override
    @Transactional
    public MemberResponse createMember(MemberRequest request) {
        log.info("Registering new library member with email: {}", request.getEmail());
        String normalizedEmail = request.getEmail().trim().toLowerCase();

        if (memberRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateResourceException("Member already registered with email: " + normalizedEmail);
        }

        Member member = memberMapper.toEntity(request);
        Member saved = memberRepository.save(member);
        log.info("Successfully registered member {} with ID: {}", saved.getMembershipNumber(), saved.getId());
        return memberMapper.toResponse(saved);
    }

    @Override
    public MemberResponse getMemberById(Long id) {
        log.debug("Fetching member by ID: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));
        return memberMapper.toResponse(member);
    }

    @Override
    public MemberResponse getMemberByMembershipNumber(String membershipNumber) {
        log.debug("Fetching member by membership number: {}", membershipNumber);
        Member member = memberRepository.findByMembershipNumber(membershipNumber.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with membership number: " + membershipNumber));
        return memberMapper.toResponse(member);
    }

    @Override
    public PagedResponse<MemberResponse> getAllMembers(Pageable pageable) {
        log.debug("Fetching all members with pagination");
        Page<Member> page = memberRepository.findAll(pageable);
        return PagedResponse.of(page.map(memberMapper::toResponse));
    }

    @Override
    public PagedResponse<MemberResponse> searchMembers(String query, Pageable pageable) {
        log.debug("Searching members with query: {}", query);
        Page<Member> page = memberRepository.findByLastNameContainingIgnoreCaseOrFirstNameContainingIgnoreCase(
                query, query, pageable);
        return PagedResponse.of(page.map(memberMapper::toResponse));
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long id, MemberRequest request) {
        log.info("Updating member profile for ID: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        String normalizedEmail = request.getEmail().trim().toLowerCase();
        if (memberRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new DuplicateResourceException("Another member is already using email: " + normalizedEmail);
        }

        memberMapper.updateEntity(member, request);
        Member updated = memberRepository.save(member);
        log.info("Successfully updated member ID: {}", id);
        return memberMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public MemberResponse updateMemberStatus(Long id, MemberStatus status) {
        log.info("Updating status for member ID: {} to {}", id, status);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        member.setStatus(status);
        Member updated = memberRepository.save(member);
        log.info("Updated status for member ID: {} to {}", id, status);
        return memberMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteMember(Long id) {
        log.info("Attempting to delete member ID: {}", id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + id));

        long activeBorrows = borrowRecordRepository.countActiveBorrowsByMemberId(id);
        if (activeBorrows > 0) {
            throw new IllegalStateException("Cannot delete member with " + activeBorrows + " active borrowed book(s). All books must be returned first.");
        }

        memberRepository.delete(member);
        log.info("Successfully deleted member ID: {}", id);
    }
}
