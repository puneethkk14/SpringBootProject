package com.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookPatchRequest {

    @Size(max = 200, message = "Book title cannot exceed 200 characters")
    private String title;

    @Size(max = 150, message = "Publisher cannot exceed 150 characters")
    private String publisher;

    private Integer publicationYear;

    @Min(value = 1, message = "Total copies must be at least 1")
    private Integer totalCopies;

    private Long categoryId;

    private Set<Long> authorIds;
}
