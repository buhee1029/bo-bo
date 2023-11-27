package com.wanted.bobo.expense.service;

import com.wanted.bobo.budget.domain.Budget;
import com.wanted.bobo.budget.domain.BudgetRepository;
import com.wanted.bobo.category.Category;
import com.wanted.bobo.expense.domain.Expense;
import com.wanted.bobo.expense.domain.ExpenseRepository;
import com.wanted.bobo.expense.dto.ExpenseFilter;
import com.wanted.bobo.expense.dto.ExpenseListResponse;
import com.wanted.bobo.expense.dto.ExpenseRequest;
import com.wanted.bobo.expense.dto.ExpenseResponse;
import com.wanted.bobo.expense.exception.InvalidAmountRangeException;
import com.wanted.bobo.expense.exception.NotFoundExpenseException;
import com.wanted.bobo.expense.exception.UnsupportedExpenseCategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final BudgetRepository budgetRepository;
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
        validateExpenseCategoryExists(userId, Category.of(request.getCategory()));

        return ExpenseResponse.from(expenseRepository.save(request.toEntity(userId)));
    }

    @Transactional
    public ExpenseResponse modifyExpense(Long userId, Long expenseId, ExpenseRequest request) {
        validateExpenseCategoryExists(userId, Category.of(request.getCategory()));

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

    public void validateExpenseCategoryExists(Long userId, Category category) {
        boolean isExpenseCategoryExists = budgetRepository.existsByUserIdAndCategory(userId, category);
        if(!isExpenseCategoryExists) {
            throw new UnsupportedExpenseCategoryException();
        }
    }
}
