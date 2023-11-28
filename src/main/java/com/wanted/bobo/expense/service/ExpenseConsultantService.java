package com.wanted.bobo.expense.service;

import com.wanted.bobo.budget.domain.Budget;
import com.wanted.bobo.budget.domain.BudgetRepository;
import com.wanted.bobo.category.Category;
import com.wanted.bobo.expense.domain.Expense;
import com.wanted.bobo.expense.domain.ExpenseRepository;
import com.wanted.bobo.expense.dto.response.TodayExpenseRecMessage;
import com.wanted.bobo.expense.dto.response.TodayExpenseReportMessage;
import com.wanted.bobo.user.domain.User;
import com.wanted.bobo.user.domain.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ExpenseConsultantService {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 8 * * *")
    public void sendTodayExpenseRecommendation() {
        List<User> users = userRepository.findByUrlNotNull();
        for (User user : users) {
            TodayExpenseRecMessage expenseRecMessage = generateExpenseRecommendations(user.getId());
            sendWebhookMessage(expenseRecMessage.toWebhookMessage(), user.getUrl());
        }
    }

    @Scheduled(cron = "0 0 20 * * *")
    public void sendTodayExpenseReport() {
        List<User> users = userRepository.findByUrlNotNull();
        for (User user : users) {
            TodayExpenseReportMessage expenseRecMessage = generateExpenseReport(user.getId());
            sendWebhookMessage(expenseRecMessage.toWebhookMessage(), user.getUrl());
        }
    }

    private void sendWebhookMessage(Object message, String discordWebhookUrl) {
        WebClient webClient = WebClient.create();
        webClient.post()
                 .uri(discordWebhookUrl)
                 .body(BodyInserters.fromValue(message))
                 .header("Content-Type", "application/json")
                 .retrieve()
                 .bodyToMono(String.class)
                 .subscribe();

    }

    private TodayExpenseRecMessage generateExpenseRecommendations(Long userId) {
        List<Expense> expenses = getExpensesForDateRange(userId);
        List<Budget> budgets = getBudgetsForUserAndYearMonth(userId);

        int totalBudget = budgets.stream().mapToInt(Budget::getAmount).sum();
        int totalExpense = expenses.stream().mapToInt(Expense::getAmount).sum();
        int remainingBudget = totalBudget - totalExpense;

        return new TodayExpenseRecMessage(totalBudget,
                                          remainingBudget,
                                          calculateTodayRecommendedBudgets(expenses, budgets));
    }

    private TodayExpenseReportMessage generateExpenseReport(Long userId) {
        List<Expense> expenses = getExpensesForDateRange(userId);
        List<Budget> budgets = getBudgetsForUserAndYearMonth(userId);

        List<Expense> todayExpenses = expenseRepository.findByUserIdAndDate(
                userId,
                getFormattedDate(LocalDate.now()));

        Map<Category, Integer> todayCategoryExpenses =
                todayExpenses.stream()
                             .collect(Collectors.groupingBy(
                                     Expense::getCategory, Collectors.summingInt(Expense::getAmount)));

        int totalTodayExpenses = todayExpenses.stream().mapToInt(Expense::getAmount).sum();

        return new TodayExpenseReportMessage(totalTodayExpenses,
                                             todayCategoryExpenses,
                                             calculateTodayRecommendedBudgets(expenses, budgets));
    }

    private List<Expense> getExpensesForDateRange(Long userId) {
        LocalDate today = LocalDate.now();
        String startOfMonth = getFormattedDate(today.withDayOfMonth(1));
        String endDate = getFormattedDate(today.minusDays(1));

        return expenseRepository.findByUserIdAndDateRange(userId, startOfMonth, endDate);
    }

    private List<Budget> getBudgetsForUserAndYearMonth(Long userId) {
        return budgetRepository.findByUserIdAndYearmonth(userId, YearMonth.now());
    }

    private String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    private Map<Category, Integer> calculateTodayRecommendedBudgets(List<Expense> expenses, List<Budget> budgets) {
        Map<Category, Integer> todayRecommendedCategoryBudgets
                = budgets.stream()
                         .collect(Collectors.groupingBy(
                                 Budget::getCategory,
                                 Collectors.summingInt(Budget::getAmount)));

        expenses.forEach(expense -> todayRecommendedCategoryBudgets.merge(
                expense.getCategory(),
                -expense.getAmount(),
                Integer::sum
        ));

        int daysRemainingInMonth = YearMonth.now().lengthOfMonth() - LocalDate.now().getDayOfMonth() + 1;

        return todayRecommendedCategoryBudgets.entrySet()
                                              .stream()
                                              .collect(Collectors.toMap(
                                                      Map.Entry::getKey,
                                                      entry -> entry.getValue() / daysRemainingInMonth));
    }

}
