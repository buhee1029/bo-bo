package com.wanted.bobo.expense.domain;

import java.util.List;

import com.wanted.bobo.expense.dto.ExpenseRateToTotalBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends
        JpaRepository<Expense, Long>,
        ExpenseRepositoryCustom {

    @Query(value =
            "SELECT * FROM expenses " +
                    "WHERE user_id = :userId AND date >= :startOfMonth AND date < :endDate AND is_exclude = 0", nativeQuery = true)
    List<Expense> findByUserIdAndDateRange(Long userId, String startOfMonth, String endDate);

    @Query(value =
            "SELECT * FROM expenses " +
                    "WHERE user_id = :userId AND date = :date AND is_exclude = 0", nativeQuery = true)
    List<Expense> findByUserIdAndDate(Long userId, String date);

    @Query(value =
            "SELECT user_id AS userId, " +
                    "ROUND(SUM(expense_amount) / SUM(budget_amount) * 100, 2) AS rate " +
                    "FROM (" +
                    "    SELECT user_id, amount AS budget_amount, 0 AS expense_amount FROM budgets WHERE yearmonth = :yearmonth " +
                    "    UNION ALL " +
                    "    SELECT user_id, 0 AS budget_amount, amount AS expense_amount FROM expenses WHERE DATE_FORMAT(date, '%Y-%m') = :yearmonth " +
                    ") AS subquery " +
                    "GROUP BY user_id", nativeQuery = true)
    List<ExpenseRateToTotalBudget> findExpenseRateToTotalBudget(@Param("yearmonth") String yearmonth);
}
