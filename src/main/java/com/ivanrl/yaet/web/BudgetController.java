package com.ivanrl.yaet.web;

import com.ivanrl.yaet.BadRequestException;
import com.ivanrl.yaet.domain.budget.BudgetCategoryDO;
import com.ivanrl.yaet.domain.budget.CopyFromPreviousUseCase;
import com.ivanrl.yaet.domain.budget.SeeMonthBudgetUseCase;
import com.ivanrl.yaet.domain.budget.UpdateMonthBudgetUseCase;
import com.ivanrl.yaet.domain.expense.ExpenseDO;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String budget(@PathVariable(required = false) YearMonth month,
                         Model model) {
        YearMonth now = YearMonth.now();
        var requestedMonth = Optional.ofNullable(month)
                                     .orElse(now);
        YearMonth lastAvailableMonth = now.plusMonths(1);

        if (requestedMonth.isAfter(lastAvailableMonth)) {
            throw new BadRequestException("You can only see up to the next month.");
        }

        var monthlyBudget = this.seeMonthBudgetUseCase.seeMonthlyBudget(requestedMonth);

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
                                             .map(BudgetCategoryDO::amountSpent)
                                             .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("monthIncome", totalIncome);
        model.addAttribute("monthSpent", totalSpent);
        model.addAttribute("monthBalance", totalIncome.subtract(totalSpent));

        // Budget information
        addBudgetCategoriesInformationToModel(model, requestedMonth, allCategories);

        return "budget";
    }

    @PostMapping("/{month}/{categoryId}/assignAmount")
    public String setAmount(@PathVariable YearMonth month,
                            @PathVariable int categoryId,
                            @RequestParam(required = false) BigDecimal amount,
                            Model model) {

        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        this.updateMonthBudgetUseCase.createMonthBudget(month, categoryId, newAmount);

        var allCategories = this.seeMonthBudgetUseCase.getBudgets(month);

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return "budget :: budget-info";
    }

    @PutMapping("/{month}/{categoryId}/updateAmount")
    public String updateAmount(@PathVariable YearMonth month,
                               @PathVariable int categoryId,
                               @RequestParam(required = false) BigDecimal amount,
                               Model model) {
        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        this.updateMonthBudgetUseCase.setBudgetAmount(month, categoryId, newAmount);

        var allCategories = this.seeMonthBudgetUseCase.getBudgets(month);

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return "budget :: budget-info";
    }

    @GetMapping("/{month}/{categoryId}")
    public String getCategoryExpenses(@PathVariable YearMonth month,
                                      @PathVariable int categoryId,
                                      Model model) {

        var categoryExpenses = this.seeExpensesUseCase.getExpenses(categoryId, month);

        model.addAttribute("categoryName",categoryExpenses.category().name());
        model.addAttribute("categoryDescription", categoryExpenses.category().description());
        model.addAttribute("expenses", categoryExpenses.expenses());
        model.addAttribute("categoryTotal",
                           categoryExpenses.expenses().stream()
                                           .map(ExpenseDO::amount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: expenses";
    }

    @PostMapping("/copy-from-previous")
    public String generateMonthBudget(@RequestParam YearMonth month,
                                      Model model) {
        this.copyFromPreviousUseCase.copyFor(month);

        var categoryBudgets = this.seeMonthBudgetUseCase.getBudgets(month);

        addBudgetCategoriesInformationToModel(model,
                                              month,
                                              categoryBudgets);

        return "budget :: budget-info";
    }

    private static void addBudgetCategoriesInformationToModel(Model model, YearMonth month, List<BudgetCategoryDO> allCategories) {
        model.addAttribute("currentMonth", month);
        model.addAttribute("budgetCategories", allCategories);
        model.addAttribute("totalAssigned",
                           allCategories.stream()
                                        .map(BudgetCategoryDO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("totalBalance",
                           allCategories.stream()
                                        .map(BudgetCategoryDO::getTotalAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Small utility to copy budget from previous month
        if (month.isAfter(YearMonth.now())
                && allCategories.stream().anyMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.addAttribute("missingBudgets", true);
        }
    }

}


