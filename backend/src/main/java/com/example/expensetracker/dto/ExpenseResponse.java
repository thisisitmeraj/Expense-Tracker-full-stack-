package com.example.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ExpenseResponse(
        Long id,
        String title,
        BigDecimal amount,
        LocalDate expenseDate,
        String note,
        Long categoryId,
        String categoryName,
        LocalDateTime createdAt
) {
}
