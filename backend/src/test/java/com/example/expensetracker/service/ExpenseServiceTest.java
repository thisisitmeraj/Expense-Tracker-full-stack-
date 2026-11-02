package com.example.expensetracker.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.dto.SummaryResponse;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryService categoryService;

    private ExpenseService expenseService;

    @BeforeEach
    void setUp() {
        expenseService = new ExpenseService(expenseRepository, userRepository, categoryService);
    }

    @Test
    void summaryAddsTotalsPerCategory() {
        when(expenseRepository.sumAmountByCategory(1L)).thenReturn(List.of(
                total("Food", "12.50"),
                total("Travel", "40.00")
        ));

        SummaryResponse summary = expenseService.summary(1L);

        assertEquals(new BigDecimal("52.50"), summary.grandTotal());
        assertEquals(2, summary.byCategory().size());
        assertEquals("Food", summary.byCategory().get(0).categoryName());
        assertEquals(new BigDecimal("12.50"), summary.byCategory().get(0).total());
    }

    private CategoryTotal total(String name, String amount) {
        return new CategoryTotal() {
            @Override
            public String getCategoryName() {
                return name;
            }

            @Override
            public BigDecimal getTotal() {
                return new BigDecimal(amount);
            }
        };
    }
}
