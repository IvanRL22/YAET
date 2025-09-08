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
import java.util.Objects;
import java.util.Set;

@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private static final String TOTAL_ASSIGNED = "totalAssigned";
    private static final String CATEGORIES = "budgetCategories";

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;

    @GetMapping
    public String currentBudget(Model model) {
        YearMonth current = YearMonth.now();

        var allCategories = getCategoriesInformation(current);

        setUpMonthNavigation(model, current);

        model.addAttribute("currentMonth", toDbInt(current));
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

    @GetMapping("/{currentMonth}")
    public String budget(@PathVariable("currentMonth") int month,
                         Model model) {
        YearMonth requestedMonth = fromDBInt(month);

        var allCategories = getCategoriesInformation(requestedMonth);

        setUpMonthNavigation(model, requestedMonth);
        model.addAttribute("currentMonth", month);

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

        model.addAttribute("previous", toDbInt(previous));
        model.addAttribute("currentMonthText", "%s of %d".formatted(current.getMonth(), current.getYear()));
        model.addAttribute("next", toDbInt(next));
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
                         .orElse(new BudgetCategoryTO(c.getId(), c.getName(), BigDecimal.ZERO, BigDecimal.ZERO, totalSpentInCategory));
    }

    @Transactional
    @PostMapping("/{currentMonth}/{categoryId}/assignAmount")
    public String setAmount(@PathVariable("currentMonth") int currentMonth,
                            @PathVariable("categoryId") int categoryId,
                            @RequestParam("amount") BigDecimal amount,
                            Model model) {

        var po = new BudgetCategoryPO(this.categoryRepository.getReferenceById(categoryId),
                                      currentMonth,
                                      amount);
        this.budgetCategoryRepository.save(po);

        var month = fromDBInt(currentMonth);
        var allCategories = getCategoriesInformation(month);

        model.addAttribute("currentMonth", toDbInt(month));
        model.addAttribute(CATEGORIES, allCategories);
        model.addAttribute(TOTAL_ASSIGNED,
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: budget-info";
    }

    @Transactional
    @PutMapping("/{currentMonth}/{categoryId}/updateAmount")
    public String updateAmount(@PathVariable("currentMonth") int currentMonth,
                               @PathVariable("categoryId") int categoryId,
                               @RequestParam("amount") BigDecimal amount,
                               Model model) {

        var po = budgetCategoryRepository.findByCategoryIdAndMonth(categoryId, currentMonth)
                                         .orElseThrow(); // TODO Handle - Need to decide how this should look in the frontend
        po.setAmountAssigned(amount);

        var allCategories = getCategoriesInformation(fromDBInt(currentMonth));

        model.addAttribute(CATEGORIES, allCategories);
        model.addAttribute(TOTAL_ASSIGNED,
                           allCategories.stream()
                                        .map(BudgetCategoryTO::amountAssigned)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: budget-info";
    }

    @GetMapping("/{currentMonth}/{categoryId}")
    public String getCategoryExpenses(@PathVariable int currentMonth,
                                      @PathVariable int categoryId,
                                      Model model) {
        var requestedMonth = fromDBInt(currentMonth);
        var from = requestedMonth.atDay(1);
        var to = requestedMonth.atEndOfMonth();
        var expenses = expenseRepository.findAllByCategoryAndDateBetween(categoryId, from, to);

        model.addAttribute("categoryName",
                           categoryRepository.findById(categoryId)
                                             .map(CategoryPO::getName)
                                             .orElse(" - "));
        model.addAttribute("expenses", expenses.stream()
                .map(Expense::from));
        model.addAttribute("categoryTotal",
                           expenses.stream()
                                   .map(ExpensePO::getAmount)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add));

        return "budget :: expenses";
    }
    
    // TODO Both of these should be extracted somewhere else or made into a proper hibernate converter
    private static int toDbInt(YearMonth yearMonth) {
        return yearMonth.getYear() * 100 + yearMonth.getMonthValue();
    }
    private static YearMonth fromDBInt(int dbYearMonth) {
        return YearMonth.of(dbYearMonth / 100, dbYearMonth % 100);
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
