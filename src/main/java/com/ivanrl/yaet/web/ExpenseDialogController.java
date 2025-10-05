package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/expense-dialog")
@RequiredArgsConstructor
public class ExpenseDialogController {

    private final SeeExpensesUseCase seeExpensesUseCase;
    private final SeeCategoriesUseCase seeCategoriesUseCase;

    @GetMapping("/{id}")
    public ModelAndView getExpense(@PathVariable int id,
                                   Model model) {

        var expense = this.seeExpensesUseCase.findExpense(id);
        model.addAttribute("expense", expense);

        // Again not nice to have to get all categories from DB each time
        model.addAttribute("categories", this.seeCategoriesUseCase.getAll());

        return new ModelAndView("expenseDialog", model.asMap());
    }
}
