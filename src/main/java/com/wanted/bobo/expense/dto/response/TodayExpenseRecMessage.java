package com.wanted.bobo.expense.dto.response;

import com.wanted.bobo.category.Category;

import java.util.Map;

import com.wanted.bobo.expense.dto.helper.MessageBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TodayExpenseRecMessage {

    int totalBudget;
    int remainingBudget;
    Map<Category, Integer> todayRecommendedCategoryBudgets;

    private static final int BUDGET_THRESHOLD = 10000;

    public Map<String, Object> toWebhookMessage() {
        MessageBuilder messageBuilder = new MessageBuilder("오늘의 지출 추천ㅤ📢", buildDescription());
        return messageBuilder.build();
    }

    private String buildDescription() {
        StringBuilder description = new StringBuilder();

        description.append(generateComment());

        int todayTotalRecBudget = todayRecommendedCategoryBudgets.values().stream().mapToInt(Integer::intValue).sum();

        description.append("오늘 사용 가능한 총 금액은 ")
                   .append(floorTo100(todayTotalRecBudget))
                   .append("원 입니다.\n\n");
        description.append("오늘 카테고리 별 사용 가능한 금액은 다음과 같습니다.\n")
                   .append("---------------------------------------------------\n");

        for (Map.Entry<Category, Integer> entry : todayRecommendedCategoryBudgets.entrySet()) {
            Category category = entry.getKey();
            int todayRecBudget = entry.getValue();

            if (todayRecBudget > 0) {
                description.append("✔︎ㅤ")
                           .append(category.getName())
                           .append("ㅤ")
                           .append(floorTo100(todayRecBudget))
                           .append("원ㅤ\n");
            } else {
                description.append("✔︎ㅤ")
                           .append(category.getName())
                           .append("ㅤ")
                           .append(BUDGET_THRESHOLD)
                           .append("원ㅤ⚠️ㅤ예산초과\n");
            }
        }

        return description.toString();
    }

    public String generateComment() {
        if (remainingBudget > totalBudget * 0.8) {
            return "💛ㅤ아주 좋아요! 계속 이렇게 예산을 효율적으로 활용해보세요!ㅤ💛\n\n";
        } else if (remainingBudget > totalBudget * 0.6) {
            return "👏ㅤ예산을 잘 관리하고 계시네요! 더 발전하는 거에요!ㅤ👏\n\n";
        } else if (remainingBudget > totalBudget * 0.4) {
            return "⚠️ㅤ지출이 높아지고 있어요. 조금 더 절약할 수 있는 부분을 찾아보세요!ㅤ⚠️\n\n";
        } else {
            return "🚨ㅤ예산 초과! 다음 번에는 조금 더 신중하게 지출해보세요. 🚨\n\n";
        }
    }

    private static int floorTo100(double amount) {
        return (int) (Math.floor(amount / 100) * 100);
    }

}