package com.wanted.bobo.expense.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ExpenseStatisticsResponse {
    Map<String, Integer> expenseStatistics;

    public static ExpenseStatisticsResponse from(Map<String, Integer> statistics) {
        return ExpenseStatisticsResponse.builder().expenseStatistics(statistics).build();
    }
}
