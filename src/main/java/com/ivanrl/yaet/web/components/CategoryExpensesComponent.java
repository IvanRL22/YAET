package com.ivanrl.yaet.web.components;

import com.ivanrl.yaet.web.BasicExpenseTO;
import com.ivanrl.yaet.web.CategoryExpenseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class CategoryExpensesComponent implements WebComponent {
    
    private final CategoryExpenseTO categoryWithExpenses;


    @Override
    public void attach(Model model) {
        model.addAttribute("categoryDetailsId", categoryWithExpenses.category().id());
        model.addAttribute("categoryName", categoryWithExpenses.category().name());
        model.addAttribute("categoryDescription", categoryWithExpenses.category().description());
        model.addAttribute("expenses", categoryWithExpenses.expenses());
        model.addAttribute("categoryTotal",
                           categoryWithExpenses.expenses().stream()
                                           .map(BasicExpenseTO::amount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

}
