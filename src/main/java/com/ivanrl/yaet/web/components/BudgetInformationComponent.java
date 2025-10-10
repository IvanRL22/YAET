package com.ivanrl.yaet.web.components;

import com.ivanrl.yaet.web.BudgetCategoryTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
public class BudgetInformationComponent implements WebComponent {


    private final YearMonth month;
    private final List<BudgetCategoryTO> categories;

    @Override
    public void attach(Model model) {
        model.addAttribute("currentMonth", month);
        model.addAttribute("budgetCategories", categories);
        model.addAttribute("totalAssigned",
                           categories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("totalBalance",
                           categories.stream()
                                        .map(BudgetCategoryTO::getTotalAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Small utility to copy budget from previous month
        if (month.isAfter(YearMonth.now())
                && categories.stream().anyMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.addAttribute("missingBudgets", true);
        }
    }

}
