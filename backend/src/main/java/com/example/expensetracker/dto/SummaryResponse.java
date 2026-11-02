package com.example.expensetracker.dto;

import java.math.BigDecimal;
import java.util.List;

public record SummaryResponse(BigDecimal grandTotal, List<SummaryItemResponse> byCategory) {
}
