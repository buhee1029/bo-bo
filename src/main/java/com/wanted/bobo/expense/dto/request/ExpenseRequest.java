package com.wanted.bobo.expense.dto.request;

import com.wanted.bobo.category.Category;
import com.wanted.bobo.expense.domain.Expense;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseRequest {

    private String memo;

    @Min(value = 0, message = "0원 이상 입력해주세요.")
    private int amount;

    @NotBlank(message = "카테고리를 선택해주세요.")
    private String category;

    @Pattern(
            regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
            message = "날짜 형식에 맞춰 입력해주세요.")
    private String date;

    public Expense toEntity(Long userId) {
        return Expense.builder()
                      .userId(userId)
                      .amount(amount)
                      .memo(memo)
                      .date(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                      .category(Category.of(category))
                      .isExclude(false)
                      .build();

    }


}

