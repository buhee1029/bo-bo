package com.wanted.bobo.budget.domain;

import com.wanted.bobo.budget.dto.BudgetStatByCategory;
import com.wanted.bobo.category.Category;
import java.time.YearMonth;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    boolean existsByUserIdAndCategoryAndYearmonth(Long userId, Category category, YearMonth yearmonth);

    List<Budget> findByUserIdAndYearmonth(Long userId, YearMonth yearmonth);

    List<Budget> findAllByUserId(Long userId);

    @Query(value =
          "SELECT " +
               "category, " +
               "ROUND(SUM(total) / (SELECT COUNT(DISTINCT user_id) FROM budgets WHERE yearmonth = :yearmonth)) AS percentage " +
          "FROM (" +
               "SELECT " +
               "   category, " +
               "   amount / SUM(amount) OVER (PARTITION BY user_id) * 100 AS total " +
               "FROM budgets " +
               "WHERE yearmonth = :yearmonth" +
          ") AS category_percentage " +
          "GROUP BY category", nativeQuery = true)
    List<BudgetStatByCategory> findAverageBudgetPercentagesByCategory(@Param("yearmonth") String yearmonth);

}
