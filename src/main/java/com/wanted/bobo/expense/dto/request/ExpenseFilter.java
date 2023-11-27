package com.wanted.bobo.expense.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseFilter {

    @NotBlank(message = "시작날짜를 입력해주세요.")
    @Pattern(
            regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
            message = "날짜 형식에 맞춰 입력해주세요.")
    private String start;

    @NotBlank(message = "끝날짜를 입력해주세요.")
    @Pattern(
            regexp = "^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$",
            message = "날짜 형식에 맞춰 입력해주세요.")
    private String end;

    private String category;

    @Positive(message = "최소금액은 양수이어야 합니다.")
    private Integer min;

    @Positive(message = "최대금액은 양수이어야 합니다.")
    private Integer max;

    public boolean isMinMaxValid() {
        return (min == null || max == null) || (min <= max);
    }

}
