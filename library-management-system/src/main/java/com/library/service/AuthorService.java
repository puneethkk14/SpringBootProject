package com.library.service;

import com.library.dto.request.AuthorRequest;
import com.library.dto.response.AuthorResponse;

import java.util.List;

public interface AuthorService {

    AuthorResponse createAuthor(AuthorRequest request);

    AuthorResponse getAuthorById(Long id);

    List<AuthorResponse> getAllAuthors();

    List<AuthorResponse> searchAuthorsByName(String name);

    AuthorResponse updateAuthor(Long id, AuthorRequest request);

    void deleteAuthor(Long id);
}
