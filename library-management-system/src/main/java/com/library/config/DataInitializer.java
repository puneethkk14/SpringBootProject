package com.library.config;

import com.library.entity.*;
import com.library.entity.enums.BorrowStatus;
import com.library.entity.enums.MemberStatus;
import com.library.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;
    private final BorrowRecordRepository borrowRecordRepository;
    private final FineRepository fineRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Database already contains data. Skipping initial seeding.");
            return;
        }

        log.info("Seeding initial library catalog and sample records...");

        // 1. Categories
        Category cs = categoryRepository.save(Category.builder()
                .name("Computer Science")
                .description("Software engineering, architecture, algorithms, and clean coding.")
                .build());

        Category fiction = categoryRepository.save(Category.builder()
                .name("Classic Literature & Fiction")
                .description("Dystopian, drama, philosophical and classic novels.")
                .build());

        Category science = categoryRepository.save(Category.builder()
                .name("Science & Mathematics")
                .description("Physics, statistics, data science and natural sciences.")
                .build());

        // 2. Authors
        Author uncleBob = authorRepository.save(Author.builder()
                .name("Robert C. Martin")
                .biography("Known colloquially as Uncle Bob, software engineer and author of Agile Software Development.")
                .build());

        Author bloch = authorRepository.save(Author.builder()
                .name("Joshua Bloch")
                .biography("Software engineer and author who led the design of numerous Java platform features.")
                .build());

        Author fowler = authorRepository.save(Author.builder()
                .name("Martin Fowler")
                .biography("Software engineer, speaker, and prominent author on object-oriented programming and microservices.")
                .build());

        Author orwell = authorRepository.save(Author.builder()
                .name("George Orwell")
                .biography("English novelist, essayist, journalist, and critic known for 1984 and Animal Farm.")
                .build());

        // 3. Books
        Book cleanCode = bookRepository.save(Book.builder()
                .isbn("9780132350884")
                .title("Clean Code: A Handbook of Agile Software Craftsmanship")
                .publisher("Prentice Hall")
                .publicationYear(2008)
                .totalCopies(5)
                .availableCopies(4) // 1 copy issued below
                .category(cs)
                .authors(new HashSet<>(Set.of(uncleBob)))
                .build());

        Book effectiveJava = bookRepository.save(Book.builder()
                .isbn("9780134685991")
                .title("Effective Java (3rd Edition)")
                .publisher("Addison-Wesley")
                .publicationYear(2018)
                .totalCopies(4)
                .availableCopies(4)
                .category(cs)
                .authors(new HashSet<>(Set.of(bloch)))
                .build());

        Book refactoring = bookRepository.save(Book.builder()
                .isbn("9780201485677")
                .title("Refactoring: Improving the Design of Existing Code")
                .publisher("Addison-Wesley")
                .publicationYear(1999)
                .totalCopies(3)
                .availableCopies(2) // 1 copy overdue below
                .category(cs)
                .authors(new HashSet<>(Set.of(fowler, uncleBob)))
                .build());

        Book book1984 = bookRepository.save(Book.builder()
                .isbn("9780451524935")
                .title("1984")
                .publisher("Signet Classic")
                .publicationYear(1949)
                .totalCopies(6)
                .availableCopies(6)
                .category(fiction)
                .authors(new HashSet<>(Set.of(orwell)))
                .build());

        // 4. Members
        Member member1 = memberRepository.save(Member.builder()
                .membershipNumber("MEM-CS202601")
                .firstName("Alex")
                .lastName("Morgan")
                .email("alex.morgan@university.edu")
                .phone("+15552345678")
                .membershipDate(LocalDate.now().minusMonths(6))
                .status(MemberStatus.ACTIVE)
                .build());

        Member member2 = memberRepository.save(Member.builder()
                .membershipNumber("MEM-CS202602")
                .firstName("Sarah")
                .lastName("Jenkins")
                .email("sarah.jenkins@university.edu")
                .phone("+15558765432")
                .membershipDate(LocalDate.now().minusMonths(3))
                .status(MemberStatus.ACTIVE)
                .build());

        Member member3 = memberRepository.save(Member.builder()
                .membershipNumber("MEM-CS202603")
                .firstName("David")
                .lastName("Chen")
                .email("david.chen@university.edu")
                .phone("+15553456789")
                .membershipDate(LocalDate.now().minusMonths(1))
                .status(MemberStatus.SUSPENDED)
                .build());

        // 5. Borrow Records (1 active regular borrow, 1 overdue borrow for demo)
        BorrowRecord activeBorrow = borrowRecordRepository.save(BorrowRecord.builder()
                .book(cleanCode)
                .member(member1)
                .borrowDate(LocalDate.now().minusDays(5))
                .dueDate(LocalDate.now().plusDays(9))
                .status(BorrowStatus.ISSUED)
                .remarks("Standard loan for semester research")
                .build());

        BorrowRecord overdueBorrow = borrowRecordRepository.save(BorrowRecord.builder()
                .book(refactoring)
                .member(member2)
                .borrowDate(LocalDate.now().minusDays(20))
                .dueDate(LocalDate.now().minusDays(6)) // 6 days overdue
                .status(BorrowStatus.OVERDUE)
                .remarks("Overdue borrow record for testing reporting and fine calculation")
                .build());

        log.info("Successfully seeded mock data: {} categories, {} authors, {} books, {} members, {} borrow records.",
                categoryRepository.count(), authorRepository.count(), bookRepository.count(),
                memberRepository.count(), borrowRecordRepository.count());
    }
}
