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
        return 0;
    }

    private Integer calculateLastMonthStatistics(Long userId) {
        LocalDate today = LocalDate.now().plusDays(1);
        String lastMonthStart = today.minusMonths(1)
                .withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String lastMonthEnd = today.minusMonths(1)
                .withDayOfMonth(today.getDayOfMonth())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        String currentMonthStart = today.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String currentMonthEnd = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        List<Expense> lastMonthExpense = expenseRepository.findByUserIdAndDateRange(userId, lastMonthStart, lastMonthEnd);
        List<Expense> currentMonthExpense = expenseRepository.findByUserIdAndDateRange(userId, currentMonthStart, currentMonthEnd);

        double lastMonthTotalAmount = lastMonthExpense.stream().mapToDouble(Expense::getAmount).sum();
        double currentMonthTotalAmount = currentMonthExpense.stream().mapToDouble(Expense::getAmount).sum();

        return (int) (currentMonthTotalAmount / lastMonthTotalAmount * 100);
    }

}
