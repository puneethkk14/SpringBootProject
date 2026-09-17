# Library Management System

A Spring Boot REST API for managing books, members, borrowing circulation, and overdue fines.

---

## Features

- Book catalog management (CRUD, search, and filter)
- Author and category management
- Member registration and status management (Active, Suspended, Expired)
- Book issue and return handling
- Real-time inventory tracking (automatic copy count updates)
- Automatic overdue detection and fine calculation
- Overdue reports and fine payment settlement
- Swagger UI interactive API documentation

---

## Concepts Used

- **Spring Boot & Spring Web**: RESTful APIs, request mapping, and HTTP response handling
- **Spring Data JPA & Hibernate**: CRUD repositories, derived queries, and custom JPQL queries
- **JPA Relationships**: `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `@OneToOne`
- **JPA Auditing**: `@EnableJpaAuditing` for automatic `createdAt` and `updatedAt` tracking
- **Transaction Management**: `@Transactional` for atomic business operations
- **Input Validation**: Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Email`, `@Pattern`, `@Min`)
- **Exception Handling**: Centralized `@RestControllerAdvice` with custom exceptions and standard error responses
- **DTO Pattern & Mappers**: Clean separation of entities and request/response DTOs
- **Pagination & Sorting**: Spring Data `Pageable` and `Sort`
- **Java Date/Time API**: `LocalDate` and `ChronoUnit` for loan periods and overdue calculations
- **Database Profiles**: Multi-profile setup with H2 (in-memory) and MySQL (production)
- **Monitoring & Docs**: Spring Boot Actuator and SpringDoc OpenAPI 3 (Swagger UI)
- **Logging**: SLF4J with `@Slf4j`
- **Project Lombok**: `@Getter`, `@Setter`, `@Builder`, and constructors

---

## Modules & Entities Added

- **Book**: ISBN, title, publisher, publication year, total copies, available copies
- **Author**: Name, biography
- **Category**: Category name, description
- **Member**: Membership number, name, email, phone, membership status
- **BorrowRecord**: Book reference, member reference, borrow date, due date, return date, status
- **Fine**: Linked borrow record, overdue days, fine amount, payment status, payment reference
