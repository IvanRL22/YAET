package com.ivanrl.yaet.web.components;

import com.ivanrl.yaet.web.BasicExpenseTO;
import com.ivanrl.yaet.web.CategoryExpenseTO;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class CategoryExpensesComponent implements WebComponent {

    private static final String VIEW_NAME = "budget :: #category-expenses-content";
    
    private final CategoryExpenseTO categoryWithExpenses;


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
        model.put("categoryDetailsId", categoryWithExpenses.category().id());
        model.put("categoryName", categoryWithExpenses.category().name());
        model.put("categoryDescription", categoryWithExpenses.category().description());
        model.put("expenses", categoryWithExpenses.expenses());
        model.put("categoryTotal",
                           categoryWithExpenses.expenses().stream()
                                               .map(BasicExpenseTO::amount)
                                               .reduce(BigDecimal.ZERO, BigDecimal::add));
        return model;
    }
}
