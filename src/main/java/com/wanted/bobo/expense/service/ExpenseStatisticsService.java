package com.wanted.bobo.expense.service;

import com.wanted.bobo.expense.domain.Expense;
import com.wanted.bobo.expense.domain.ExpenseRepository;
import com.wanted.bobo.expense.dto.response.ExpenseStatisticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExpenseStatisticsService {

    private final ExpenseRepository expenseRepository;

    public ExpenseStatisticsResponse getExpenseStatistics(Long userId) {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("lastMonth", calculateLastMonthStatistics(userId));
        statistics.put("lastWeek", calculateLastWeekStatistics(userId));
        statistics.put("otherUsers", calculateOtherUsersStatistics(userId));

        return ExpenseStatisticsResponse.from(statistics);
    }

    private Integer calculateOtherUsersStatistics(Long userId) {
        return 0;
    }

    private Integer calculateLastWeekStatistics(Long userId) {
        LocalDate today = LocalDate.now();
        String lastWeekStart = today.minusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String lastWeekEnd = today.minusWeeks(1).plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentWeekStart = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentWeekEnd = today.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return calculateConsumptionRate(userId, lastWeekStart, lastWeekEnd, currentWeekStart, currentWeekEnd);
    }

    private Integer calculateLastMonthStatistics(Long userId) {
        LocalDate today = LocalDate.now();
        String lastMonthStart = today.minusMonths(1)
                .withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String lastMonthEnd = today.minusMonths(1)
                .withDayOfMonth(today.getDayOfMonth())
                .plusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String currentMonthStart = today.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentMonthEnd = today.plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        return calculateConsumptionRate(userId, lastMonthStart, lastMonthEnd, currentMonthStart, currentMonthEnd);
    }

    private Integer calculateConsumptionRate(
            Long userId, String lastWeekStart, String lastWeekEnd, String currentWeekStart, String currentWeekEnd) {
        List<Expense> lastExpenses = expenseRepository.findByUserIdAndDateRange(userId, lastWeekStart, lastWeekEnd);
        List<Expense> currentExpenses = expenseRepository.findByUserIdAndDateRange(userId, currentWeekStart, currentWeekEnd);

        double lastTotalAmount = lastExpenses.stream().mapToDouble(Expense::getAmount).sum();
        double currentTotalAmount = currentExpenses.stream().mapToDouble(Expense::getAmount).sum();

        return (int) (currentTotalAmount / lastTotalAmount * 100);
    }

}
