package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.budget.SeeMonthBudgetUseCase;
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

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static com.ivanrl.yaet.web.BudgetController.addBudgetCategoriesInformationToModel;

@Controller
@RequestMapping("/expense-dialog")
@RequiredArgsConstructor
public class ExpenseDialogController {

    private final SeeExpensesUseCase seeExpensesUseCase;
    private final ManageExpensesUseCase manageExpensesUseCase;
    private final SeeCategoriesUseCase seeCategoriesUseCase;
    private final SeeMonthBudgetUseCase seeMonthBudgetUseCase;

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
    public List<ModelAndView> updateExpense(@Valid @ModelAttribute("request") UpdateExpenseRequestTO request,
                                BindingResult bindingResult,
                                HttpServletResponse response,
                                Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                               bindingResult.getAllErrors().stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .toList());
            return List.of(new ModelAndView("expenseDialog :: #result-information", model.asMap()));
        }


        this.manageExpensesUseCase.updateExpense(request.toDomainModel());

        // TODO Duplicated from budgetcontroller - Consider moving logic to reusable component objects
        YearMonth month = YearMonth.from(request.date());
        var monthlyBudget = BudgetMonthTO.from(this.seeMonthBudgetUseCase.seeMonthlyBudget(month));
        var allCategories = monthlyBudget.categories();


        BigDecimal totalIncome = monthlyBudget.totalIncome();
        BigDecimal totalSpent = allCategories.stream()
                                             .map(BudgetCategoryTO::amountSpent)
                                             .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("monthIncome", totalIncome);
        model.addAttribute("monthSpent", totalSpent);
        model.addAttribute("monthBalance", totalIncome.subtract(totalSpent));

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        model.addAttribute("messages", List.of("The expense was updated"));

        var categoryExpenses = CategoryExpenseTO.from(this.seeExpensesUseCase.getExpenses(request.categoryId(),
                                                                                          month));

        model.addAttribute("categoryName",categoryExpenses.category().name());
        model.addAttribute("categoryDescription", categoryExpenses.category().description());
        model.addAttribute("expenses", categoryExpenses.expenses());
        model.addAttribute("categoryTotal",
                           categoryExpenses.expenses().stream()
                                           .map(BasicExpenseTO::amount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add));


        return List.of(new ModelAndView("expenseDialog :: #result-information", model.asMap()),
                       new ModelAndView("budget :: #month-overview", model.asMap()),
                       new ModelAndView("budget :: budget-info", model.asMap()),
                       new ModelAndView("budget :: #category-expenses-content", model.asMap()));


    }
}
