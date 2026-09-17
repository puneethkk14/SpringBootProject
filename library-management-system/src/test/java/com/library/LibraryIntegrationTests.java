package com.library.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dto.request.IssueBookRequest;
import com.library.dto.request.MemberRequest;
import com.library.entity.Book;
import com.library.entity.Member;
import com.library.repository.BookRepository;
import com.library.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
class LibraryIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("GET /api/v1/books - Should return seeded books with pagination")
    void shouldReturnBooksWithPagination() throws Exception {
        mockMvc.perform(get("/api/v1/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.totalElements", greaterThanOrEqualTo(1)));
    }

    @Test
    @DisplayName("GET /api/v1/books - Should filter books by title keyword")
    void shouldFilterBooksByTitle() throws Exception {
        mockMvc.perform(get("/api/v1/books")
                        .param("title", "Clean Code")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].title", containsString("Clean Code")));
    }

    @Test
    @DisplayName("POST /api/v1/borrow/issue - Should issue book and decrease available copies")
    void shouldIssueBookSuccessfully() throws Exception {
        Book book = bookRepository.findAll().stream()
                .filter(b -> b.getAvailableCopies() > 0)
                .findFirst()
                .orElseThrow();

        Member member = memberRepository.findAll().stream()
                .filter(m -> m.getStatus().name().equals("ACTIVE"))
                .findFirst()
                .orElseThrow();

        int initialCopies = book.getAvailableCopies();

        IssueBookRequest request = IssueBookRequest.builder()
                .bookId(book.getId())
                .memberId(member.getId())
                .borrowDays(7)
                .remarks("Test borrow")
                .build();

        mockMvc.perform(post("/api/v1/borrow/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bookId").value(book.getId()))
                .andExpect(jsonPath("$.data.status").value("ISSUED"));

        Book updatedBook = bookRepository.findById(book.getId()).orElseThrow();
        org.junit.jupiter.api.Assertions.assertEquals(initialCopies - 1, updatedBook.getAvailableCopies());
    }

    @Test
    @DisplayName("POST /api/v1/members - Should reject invalid email and return 400 with validation errors")
    void shouldRejectInvalidMemberRegistration() throws Exception {
        MemberRequest invalidRequest = MemberRequest.builder()
                .firstName("")
                .lastName("Doe")
                .email("not-a-valid-email")
                .phone("123")
                .build();

        mockMvc.perform(post("/api/v1/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.firstName").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists());
    }

    @Test
    @DisplayName("GET /api/v1/reports/overdue - Should generate overdue report")
    void shouldGenerateOverdueReport() throws Exception {
        mockMvc.perform(get("/api/v1/reports/overdue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", isA(java.util.List.class)));
    }
}
