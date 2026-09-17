package com.library.service.impl;

import com.library.dto.request.BookPatchRequest;
import com.library.dto.request.BookRequest;
import com.library.dto.response.BookResponse;
import com.library.dto.response.PagedResponse;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.entity.Category;
import com.library.exception.DuplicateResourceException;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.BookMapper;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import com.library.repository.CategoryRepository;
import com.library.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request) {
        log.info("Registering new book with ISBN: {}", request.getIsbn());
        String normalizedIsbn = request.getIsbn().trim().toUpperCase();

        if (bookRepository.existsByIsbn(normalizedIsbn)) {
            throw new DuplicateResourceException("Book already exists with ISBN: " + normalizedIsbn);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        List<Author> authorsList = authorRepository.findAllByIdIn(request.getAuthorIds());
        if (authorsList.isEmpty()) {
            throw new ResourceNotFoundException("No valid authors found with provided IDs: " + request.getAuthorIds());
        }
        Set<Author> authors = new HashSet<>(authorsList);

        Book book = bookMapper.toEntity(request, category, authors);
        Book saved = bookRepository.save(book);
        log.info("Successfully created book '{}' with ID: {}", saved.getTitle(), saved.getId());
        return bookMapper.toResponse(saved);
    }

    @Override
    public BookResponse getBookById(Long id) {
        log.debug("Fetching book by ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));
        return bookMapper.toResponse(book);
    }

    @Override
    public BookResponse getBookByIsbn(String isbn) {
        log.debug("Fetching book by ISBN: {}", isbn);
        Book book = bookRepository.findByIsbn(isbn.trim().toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ISBN: " + isbn));
        return bookMapper.toResponse(book);
    }

    @Override
    public PagedResponse<BookResponse> searchBooks(
            String title,
            String author,
            Long categoryId,
            Boolean availableOnly,
            Pageable pageable) {
        log.debug("Searching books: title={}, author={}, categoryId={}, availableOnly={}",
                title, author, categoryId, availableOnly);

        Page<Book> bookPage = bookRepository.searchBooks(title, author, categoryId, availableOnly, pageable);
        Page<BookResponse> responsePage = bookPage.map(bookMapper::toResponse);
        return PagedResponse.of(responsePage);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest request) {
        log.info("Updating book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        String normalizedIsbn = request.getIsbn().trim().toUpperCase();
        if (bookRepository.existsByIsbnAndIdNot(normalizedIsbn, id)) {
            throw new DuplicateResourceException("Another book already exists with ISBN: " + normalizedIsbn);
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        List<Author> authorsList = authorRepository.findAllByIdIn(request.getAuthorIds());
        if (authorsList.isEmpty()) {
            throw new ResourceNotFoundException("No valid authors found with provided IDs: " + request.getAuthorIds());
        }
        Set<Author> authors = new HashSet<>(authorsList);

        bookMapper.updateEntity(book, request, category, authors);
        Book updated = bookRepository.save(book);
        log.info("Successfully updated book with ID: {}", id);
        return bookMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public BookResponse patchBook(Long id, BookPatchRequest request) {
        log.info("Patching book with ID: {}", id);
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with ID: " + id));

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
        }

        Set<Author> authors = null;
        if (request.getAuthorIds() != null && !request.getAuthorIds().isEmpty()) {
            List<Author> authorsList = authorRepository.findAllByIdIn(request.getAuthorIds());
            authors = new HashSet<>(authorsList);
        }

        bookMapper.applyPatch(book, request, category, authors);
        Book saved = bookRepository.save(book);
        log.info("Successfully patched book with ID: {}", id);
        return bookMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.info("Deleting book with ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with ID: " + id);
        }
        bookRepository.deleteById(id);
        log.info("Successfully deleted book with ID: {}", id);
    }
}
