package com.ivanrl.yaet.yaetApp.budget;

import com.ivanrl.yaet.yaetApp.UsedInTemplate;
import com.ivanrl.yaet.yaetApp.expenses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;


    @GetMapping(value = {"", "/{month}"})
    public String budget(@PathVariable(required = false) YearMonth month,
                         Model model) {
        var requestedMonth = Optional.ofNullable(month)
                                     .orElse(YearMonth.now());
        var allCategories = getCategoriesInformation(requestedMonth);

        var previous = requestedMonth.minusMonths(1);
        var next = requestedMonth.plusMonths(1);

        // Small utility to copy budget from previous month
        if (requestedMonth.isAfter(YearMonth.now())
                && allCategories.stream().allMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.addAttribute("emptyFutureBudget", true);
        }

        // Navigation
        model.addAttribute("previous", previous);
        model.addAttribute("currentMonthText", "%s of %d".formatted(requestedMonth.getMonth(), requestedMonth.getYear()));
        model.addAttribute("next", next);

        // Budget information
        addBudgetCategoriesInformationToModel(model, requestedMonth, allCategories);

        return "budget";
    }

    private List<BudgetCategoryTO> getCategoriesInformation(YearMonth requestedMonth) {
        var categories = categoryRepository.findAll();
        Set<BudgetCategoryProjection> categoriesFromCurrentMonth = budgetCategoryRepository.findAllWithCategory(requestedMonth);
        var from = requestedMonth.atDay(1);
        var to = requestedMonth.atEndOfMonth();
        var allExpenses = expenseRepository.findAllByDateBetween(from, to);

        return categories.stream().map(c -> createCurrentMonthCategory(c, categoriesFromCurrentMonth, allExpenses)).toList();
    }

    // TODO Somewhat duplicated from ExpensesController, a new utility class should be created
    private BudgetCategoryTO createCurrentMonthCategory(CategoryPO c,
                                                        Set<BudgetCategoryProjection> categories,
                                                        List<ExpensePO> expenses) {
        var totalSpentInCategory = expenses.stream()
                                           .filter(e -> e.getCategory().getName().equals(c.getName()))
                                           .map(ExpensePO::getAmount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add);
        return categories.stream()
                         .filter(category -> category.getName().equals(c.getName()))
                         .findFirst()
                         .map(bcp -> BudgetCategoryTO.from(bcp, totalSpentInCategory))
                         .orElse(new BudgetCategoryTO(c.getId(), c.getName(), BigDecimal.ZERO, BigDecimal.ZERO, totalSpentInCategory));
    }

    @Transactional
    @PostMapping("/{month}/{categoryId}/assignAmount")
    public String setAmount(@PathVariable YearMonth month,
                            @PathVariable int categoryId,
                            @RequestParam BigDecimal amount,
                            Model model) {

        var po = new BudgetCategoryPO(this.categoryRepository.getReferenceById(categoryId),
                                      month,
                                      amount);
        this.budgetCategoryRepository.save(po);

        var allCategories = getCategoriesInformation(month);

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return "budget :: budget-info";
    }

    private static void addBudgetCategoriesInformationToModel(Model model, YearMonth month, List<BudgetCategoryTO> allCategories) {
        model.addAttribute("currentMonth", month);
        model.addAttribute("budgetCategories", allCategories);
        model.addAttribute("totalAssigned",
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    @Transactional
    @PutMapping("/{month}/{categoryId}/updateAmount")
    public String updateAmount(@PathVariable YearMonth month,
                               @PathVariable int categoryId,
                               @RequestParam BigDecimal amount,
                               Model model) {

        var po = budgetCategoryRepository.findByCategoryIdAndMonth(categoryId, month)
                                         .orElseThrow(); // TODO Handle - Need to decide how this should look in the frontend
        po.setAmountAssigned(amount);

        var allCategories = getCategoriesInformation(month);

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return "budget :: budget-info";
    }

    @GetMapping("/{month}/{categoryId}")
    public String getCategoryExpenses(@PathVariable YearMonth month,
                                      @PathVariable int categoryId,
                                      Model model) {
        var from = month.atDay(1);
        var to = month.atEndOfMonth();
        var expenses = expenseRepository.findAllByCategoryAndDateBetween(categoryId, from, to);

        CategoryPO categoryPO = categoryRepository.findById(categoryId).orElseThrow();
        model.addAttribute("categoryName", categoryPO.getName());
        model.addAttribute("categoryDescription", categoryPO.getDescription());
        model.addAttribute("expenses", expenses.stream()
                                               .map(Expense::from)
                                               .toList());
        model.addAttribute("categoryTotal",
                           expenses.stream()
                                   .map(ExpensePO::getAmount)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: expenses";
    }

}


record BudgetCategoryTO(Integer id, Integer categoryId, String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {

    BudgetCategoryTO(Integer categoryId, String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {
        this(null, categoryId, name, amountInherited, amountAssigned, amountSpent);
    }

    public static BudgetCategoryTO from(BudgetCategoryProjection projection, BigDecimal amountSpent) {
        return new BudgetCategoryTO(projection.getId(),
                                    projection.getCategoryId(),
                                    projection.getName(),
                                    projection.getAmountInherited() != null ? projection.getAmountInherited() : BigDecimal.ZERO,
                                    projection.getAmountAssigned() != null ? projection.getAmountAssigned() : BigDecimal.ZERO,
                                    amountSpent);
    }

    @UsedInTemplate
    public BigDecimal getTotalAmount() {
        return amountAssigned.add(amountInherited).subtract(amountSpent);
    }
}
