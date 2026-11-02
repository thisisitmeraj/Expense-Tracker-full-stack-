package com.example.expensetracker.dto;

import java.math.BigDecimal;

/** Spring Data projection for SUM(amount) GROUP BY category. */
public interface CategoryTotal {

    String getCategoryName();

    BigDecimal getTotal();
}
