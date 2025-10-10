package com.ivanrl.yaet.web.components;

import com.ivanrl.yaet.web.BudgetCategoryTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class BudgetInformationComponent implements WebComponent {

    private static final String VIEW_NAME = "budget :: budget-info";

    private final YearMonth month;
    private final List<BudgetCategoryTO> categories;

    @Override
    public void attach(Model model) {
        model.addAllAttributes(generateAttributeMap());
    }

    @Override
    public ModelAndView toModelAndView() {
        return new ModelAndView(VIEW_NAME, generateAttributeMap());
    }

    private Map<String, Object> generateAttributeMap() {
        Map<String, Object> model = new HashMap<>();
        model.put("currentMonth", month);
        model.put("budgetCategories", categories);
        model.put("totalAssigned",
                           categories.stream()
                                     .map(BudgetCategoryTO::amountAssigned)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.put("totalBalance",
                           categories.stream()
                                     .map(BudgetCategoryTO::getTotalAmount)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Small utility to copy budget from previous month
        if (month.isAfter(YearMonth.now())
                && categories.stream().anyMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.put("missingBudgets", true);
        }
        return model;
    }
}
