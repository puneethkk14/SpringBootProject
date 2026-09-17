package com.library.service;

import com.library.dto.request.BookPatchRequest;
import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponse createBook(BookRequest request);

    BookResponse getBookById(Long id);

    BookResponse getBookByIsbn(String isbn);

    PagedResponse<BookResponse> searchBooks(
            String title,
            String author,
            Long categoryId,
            Boolean availableOnly,
            Pageable pageable);

    BookResponse updateBook(Long id, BookRequest request);

    BookResponse patchBook(Long id, BookPatchRequest request);

    void deleteBook(Long id);
}
