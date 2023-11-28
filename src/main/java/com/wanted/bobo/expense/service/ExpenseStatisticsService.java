package com.wanted.bobo.expense.service;

import com.wanted.bobo.expense.domain.Expense;
import com.wanted.bobo.expense.domain.ExpenseRepository;
import com.wanted.bobo.expense.dto.ExpenseRateToTotalBudget;
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

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final ExpenseRepository expenseRepository;

    public ExpenseStatisticsResponse getExpenseStatistics(Long userId) {
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("lastMonth", calculateLastMonthStatistics(userId));
        statistics.put("lastWeek", calculateLastWeekStatistics(userId));
        statistics.put("otherUsers", calculateOtherUsersStatistics(userId));

        return ExpenseStatisticsResponse.from(statistics);
    }

    private Integer calculateOtherUsersStatistics(Long userId) {
        String yearmonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<ExpenseRateToTotalBudget> rates = expenseRepository.findExpenseRateToTotalBudget(yearmonth);

        double myRate = getExpenseRateForUser(rates, userId);
        double otherUserAverageRate = getOtherUsersAverageRate(rates, userId);

        return otherUserAverageRate == 0.0 ? 0 : (int) (myRate / otherUserAverageRate * 100);
    }

    private Integer calculateLastWeekStatistics(Long userId) {
        LocalDate today = LocalDate.now();
        String lastWeekStart = today.minusWeeks(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String lastWeekEnd = today.minusWeeks(1).plusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String currentWeekStart = today.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String currentWeekEnd = today.plusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        return calculateConsumptionRate(userId, lastWeekStart, lastWeekEnd, currentWeekStart, currentWeekEnd);
    }

    private Integer calculateLastMonthStatistics(Long userId) {
        LocalDate today = LocalDate.now();
        String lastMonthStart = today.minusMonths(1)
                .withDayOfMonth(1)
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String lastMonthEnd = today.minusMonths(1)
                .withDayOfMonth(today.getDayOfMonth())
                .plusDays(1)
                .format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        String currentMonthStart = today.withDayOfMonth(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        String currentMonthEnd = today.plusDays(1).format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        return calculateConsumptionRate(userId, lastMonthStart, lastMonthEnd, currentMonthStart, currentMonthEnd);
    }

    private Integer calculateConsumptionRate(
            Long userId, String lastWeekStart, String lastWeekEnd, String currentWeekStart, String currentWeekEnd) {
        List<Expense> lastExpenses = expenseRepository.findByUserIdAndDateRange(userId, lastWeekStart, lastWeekEnd);
        List<Expense> currentExpenses = expenseRepository.findByUserIdAndDateRange(userId, currentWeekStart, currentWeekEnd);

        double lastTotalAmount = lastExpenses.stream().mapToDouble(Expense::getAmount).sum();
        double currentTotalAmount = currentExpenses.stream().mapToDouble(Expense::getAmount).sum();

        return lastTotalAmount == 0.0 ? 0 : (int) (currentTotalAmount / lastTotalAmount * 100);
    }

    private double getExpenseRateForUser(List<ExpenseRateToTotalBudget> rates, Long userId) {
        return rates.stream()
                .filter(rate -> rate.getUserId().equals(userId))
                .mapToDouble(ExpenseRateToTotalBudget::getRate)
                .findFirst().orElse(0.0);
    }

    private double getOtherUsersAverageRate(List<ExpenseRateToTotalBudget> rates, Long userId) {
        return rates.stream()
                .filter(rate -> !rate.getUserId().equals(userId))
                .mapToDouble(ExpenseRateToTotalBudget::getRate)
                .average().orElse(0.0);
    }

}
