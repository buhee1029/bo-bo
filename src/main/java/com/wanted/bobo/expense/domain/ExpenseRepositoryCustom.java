package com.wanted.bobo.expense.domain;

import com.wanted.bobo.expense.dto.request.ExpenseFilter;
import com.wanted.bobo.expense.dto.response.ExpenseListResponse;

public interface ExpenseRepositoryCustom {
    ExpenseListResponse findByCondition(Long userId, ExpenseFilter condition);
}
