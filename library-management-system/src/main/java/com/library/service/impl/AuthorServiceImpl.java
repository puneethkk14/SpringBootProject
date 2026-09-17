package com.library.service.impl;

import com.library.dto.request.AuthorRequest;
import com.library.dto.response.AuthorResponse;
import com.library.entity.Author;
import com.library.exception.ResourceNotFoundException;
import com.library.mapper.AuthorMapper;
import com.library.repository.AuthorRepository;
import com.library.service.AuthorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

    @Override
    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        log.info("Creating author with name: {}", request.getName());
        Author author = authorMapper.toEntity(request);
        Author saved = authorRepository.save(author);
        log.info("Successfully created author with ID: {}", saved.getId());
        return authorMapper.toResponse(saved);
    }

    @Override
    public AuthorResponse getAuthorById(Long id) {
        log.debug("Fetching author by ID: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + id));
        return authorMapper.toResponse(author);
    }

    @Override
    public List<AuthorResponse> getAllAuthors() {
        log.debug("Fetching all authors");
        return authorRepository.findAll().stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AuthorResponse> searchAuthorsByName(String name) {
        log.debug("Searching authors containing name: {}", name);
        return authorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(authorMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuthorResponse updateAuthor(Long id, AuthorRequest request) {
        log.info("Updating author with ID: {}", id);
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with ID: " + id));

        authorMapper.updateEntity(author, request);
        Author updated = authorRepository.save(author);
        log.info("Successfully updated author with ID: {}", id);
        return authorMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteAuthor(Long id) {
        log.info("Deleting author with ID: {}", id);
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author not found with ID: " + id);
        }
        authorRepository.deleteById(id);
        log.info("Successfully deleted author with ID: {}", id);
    }
}
