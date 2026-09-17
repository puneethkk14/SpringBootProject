package com.library.mapper;

import com.library.dto.request.BookPatchRequest;
import com.library.dto.request.BookRequest;
import com.library.dto.response.AuthorResponse;
import com.library.dto.response.BookResponse;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookMapper {

    private final CategoryMapper categoryMapper;
    private final AuthorMapper authorMapper;

    public Book toEntity(BookRequest request, Category category, Set<Author> authors) {
        if (request == null) return null;
        return Book.builder()
                .isbn(request.getIsbn().trim().toUpperCase())
                .title(request.getTitle().trim())
                .publisher(request.getPublisher())
                .publicationYear(request.getPublicationYear())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .category(category)
                .authors(authors)
                .build();
    }

    public BookResponse toResponse(Book book) {
        if (book == null) return null;

        List<AuthorResponse> authorResponses = book.getAuthors() != null
                ? book.getAuthors().stream()
                .map(authorMapper::toResponse)
                .sorted(Comparator.comparing(AuthorResponse::getName))
                .collect(Collectors.toList())
                : List.of();

        return BookResponse.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .publisher(book.getPublisher())
                .publicationYear(book.getPublicationYear())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .category(categoryMapper.toResponse(book.getCategory()))
                .authors(authorResponses)
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    public void updateEntity(Book book, BookRequest request, Category category, Set<Author> authors) {
        if (book == null || request == null) return;
        book.setIsbn(request.getIsbn().trim().toUpperCase());
        book.setTitle(request.getTitle().trim());
        book.setPublisher(request.getPublisher());
        book.setPublicationYear(request.getPublicationYear());

        // Adjust available copies based on total copies change
        int copyDelta = request.getTotalCopies() - book.getTotalCopies();
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + copyDelta));

        book.setCategory(category);
        book.setAuthors(authors);
    }

    public void applyPatch(Book book, BookPatchRequest patch, Category category, Set<Author> authors) {
        if (book == null || patch == null) return;

        if (patch.getTitle() != null && !patch.getTitle().isBlank()) {
            book.setTitle(patch.getTitle().trim());
        }
        if (patch.getPublisher() != null) {
            book.setPublisher(patch.getPublisher().trim());
        }
        if (patch.getPublicationYear() != null) {
            book.setPublicationYear(patch.getPublicationYear());
        }
        if (patch.getTotalCopies() != null) {
            int copyDelta = patch.getTotalCopies() - book.getTotalCopies();
            book.setTotalCopies(patch.getTotalCopies());
            book.setAvailableCopies(Math.max(0, book.getAvailableCopies() + copyDelta));
        }
        if (category != null) {
            book.setCategory(category);
        }
        if (authors != null && !authors.isEmpty()) {
            book.setAuthors(authors);
        }
    }
}
