package com.library.controller;

import com.library.dto.request.BookPatchRequest;
import com.library.dto.request.BookRequest;
import com.library.dto.response.ApiResponse;
import com.library.dto.response.BookResponse;
import com.library.dto.response.PagedResponse;
import com.library.service.BookService;
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
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "APIs for cataloging, searching, and managing books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Add New Book", description = "Catalogs a new book in the library with author and category relationships.")
    public ResponseEntity<ApiResponse<BookResponse>> createBook(@Valid @RequestBody BookRequest request) {
        log.info("REST request to add book with ISBN: {}", request.getIsbn());
        BookResponse response = bookService.createBook(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Book cataloged successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Book by ID", description = "Retrieves complete book details by primary key.")
    public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
        log.info("REST request to get book ID: {}", id);
        BookResponse response = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/isbn/{isbn}")
    @Operation(summary = "Get Book by ISBN", description = "Retrieves book details by unique ISBN code.")
    public ResponseEntity<ApiResponse<BookResponse>> getBookByIsbn(@PathVariable String isbn) {
        log.info("REST request to get book by ISBN: {}", isbn);
        BookResponse response = bookService.getBookByIsbn(isbn);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @Operation(summary = "Search and Filter Books", description = "Search catalog by title, author name, category, and availability with pagination.")
    public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "false") Boolean availableOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir) {

        log.info("REST request to search books: title={}, author={}, categoryId={}, availableOnly={}",
                title, author, categoryId, availableOnly);

        Sort sort = sortDir.equalsIgnoreCase("DESC") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedResponse<BookResponse> response = bookService.searchBooks(title, author, categoryId, availableOnly, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Book", description = "Performs a complete update of a book record.")
    public ResponseEntity<ApiResponse<BookResponse>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request) {
        log.info("REST request to update book ID: {}", id);
        BookResponse response = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Book updated successfully"));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partial Update Book", description = "Updates specific fields of a book without overwriting all fields.")
    public ResponseEntity<ApiResponse<BookResponse>> patchBook(
            @PathVariable Long id,
            @Valid @RequestBody BookPatchRequest request) {
        log.info("REST request to patch book ID: {}", id);
        BookResponse response = bookService.patchBook(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Book partially updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Book", description = "Removes a book from the library catalog.")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        log.info("REST request to delete book ID: {}", id);
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
    }
}
