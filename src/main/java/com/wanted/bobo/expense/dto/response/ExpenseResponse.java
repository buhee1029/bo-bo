package com.wanted.bobo.expense.dto.response;

import com.wanted.bobo.category.Category;
import com.wanted.bobo.expense.domain.Expense;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ExpenseResponse {

    private Long id;
    private int amount;
    private String memo;
    private LocalDate date;
    private Category category;
    private boolean isExclude;


    public static ExpenseResponse from(Expense expense) {
        return ExpenseResponse.builder()
                              .id(expense.getId())
                              .memo(expense.getMemo())
                              .date(expense.getDate())
                              .amount(expense.getAmount())
                              .category(expense.getCategory())
                              .isExclude(expense.isExclude())
                              .build();
    }
}
