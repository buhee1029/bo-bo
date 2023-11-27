package com.wanted.bobo.expense.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.wanted.bobo.category.Category;
import com.wanted.bobo.expense.domain.Expense;
import com.wanted.bobo.expense.domain.ExpenseRepository;
import com.wanted.bobo.expense.dto.request.ExpenseRequest;
import com.wanted.bobo.expense.dto.response.ExpenseResponse;
import com.wanted.bobo.expense.exception.NotFoundExpenseException;
import com.wanted.bobo.expense.exception.NotMatchExpenseUserException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_ANOTHER_USER_ID = 2L;
    private static final int TEST_AMOUNT = 100000;
    private static final int TEST_UPDATE_AMOUNT = 200000;
    private static final String TEST_MEMO = "test";
    private static final String TEST_DATE = "2023-11-11";
    private static final String TEST_CATEGORY = "food";
    private static final String TEST_UPDATE_CATEGORY = "traffic";

    private Expense expense;

    @BeforeEach
    void setUp() {
        expense = Expense.builder()
                         .userId(TEST_USER_ID)
                         .amount(TEST_AMOUNT)
                         .memo(TEST_MEMO)
                         .category(Category.FOOD)
                         .date(LocalDate.parse(TEST_DATE, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                         .isExclude(false)
                         .build();
    }

    @DisplayName("지출 등록 성공")
    @Test
    void register_expense_success() {
        ExpenseRequest request = new ExpenseRequest(TEST_MEMO, TEST_AMOUNT, TEST_CATEGORY, TEST_DATE);
        when(expenseRepository.save(any())).thenReturn(expense);

        ExpenseResponse response = expenseService.registerExpense(TEST_USER_ID, request);

        assertThat(response.getAmount()).isEqualTo(request.getAmount());
        assertThat(response.getCategory()).isEqualTo(Category.of(request.getCategory()));
    }

    @DisplayName("지출 수정 성공")
    @Test
    void modify_expense_success() {
        ExpenseRequest request = new ExpenseRequest(TEST_MEMO, TEST_UPDATE_AMOUNT, TEST_UPDATE_CATEGORY, TEST_DATE);
        when(expenseRepository.findById(any())).thenReturn(Optional.of(expense));

        ExpenseResponse response = expenseService.modifyExpense(TEST_USER_ID, expense.getId(), request);

        assertThat(response.getAmount()).isEqualTo(request.getAmount());
        assertThat(response.getCategory()).isEqualTo(Category.of(request.getCategory()));
    }

    @DisplayName("지출 수정 실패 - 지출 정보가 없을때")
    @Test
    void modify_expense_fail_by_no_expense() {
        ExpenseRequest request = new ExpenseRequest(TEST_MEMO, TEST_UPDATE_AMOUNT, TEST_UPDATE_CATEGORY, TEST_DATE);
        when(expenseRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseService.modifyExpense(TEST_USER_ID, expense.getId(), request))
                .isInstanceOf(NotFoundExpenseException.class);
    }

    @DisplayName("지출 수정 실패 - 작성자가 일치하지 않을때")
    @Test
    void modify_expense_fail_by_no_match_user() {
        ExpenseRequest request = new ExpenseRequest(TEST_MEMO, TEST_UPDATE_AMOUNT, TEST_UPDATE_CATEGORY, TEST_DATE);
        when(expenseRepository.findById(any())).thenReturn(Optional.of(expense));

        assertThatThrownBy(() -> expenseService.modifyExpense(TEST_ANOTHER_USER_ID, expense.getId(), request))
                .isInstanceOf(NotMatchExpenseUserException.class);
    }

    @DisplayName("지출 합계 제외 처리 성공")
    @Test
    void exclude_expense_success() {
        when(expenseRepository.findById(any())).thenReturn(Optional.of(expense));
        expenseService.toggleExcludedStatus(TEST_USER_ID, expense.getId());
    }

    @DisplayName("지출 삭제 성공")
    @Test
    void remove_expense_success() {
        when(expenseRepository.findById(any())).thenReturn(Optional.of(expense));
        expenseService.removeExpense(TEST_USER_ID, expense.getId());
    }

}