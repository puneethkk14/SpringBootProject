package com.library.controller;

import com.library.dto.request.AuthorRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.AuthorResponse;
import com.library.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Tag(name = "Author Management", description = "APIs for managing authors")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @Operation(summary = "Create Author", description = "Registers a new author.")
    public ResponseEntity<ApiResponse<AuthorResponse>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        log.info("REST request to create author: {}", request.getName());
        AuthorResponse response = authorService.createAuthor(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Author registered successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Author by ID", description = "Retrieves author details by unique ID.")
    public ResponseEntity<ApiResponse<AuthorResponse>> getAuthorById(@PathVariable Long id) {
        log.info("REST request to fetch author ID: {}", id);
        AuthorResponse response = authorService.getAuthorById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "List all Authors", description = "Retrieves all registered authors.")
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> getAllAuthors() {
        log.info("REST request to list all authors");
        List<AuthorResponse> response = authorService.getAllAuthors();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Authors", description = "Searches authors by name keyword.")
    public ResponseEntity<ApiResponse<List<AuthorResponse>>> searchAuthors(@RequestParam String name) {
        log.info("REST request to search authors with name: {}", name);
        List<AuthorResponse> response = authorService.searchAuthorsByName(name);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Author", description = "Updates an existing author profile.")
    public ResponseEntity<ApiResponse<AuthorResponse>> updateAuthor(
            @PathVariable Long id,
            @Valid @RequestBody AuthorRequest request) {
        log.info("REST request to update author ID: {}", id);
        AuthorResponse response = authorService.updateAuthor(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Author updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Author", description = "Deletes an author from the system.")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable Long id) {
        log.info("REST request to delete author ID: {}", id);
        authorService.deleteAuthor(id);
        return ResponseEntity.ok(ApiResponse.success("Author deleted successfully"));
    }
}
