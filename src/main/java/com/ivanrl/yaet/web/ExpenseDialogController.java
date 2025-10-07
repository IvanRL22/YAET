package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import com.ivanrl.yaet.domain.expense.ManageExpensesUseCase;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Controller
@RequestMapping("/expense-dialog")
@RequiredArgsConstructor
public class ExpenseDialogController {

    private final SeeExpensesUseCase seeExpensesUseCase;
    private final ManageExpensesUseCase manageExpensesUseCase;
    private final SeeCategoriesUseCase seeCategoriesUseCase;

    @GetMapping("/{id}")
    public ModelAndView getExpense(@PathVariable int id,
                                   Model model) {

        var expense = this.seeExpensesUseCase.findExpense(id);
        model.addAttribute("expense", expense); // When I move category into the request I will be able to remove this
        model.addAttribute("request", UpdateExpenseRequestTO.from(expense));

        // For now cannot change the date to a different month
        model.addAttribute("minDate", expense.date().withDayOfMonth(1));
        model.addAttribute("maxDate", expense.date().with(TemporalAdjusters.lastDayOfMonth()));

        // Again not nice to have to get all categories from DB each time
        model.addAttribute("categories", this.seeCategoriesUseCase.getAll());

        return new ModelAndView("expenseDialog", model.asMap());
    }

    @PutMapping
    public ModelAndView updateExpense(@Valid @ModelAttribute("request") UpdateExpenseRequestTO request,
                                BindingResult bindingResult,
                                HttpServletResponse response,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                               bindingResult.getAllErrors().stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .toList());
            response.setHeader("HX-Retarget", "#errors");
            return new ModelAndView("expenseDialog :: errors", model.asMap());
        }

        this.manageExpensesUseCase.updateExpense(request.toDomainModel());

        model.addAttribute("messages", List.of("The expense was updated"));
        response.setHeader("HX-Retarget", "#messages");

        return new ModelAndView("expenseDialog :: messages", model.asMap());
    }
}
