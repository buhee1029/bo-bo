package com.wanted.bobo.expense.service;

import com.wanted.bobo.expense.domain.Expense;
import com.wanted.bobo.expense.domain.ExpenseRepository;
import com.wanted.bobo.expense.dto.request.ExpenseFilter;
import com.wanted.bobo.expense.dto.response.ExpenseListResponse;
import com.wanted.bobo.expense.dto.request.ExpenseRequest;
import com.wanted.bobo.expense.dto.response.ExpenseResponse;
import com.wanted.bobo.expense.exception.InvalidAmountRangeException;
import com.wanted.bobo.expense.exception.NotFoundExpenseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Transactional(readOnly = true)
    public ExpenseListResponse getExpenses(Long userId, ExpenseFilter filter) {
        if (!filter.isMinMaxValid()) {
            throw new InvalidAmountRangeException();
        }

        return expenseRepository.findByCondition(userId, filter);
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpense(Long userId, Long expenseId) {
        Expense expense = findExpense(expenseId);
        expense.verifyMatchUser(userId);

        return ExpenseResponse.from(expense);
    }

    @Transactional
    public ExpenseResponse registerExpense(Long userId, ExpenseRequest request) {
        return ExpenseResponse.from(expenseRepository.save(request.toEntity(userId)));
    }

    @Transactional
    public ExpenseResponse modifyExpense(Long userId, Long expenseId, ExpenseRequest request) {
        Expense expense = findExpense(expenseId);
        expense.verifyMatchUser(userId);
        expense.changeInfo(request);

        return ExpenseResponse.from(expense);
    }

    @Transactional
    public void toggleExcludedStatus(Long userId, Long expenseId) {
        Expense expense = findExpense(expenseId);
        expense.verifyMatchUser(userId);
        expense.toggleExclude();
    }

    @Transactional
    public void removeExpense(Long userId, Long expenseId) {
        Expense expense = findExpense(expenseId);
        expense.verifyMatchUser(userId);
        expenseRepository.delete(expense);
    }

    private Expense findExpense(Long id) {
        return expenseRepository.findById(id).orElseThrow(NotFoundExpenseException::new);
    }
}
