package com.ivanrl.yaet.web;

import com.ivanrl.yaet.BadRequestException;
import com.ivanrl.yaet.domain.budget.CopyFromPreviousUseCase;
import com.ivanrl.yaet.domain.budget.SeeMonthBudgetUseCase;
import com.ivanrl.yaet.domain.budget.UpdateMonthBudgetUseCase;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import com.ivanrl.yaet.web.components.CategoryExpensesComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
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
        model.addAttribute("monthIncome", totalIncome);
        model.addAttribute("monthSpent", totalSpent);
        model.addAttribute("monthBalance", totalIncome.subtract(totalSpent));

        // Budget information
        addBudgetCategoriesInformationToModel(model, requestedMonth, allCategories);

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
                                  @RequestParam(required = false) BigDecimal amount,
                                  Model model) {

        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        this.updateMonthBudgetUseCase.createMonthBudget(month, categoryId, newAmount);

        var allCategories = this.seeMonthBudgetUseCase.getBudgets(month)
                                                      .stream()
                                                      .map(BudgetCategoryTO::from)
                                                      .toList();

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return new ModelAndView("budget :: budget-info", model.asMap());
    }

    @PutMapping("/{month}/{categoryId}/updateAmount")
    public ModelAndView updateAmount(@PathVariable YearMonth month,
                                     @PathVariable int categoryId,
                                     @RequestParam(required = false) BigDecimal amount,
                                     Model model) {
        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        this.updateMonthBudgetUseCase.setBudgetAmount(month, categoryId, newAmount);

        var allCategories = this.seeMonthBudgetUseCase.getBudgets(month)
                                                      .stream()
                                                      .map(BudgetCategoryTO::from)
                                                      .toList();

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return new ModelAndView("budget :: budget-info", model.asMap());
    }

    @GetMapping("/{month}/{categoryId}")
    public ModelAndView getCategoryExpenses(@PathVariable YearMonth month,
                                      @PathVariable int categoryId,
                                      Model model) {

        var categoryExpenses = CategoryExpenseTO.from(this.seeExpensesUseCase.getExpenses(categoryId, month));

        model.addAttribute("categoryName",categoryExpenses.category().name());
        model.addAttribute("categoryDescription", categoryExpenses.category().description());
        model.addAttribute("expenses", categoryExpenses.expenses());
        model.addAttribute("categoryTotal",
                           categoryExpenses.expenses().stream()
                                           .map(BasicExpenseTO::amount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add));

        return new ModelAndView("budget :: expenses", model.asMap());
    }

    @PostMapping("/copy-from-previous")
    public ModelAndView generateMonthBudget(@RequestParam YearMonth month,
                                      Model model) {
        this.copyFromPreviousUseCase.copyFor(month);

        var categoryBudgets = this.seeMonthBudgetUseCase.getBudgets(month)
                                                        .stream()
                                                        .map(BudgetCategoryTO::from)
                                                        .toList();

        addBudgetCategoriesInformationToModel(model,
                                              month,
                                              categoryBudgets);

        return new ModelAndView("budget :: budget-info", model.asMap());
    }

    // TODO Consider moving this to reusable component
    public static void addBudgetCategoriesInformationToModel(Model model, YearMonth month, List<BudgetCategoryTO> allCategories) {
        model.addAttribute("currentMonth", month);
        model.addAttribute("budgetCategories", allCategories);
        model.addAttribute("totalAssigned",
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("totalBalance",
                           allCategories.stream()
                                        .map(BudgetCategoryTO::getTotalAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Small utility to copy budget from previous month
        if (month.isAfter(YearMonth.now())
                && allCategories.stream().anyMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.addAttribute("missingBudgets", true);
        }
    }

}


