package com.library.mapper;

import com.library.dto.request.AuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import org.springframework.stereotype.Component;

@Component
public class AuthorMapper {

    public Author toEntity(AuthorRequest request) {
        if (request == null) return null;
        return Author.builder()
                .name(request.getName().trim())
                .biography(request.getBiography())
                .build();
    }

    public AuthorResponse toResponse(Author author) {
        if (author == null) return null;
        return AuthorResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .biography(author.getBiography())
                .createdAt(author.getCreatedAt())
                .updatedAt(author.getUpdatedAt())
                .build();
    }

    public void updateEntity(Author author, AuthorRequest request) {
        if (author == null || request == null) return;
        author.setName(request.getName().trim());
        author.setBiography(request.getBiography());
    }
}
