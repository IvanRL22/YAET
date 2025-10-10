package com.ivanrl.yaet.web.components;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MonthOverviewComponent implements WebComponent {

    public static final String VIEW_NAME = "budget :: #month-overview";

    private final BigDecimal totalIncome;
    private final BigDecimal totalSpent;

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
        model.put("monthIncome", totalIncome);
        model.put("monthSpent", totalSpent);
        model.put("monthBalance", totalIncome.subtract(totalSpent));
        return model;
    }
}
