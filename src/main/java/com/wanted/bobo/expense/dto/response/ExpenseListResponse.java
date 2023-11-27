package com.wanted.bobo.expense.dto.response;

import com.wanted.bobo.category.Category;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenseListResponse {

    private int totalExpenses;
    private Map<Category, Integer> totalExpensesByCategory;
    private List<ExpenseResponse> expenses;

    public static ExpenseListResponse of(
            int totalExpenses,
            Map<Category, Integer> totalExpensesByCategory,
            List<ExpenseResponse> expenses) {
        return ExpenseListResponse.builder()
                                  .totalExpenses(totalExpenses)
                                  .totalExpensesByCategory(totalExpensesByCategory)
                                  .expenses(expenses)
                                  .build();
    }
}
