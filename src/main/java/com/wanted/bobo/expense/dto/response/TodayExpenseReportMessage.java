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
public class TodayExpenseReportMessage {

    int totalTodayExpenses;
    Map<Category, Integer> todayCategoryExpenses;
    Map<Category, Integer> todayRecommendedCategoryBudgets;

    public Map<String, Object> toWebhookMessage() {
        MessageBuilder messageBuilder = new MessageBuilder("오늘의 지출 안내  📢", buildDescription());
        return messageBuilder.build();
    }

    private String buildDescription() {
        StringBuilder description = new StringBuilder();

        description.append("💰ㅤ오늘 지출 내역을 총액과 카테고리 별로 안내해드립니다.ㅤ💰\n\n");

        description.append("오늘의 총 지출 금액은 ")
                   .append(floorTo100(totalTodayExpenses))
                   .append("원입니다. \n\n");

        description.append("오늘의 적정 금액과 실제 사용 금액은 다음과 같습니다. \n")
                   .append("---------------------------------------------------\n");

        for (Map.Entry<Category, Integer> entry : todayRecommendedCategoryBudgets.entrySet()) {
            Category category = entry.getKey();
            int recommendedExpenses = entry.getValue();

            if (todayCategoryExpenses.containsKey(category)) {
                int actualExpenses = todayCategoryExpenses.get(category);
                double riskPercentage = (double) actualExpenses / recommendedExpenses;

                description.append("✔︎ㅤ")
                           .append(category.getName())
                           .append("ㅤ적정 금액 ")
                           .append(floorTo100(recommendedExpenses))
                           .append("원ㅤ사용한 금액 ")
                           .append(floorTo100(actualExpenses))
                           .append("원ㅤㅤ");

                if (riskPercentage > 1.0) {
                    description.append("🚨ㅤ위험도 ")
                               .append(String.format("%.2f", riskPercentage * 100))
                               .append("%\n");
                } else if (riskPercentage > 0.7) {
                    description.append("⚠️ㅤ위험도 ")
                               .append(String.format("%.2f", riskPercentage * 100))
                               .append("%\n");
                }else {
                    description.append("\n");
                }
            }
        }

        return description.toString();
    }

    private static int floorTo100(double amount) {
        return (int) (Math.floor(amount / 100) * 100);
    }

}
