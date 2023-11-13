package com.wanted.bobo.expense.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseRepository extends
        JpaRepository<Expense, Long>,
        ExpenseRepositoryCustom {

}
