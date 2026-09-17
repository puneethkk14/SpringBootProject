package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIdNot(String isbn, Long id);

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT DISTINCT b FROM Book b LEFT JOIN b.authors a WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :author, '%'))) AND " +
           "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
           "(:availableOnly IS NULL OR :availableOnly = false OR b.availableCopies > 0)")
    Page<Book> searchBooks(
            @Param("title") String title,
            @Param("author") String author,
            @Param("categoryId") Long categoryId,
            @Param("availableOnly") Boolean availableOnly,
            Pageable pageable);
}
