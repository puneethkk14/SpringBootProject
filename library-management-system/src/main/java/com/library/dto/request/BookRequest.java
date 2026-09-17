package com.library.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequest {

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "Invalid ISBN format (must be 10 or 13 digits)")
    private String isbn;

    @NotBlank(message = "Book title is required")
    @Size(max = 200, message = "Book title cannot exceed 200 characters")
    private String title;

    @Size(max = 150, message = "Publisher cannot exceed 150 characters")
    private String publisher;

    private Integer publicationYear;

    @NotNull(message = "Total copies must be specified")
    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotEmpty(message = "At least one author ID must be provided")
    private Set<Long> authorIds;
}
