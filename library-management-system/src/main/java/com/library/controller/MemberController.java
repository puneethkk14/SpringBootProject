package com.library.controller;

import com.library.dto.request.MemberRequest;
import com.library.dto.request.MemberStatusRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.MemberResponse;
import com.library.dto.response.PagedResponse;
import com.library.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member Management", description = "APIs for library membership registration, profiles, and status")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Register Member", description = "Registers a new library member.")
    public ResponseEntity<ApiResponse<MemberResponse>> registerMember(@Valid @RequestBody MemberRequest request) {
        log.info("REST request to register member: {}", request.getEmail());
        MemberResponse response = memberService.createMember(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Member registered successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Member by ID", description = "Retrieves member profile details by primary key.")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberById(@PathVariable Long id) {
        log.info("REST request to get member ID: {}", id);
        MemberResponse response = memberService.getMemberById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/number/{membershipNumber}")
    @Operation(summary = "Get Member by Membership Number", description = "Retrieves member by unique membership code (e.g., MEM-ABC12345).")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberByNumber(@PathVariable String membershipNumber) {
        log.info("REST request to get member by number: {}", membershipNumber);
        MemberResponse response = memberService.getMemberByMembershipNumber(membershipNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List Members", description = "Retrieves paginated list of all library members.")
    public ResponseEntity<ApiResponse<PagedResponse<MemberResponse>>> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.info("REST request to list members (page={}, size={})", page, size);
        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<MemberResponse> response = memberService.getAllMembers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Members", description = "Search members by first or last name.")
    public ResponseEntity<ApiResponse<PagedResponse<MemberResponse>>> searchMembers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to search members with query: {}", query);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastName").ascending());
        PagedResponse<MemberResponse> response = memberService.searchMembers(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Member", description = "Updates member contact and personal information.")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMember(
            @PathVariable Long id,
            @Valid @RequestBody MemberRequest request) {
        log.info("REST request to update member ID: {}", id);
        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Member updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change Member Status", description = "Updates membership status (ACTIVE, SUSPENDED, EXPIRED).")
    public ResponseEntity<ApiResponse<MemberResponse>> updateMemberStatus(
            @PathVariable Long id,
            @Valid @RequestBody MemberStatusRequest request) {
        log.info("REST request to update status of member ID: {} to {}", id, request.getStatus());
        MemberResponse response = memberService.updateMemberStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success(response, "Member status updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Member", description = "Deletes member record if they have no active borrowed books.")
    public ResponseEntity<ApiResponse<Void>> deleteMember(@PathVariable Long id) {
        log.info("REST request to delete member ID: {}", id);
        memberService.deleteMember(id);
        return ResponseEntity.ok(ApiResponse.success("Member deleted successfully"));
    }
}
