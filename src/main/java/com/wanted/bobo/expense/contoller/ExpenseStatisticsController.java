package com.wanted.bobo.expense.contoller;

import com.wanted.bobo.common.response.ApiResponse;
import com.wanted.bobo.expense.dto.response.ExpenseStatisticsResponse;
import com.wanted.bobo.expense.service.ExpenseStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "지출 통계")
@RestController
@RequestMapping("/expenses/stat")
@RequiredArgsConstructor
public class ExpenseStatisticsController {

    private final ExpenseStatisticsService statisticsService;

    @GetMapping
    @Operation(summary = "지출 통계 - 지난달 대비 소비율, 지난요일 대비 소비율, 다른 사용자 대비 소비율")
    public ApiResponse<ExpenseStatisticsResponse> getExpenseStatistics(@RequestAttribute Long userId) {
        return ApiResponse.ok(statisticsService.getExpenseStatistics(userId));
    }
}
