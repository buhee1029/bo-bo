package com.wanted.bobo.expense.domain;

import static com.wanted.bobo.expense.domain.QExpense.expense;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wanted.bobo.category.Category;
import com.wanted.bobo.expense.dto.request.ExpenseFilter;
import com.wanted.bobo.expense.dto.response.ExpenseListResponse;
import com.wanted.bobo.expense.dto.response.ExpenseResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExpenseRepositoryImpl implements ExpenseRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public ExpenseListResponse findByCondition(Long userId, ExpenseFilter filter) {
        int totalExpenses = getTotalExpenses(userId, filter);
        Map<Category, Integer> totalExpensesByCategory = getTotalExpensesByCategory(userId, filter);
        List<ExpenseResponse> filteredExpenses = getFilteredExpenses(userId, filter);

        return ExpenseListResponse.of(totalExpenses, totalExpensesByCategory, filteredExpenses);
    }

    private Integer getTotalExpenses(Long userId, ExpenseFilter filter) {
        return Optional.ofNullable(
                               jpaQueryFactory
                                       .select(expense.amount.sum())
                                       .from(expense)
                                       .where(userEq(userId),
                                              isExcludeEq(),
                                              dateBetween(filter.getStart(), filter.getEnd()),
                                              categoryEq(filter.getCategory()),
                                              amountBetween(filter.getMin(), filter.getMax()))
                                       .fetchOne())
                       .orElse(0);
    }

    private Map<Category, Integer> getTotalExpensesByCategory(Long userId, ExpenseFilter filter) {
        List<Tuple> result = jpaQueryFactory
                .select(expense.category, expense.amount.sum().as("total"))
                .from(expense)
                .where(userEq(userId),
                       isExcludeEq(),
                       dateBetween(filter.getStart(), filter.getEnd()),
                       categoryEq(filter.getCategory()),
                       amountBetween(filter.getMin(), filter.getMax()))
                .groupBy(expense.category)
                .fetch();

        return result.stream()
                     .collect(Collectors.toMap(
                             tuple -> tuple.get(expense.category),
                             tuple -> Optional.ofNullable(tuple.get(1, Integer.class)).orElse(0)
                     ));
    }

    private List<ExpenseResponse> getFilteredExpenses(Long userId, ExpenseFilter filter) {
        return jpaQueryFactory
                .select(Projections.constructor(
                        ExpenseResponse.class,
                        expense.id,
                        expense.amount,
                        expense.memo,
                        expense.date,
                        expense.category,
                        expense.isExclude))
                .from(expense)
                .where(userEq(userId),
                       dateBetween(filter.getStart(), filter.getEnd()),
                       categoryEq(filter.getCategory()),
                       amountBetween(filter.getMin(), filter.getMax()))
                .fetch();
    }

    private BooleanExpression userEq(Long userId) {
        return expense.userId.eq(userId);
    }

    private BooleanExpression isExcludeEq() {
        return expense.isExclude.eq(false);
    }

    private BooleanExpression dateBetween(String start, String end) {
        return expense.date.between(parseDate(start), parseDate(end));
    }

    private LocalDate parseDate(String date) {
        return StringUtils.isNullOrEmpty(date) ?
                null : LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private BooleanExpression categoryEq(String category) {
        return StringUtils.isNullOrEmpty(category) ? Expressions.TRUE : expense.category.eq(Category.of(category));
    }

    private BooleanExpression amountBetween(Integer min, Integer max) {
        if (min != null) {
            return max != null ? expense.amount.between(min, max) : expense.amount.goe(min);
        } else if (max != null) {
            return expense.amount.loe(max);
        } else {
            return expense.amount.isNotNull();
        }
    }
}