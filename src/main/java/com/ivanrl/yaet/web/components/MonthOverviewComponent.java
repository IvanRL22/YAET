package com.ivanrl.yaet.web.components;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class MonthOverviewComponent implements WebComponent {

    private final BigDecimal totalIncome;
    private final BigDecimal totalSpent;

    @Override
    public void attach(Model model) {
        model.addAttribute("monthIncome", totalIncome);
        model.addAttribute("monthSpent", totalSpent);
        model.addAttribute("monthBalance", totalIncome.subtract(totalSpent));
    }

}
