# Digital Lending & Loan Management System (SprintLend)

A full-stack digital lending web application built with **Spring Boot, Spring Data JPA, Hibernate, MySQL, Spring Security (JWT), Swagger/OpenAPI, and Thymeleaf**.

## Features
- **Borrower:** Register with profile (PAN, income, employment), login with JWT, apply for loans with live EMI calculation, view generated EMI schedule, and pay installments (auto-closes loan when all EMIs are paid).
- **Admin:** View all loan applications, filter by status using Java Streams, and approve (auto-generates EMI schedule) or reject loans with remarks.
- **Validation & Business Rules:** Blocks duplicate active loans, enforces EMI <= 50% of monthly income, validates loan amount (₹10,000–₹50,00,000) and tenure (3–60 months), and returns consistent JSON errors via `@RestControllerAdvice`.

## ER Diagram
```mermaid
erDiagram
    USERS ||--o| BORROWER_PROFILES : "has one (1:1)"
    BORROWER_PROFILES ||--o{ LOANS : "applies for (1:N)"
    LOANS ||--o{ REPAYMENTS : "has schedule (1:N)"

    USERS {
        bigint id PK
        string email UK
        string password
        string role
    }
    BORROWER_PROFILES {
        bigint id PK
        string full_name
        string pan UK
        decimal monthly_income
        string employment_type
        bigint user_id FK
    }
    LOANS {
        bigint id PK
        decimal amount
        double interest_rate
        int tenure_months
        decimal emi
        string status
        string admin_remark
        bigint borrower_id FK
    }
    REPAYMENTS {
        bigint id PK
        int installment_no
        date due_date
        decimal amount
        string status
        bigint loan_id FK
    }