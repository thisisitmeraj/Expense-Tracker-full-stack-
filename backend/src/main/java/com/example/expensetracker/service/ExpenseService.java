package com.example.expensetracker.service;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.dto.ExpenseRequest;
import com.example.expensetracker.dto.ExpenseResponse;
import com.example.expensetracker.dto.SummaryItemResponse;
import com.example.expensetracker.dto.SummaryResponse;
import com.example.expensetracker.entity.Category;
import com.example.expensetracker.entity.Expense;
import com.example.expensetracker.entity.User;
import com.example.expensetracker.exception.ApiException;
import com.example.expensetracker.repository.ExpenseRepository;
import com.example.expensetracker.repository.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryService categoryService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            UserRepository userRepository,
            CategoryService categoryService
    ) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryService = categoryService;
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(Long userId) {
        return expenseRepository.findByUserIdOrderByExpenseDateDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpenseResponse get(Long userId, Long id) {
        return toResponse(getOwned(userId, id));
    }

    @Transactional
    public ExpenseResponse create(Long userId, ExpenseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        // Category must belong to the same user — otherwise anyone could tag another person's category.
        Category category = categoryService.getOwned(userId, request.categoryId());

        Expense expense = new Expense();
        apply(expense, request, user, category);
        return toResponse(expenseRepository.save(expense));
    }

    @Transactional
    public ExpenseResponse update(Long userId, Long id, ExpenseRequest request) {
        Expense expense = getOwned(userId, id);
        Category category = categoryService.getOwned(userId, request.categoryId());
        apply(expense, request, expense.getUser(), category);
        return toResponse(expense);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        Expense expense = getOwned(userId, id);
        expenseRepository.delete(expense);
    }

    @Transactional(readOnly = true)
    public SummaryResponse summary(Long userId) {
        List<CategoryTotal> rows = expenseRepository.sumAmountByCategory(userId);
        List<SummaryItemResponse> items = rows.stream()
                .map(row -> new SummaryItemResponse(row.getCategoryName(), row.getTotal()))
                .toList();
        BigDecimal grandTotal = items.stream()
                .map(SummaryItemResponse::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SummaryResponse(grandTotal, items);
    }

    private Expense getOwned(Long userId, Long id) {
        return expenseRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Expense not found"));
    }

    private void apply(Expense expense, ExpenseRequest request, User user, Category category) {
        expense.setTitle(request.title().trim());
        expense.setAmount(request.amount());
        expense.setExpenseDate(request.expenseDate());
        expense.setNote(request.note());
        expense.setUser(user);
        expense.setCategory(category);
    }

    private ExpenseResponse toResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getTitle(),
                expense.getAmount(),
                expense.getExpenseDate(),
                expense.getNote(),
                expense.getCategory().getId(),
                expense.getCategory().getName(),
                expense.getCreatedAt()
        );
    }
}
