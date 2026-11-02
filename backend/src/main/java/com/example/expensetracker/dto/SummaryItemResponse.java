package com.example.expensetracker.dto;

import java.math.BigDecimal;

public record SummaryItemResponse(String categoryName, BigDecimal total) {
}
