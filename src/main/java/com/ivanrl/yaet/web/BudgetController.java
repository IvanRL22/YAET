package com.ivanrl.yaet.web;

import com.ivanrl.yaet.BadRequestException;
import com.ivanrl.yaet.domain.budget.CopyFromPreviousUseCase;
import com.ivanrl.yaet.domain.budget.SeeMonthBudgetUseCase;
import com.ivanrl.yaet.domain.budget.UpdateMonthBudgetUseCase;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import com.ivanrl.yaet.web.components.BudgetInformationComponent;
import com.ivanrl.yaet.web.components.CategoryExpensesComponent;
import com.ivanrl.yaet.web.components.MonthOverviewComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Optional;

@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final SeeMonthBudgetUseCase seeMonthBudgetUseCase;
    private final UpdateMonthBudgetUseCase updateMonthBudgetUseCase;
    private final SeeExpensesUseCase seeExpensesUseCase;
    private final CopyFromPreviousUseCase copyFromPreviousUseCase;


    @GetMapping(value = {"", "/{month}"})
    public ModelAndView budget(@PathVariable(required = false) YearMonth month,
                               @RequestParam(required = false) Integer categoryDetailsId,
                               Model model) {
        YearMonth now = YearMonth.now();
        var requestedMonth = Optional.ofNullable(month)
                                     .orElse(now);
        YearMonth lastAvailableMonth = now.plusMonths(1);

        if (requestedMonth.isAfter(lastAvailableMonth)) {
            throw new BadRequestException("You can only see up to the next month.");
        }

        var monthlyBudget = BudgetMonthTO.from(this.seeMonthBudgetUseCase.seeMonthlyBudget(requestedMonth));

        var allCategories = monthlyBudget.categories();

        var previous = requestedMonth.minusMonths(1);
        var next = requestedMonth.plusMonths(1);

        // Navigation
        model.addAttribute("previous", previous);
        model.addAttribute("currentMonthText", "%s of %d".formatted(requestedMonth.getMonth(), requestedMonth.getYear()));
        model.addAttribute("next", next);
        model.addAttribute("isLastMonth", requestedMonth.equals(lastAvailableMonth));

        BigDecimal totalIncome = monthlyBudget.totalIncome();
        BigDecimal totalSpent = allCategories.stream()
                                             .map(BudgetCategoryTO::amountSpent)
                                             .reduce(BigDecimal.ZERO, BigDecimal::add);
        var monthOverviewComponent = new MonthOverviewComponent(totalIncome,
                                                                totalSpent);
        monthOverviewComponent.attach(model);

        var budgetInformationComponent = new BudgetInformationComponent(requestedMonth,
                                                                        allCategories);
        budgetInformationComponent.attach(model);

        if(categoryDetailsId != null) {
            var categoryExpenses = CategoryExpenseTO.from(this.seeExpensesUseCase.getExpenses(categoryDetailsId, month));
            var component = new CategoryExpensesComponent(categoryExpenses);
            component.attach(model);
        }

        return new ModelAndView("budget", model.asMap());
    }

    @PostMapping("/{month}/{categoryId}/assignAmount")
    public ModelAndView setAmount(@PathVariable YearMonth month,
                                  @PathVariable int categoryId,
                                  @RequestParam(required = false) BigDecimal amount) {

        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        this.updateMonthBudgetUseCase.createMonthBudget(month, categoryId, newAmount);

        var allCategories = this.seeMonthBudgetUseCase.getBudgets(month)
                                                      .stream()
                                                      .map(BudgetCategoryTO::from)
                                                      .toList();

        var budgetInformationComponent = new BudgetInformationComponent(month,
                                                                        allCategories);

        return budgetInformationComponent.toModelAndView();
    }

    @PutMapping("/{month}/{categoryId}/updateAmount")
    public ModelAndView updateAmount(@PathVariable YearMonth month,
                                     @PathVariable int categoryId,
                                     @RequestParam(required = false) BigDecimal amount) {

        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        this.updateMonthBudgetUseCase.setBudgetAmount(month, categoryId, newAmount);

        var allCategories = this.seeMonthBudgetUseCase.getBudgets(month)
                                                      .stream()
                                                      .map(BudgetCategoryTO::from)
                                                      .toList();

        var budgetInformationComponent = new BudgetInformationComponent(month,
                                                                        allCategories);

        return budgetInformationComponent.toModelAndView();
    }

    @GetMapping("/{month}/{categoryId}")
    public ModelAndView getCategoryExpenses(@PathVariable YearMonth month,
                                            @PathVariable int categoryId) {

        var categoryExpenses = CategoryExpenseTO.from(this.seeExpensesUseCase.getExpenses(categoryId, month));
        var component = new CategoryExpensesComponent(categoryExpenses);

        return component.toModelAndView();
    }

    @PostMapping("/copy-from-previous")
    public ModelAndView generateMonthBudget(@RequestParam YearMonth month) {
        this.copyFromPreviousUseCase.copyFor(month);

        var categoryBudgets = this.seeMonthBudgetUseCase.getBudgets(month)
                                                        .stream()
                                                        .map(BudgetCategoryTO::from)
                                                        .toList();

        var budgetInformationComponent = new BudgetInformationComponent(month,
                                                                        categoryBudgets);

        return budgetInformationComponent.toModelAndView();
    }

}


