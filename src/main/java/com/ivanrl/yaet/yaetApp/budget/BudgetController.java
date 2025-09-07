package com.ivanrl.yaet.yaetApp.budget;

import com.ivanrl.yaet.yaetApp.UsedInTemplate;
import com.ivanrl.yaet.yaetApp.expenses.CategoryPO;
import com.ivanrl.yaet.yaetApp.expenses.CategoryRepository;
import com.ivanrl.yaet.yaetApp.expenses.ExpensePO;
import com.ivanrl.yaet.yaetApp.expenses.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private static final String TOTAL_ASSIGNED = "totalAssigned";
    private static final String CATEGORIES = "categories";

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @GetMapping
    public String currentBudget(Model model) {
        YearMonth current = YearMonth.now();

        var allCategories = getCategoriesInformation(current);

        setUpMonthNavigation(model, current);

        model.addAttribute(CATEGORIES, allCategories);
        if (allCategories.stream().allMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.addAttribute("emptyFutureBudget", true);
        }
        model.addAttribute(TOTAL_ASSIGNED,
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget";
    }

    @GetMapping("/{year}/{month}")
    public String budget(Model model,
                         @PathVariable("year") int year,
                         @PathVariable("month") int month) {
        YearMonth requestedMonth = YearMonth.of(year, month);

        var allCategories = getCategoriesInformation(requestedMonth);

        setUpMonthNavigation(model, requestedMonth);

        model.addAttribute(CATEGORIES, allCategories);
        if (requestedMonth.isAfter(YearMonth.now())
                && allCategories.stream().allMatch(c -> BigDecimal.ZERO.equals(c.amountAssigned()))) {
            model.addAttribute("emptyFutureBudget", true);
        }
        model.addAttribute(TOTAL_ASSIGNED,
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget";
    }

    private List<BudgetCategoryTO> getCategoriesInformation(YearMonth requestedMonth) {
        var categories = categoryRepository.findAll();
        Set<BudgetCategoryProjection> categoriesFromCurrentMonth = budgetCategoryRepository.findAllWithCategory(toDbInt(requestedMonth));
        var from = requestedMonth.atDay(1);
        var to = requestedMonth.atEndOfMonth();
        var allExpenses = expenseRepository.findAllByDateBetween(from, to);

        return categories.stream().map(c -> createCurrentMonthCategory(c, categoriesFromCurrentMonth, allExpenses)).toList();
    }

    // TODO Somewhat duplicated from ExpensesController, a new utility class should be created
    private static void setUpMonthNavigation(Model model, YearMonth current) {
        var previous = current.minusMonths(1);
        var next = current.plusMonths(1);

        model.addAttribute("previous", previous);
        model.addAttribute("currentMonth", "%s of %d".formatted(current.getMonth(), current.getYear()));
        model.addAttribute("next", next);
    }

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
                         .orElse(new BudgetCategoryTO(c.getName(), BigDecimal.ZERO, BigDecimal.ZERO, totalSpentInCategory));
    }

    @Transactional
    @PostMapping("/{categoryName}/assignAmount")
    public String setAmount(@PathVariable("categoryName") String categoryName,
                            @RequestParam("amount") BigDecimal amount,
                            Model model) {

        // TODO Missing check that it's the current month - Month should be part of the request
        YearMonth currentMonth = YearMonth.now();
        var po = new BudgetCategoryPO(this.categoryRepository.findByName(categoryName),
                                      toDbInt(currentMonth),
                                      amount);
        this.budgetCategoryRepository.save(po);

        var allCategories = getCategoriesInformation(currentMonth);

        model.addAttribute(CATEGORIES, allCategories);
        model.addAttribute(TOTAL_ASSIGNED,
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: budget-info";
    }

    @Transactional
    @PutMapping("/{id}/updateAmount")
    public String updateAmount(@PathVariable("id") int id,
                               @RequestParam("amount") BigDecimal amount,
                               Model model) {

        var po = budgetCategoryRepository.findById(id).orElseThrow();
        po.setAmountAssigned(amount);

        // TODO Missing check that it's the current month - Month should be part of the request
        var allCategories = getCategoriesInformation(YearMonth.now());

        model.addAttribute(CATEGORIES, allCategories);
        model.addAttribute(TOTAL_ASSIGNED,
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: budget-info";
    }
    
    // TODO Both of these should be extracted somewhere else or made into a proper hibernate converter
    private int toDbInt(YearMonth yearMonth) {
        return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
    }
    private YearMonth fromDBInt(int dbYearMonth) {
        return YearMonth.of(dbYearMonth / 100, dbYearMonth % 100);
    }
}


record BudgetCategoryTO(Integer id, String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {

    BudgetCategoryTO(String name, BigDecimal amountInherited, BigDecimal amountAssigned, BigDecimal amountSpent) {
        this(null, name, amountInherited, amountAssigned, amountSpent);
    }

    public static BudgetCategoryTO from(BudgetCategoryProjection projection, BigDecimal amountSpent) {
        return new BudgetCategoryTO(projection.getId(),
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
