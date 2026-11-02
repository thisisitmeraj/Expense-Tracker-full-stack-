package com.example.expensetracker.repository;

import com.example.expensetracker.dto.CategoryTotal;
import com.example.expensetracker.entity.Expense;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
            SELECT e FROM Expense e
            JOIN FETCH e.category
            WHERE e.user.id = :userId
            ORDER BY e.expenseDate DESC
            """)
    List<Expense> findByUserIdOrderByExpenseDateDesc(@Param("userId") Long userId);

    Optional<Expense> findByIdAndUserId(Long id, Long userId);

    // Interview point: aggregation in SQL is cheaper than loading every expense into Java.
    @Query("""
            SELECT c.name AS categoryName, COALESCE(SUM(e.amount), 0) AS total
            FROM Expense e
            JOIN e.category c
            WHERE e.user.id = :userId
            GROUP BY c.name
            ORDER BY c.name
            """)
    List<CategoryTotal> sumAmountByCategory(@Param("userId") Long userId);
}
