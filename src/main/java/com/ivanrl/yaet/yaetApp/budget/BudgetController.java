package com.ivanrl.yaet.yaetApp.budget;

import com.ivanrl.yaet.yaetApp.BadRequestException;
import com.ivanrl.yaet.yaetApp.domain.budget.BudgetCategoryDO;
import com.ivanrl.yaet.yaetApp.domain.budget.BudgetCategoryProjection;
import com.ivanrl.yaet.yaetApp.domain.budget.SeeMonthBudgetUseCase;
import com.ivanrl.yaet.yaetApp.expenses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetCategoryRepository budgetCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final SeeMonthBudgetUseCase seeMonthBudgetUseCase;


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

    private List<BudgetCategoryDO> getCategoriesInformation(YearMonth requestedMonth) {
        Set<BudgetCategoryProjection> categoriesFromCurrentMonth = budgetCategoryRepository.findAll(requestedMonth);
        var currentMonthExpenses = expenseRepository.findAllByDateBetween(requestedMonth.atDay(1),
                                                                          requestedMonth.atEndOfMonth());
        var currentMonthCategories = categoriesFromCurrentMonth.stream()
                                                               .map(c -> createCurrentMonthCategory(c, currentMonthExpenses)).toList();

        // Checking if all categories have a budget for the requested month
        // Having to check this for each time does not seem very performant
        var missingCategories = categoryRepository.findAll();
        missingCategories.removeIf(c -> categoriesFromCurrentMonth.stream()
                                                                  .anyMatch(bcp -> bcp.getCategoryId() == c.getId()));

        if (missingCategories.isEmpty()) {
            return currentMonthCategories;
        }

        // From here, we know there are categories without budget for the requested month

        var categoriesWithoutBudget = getCategoriesWithoutCurrentMonthBudget(requestedMonth, missingCategories);
        List<BudgetCategoryDO> allCategories = new ArrayList<>(currentMonthCategories.size() + missingCategories.size());
        allCategories.addAll(categoriesWithoutBudget);
        allCategories.addAll(currentMonthCategories);
        allCategories.sort(Comparator.comparing(BudgetCategoryDO::name));

        return allCategories;
    }

    private List<BudgetCategoryDO> getCategoriesWithoutCurrentMonthBudget(YearMonth requestedMonth,
                                                                          List<CategoryPO> missingCategories) {
        YearMonth previousMonth = requestedMonth.minusMonths(1);
        // What if some category still does not have a budget for the previous month?
        var categoriesWithoutMonthBudget = budgetCategoryRepository.findAll(previousMonth,
                                                                            missingCategories.stream()
                                                                                             .map(CategoryPO::getId)
                                                                                             .collect(Collectors.toSet()));

        var pastMonthExpenses = expenseRepository.findAllWithCategory(previousMonth.atDay(1),
                                                                      previousMonth.atEndOfMonth(),
                                                                      categoriesWithoutMonthBudget.stream()
                                                                                                                .map(BudgetCategoryProjection::getCategoryId)
                                                                                                                .collect(Collectors.toSet()));

        // Grouping the expenses by category now to avoid having to iterate through all of them when creating each TO
        var pastMonthExpensesByCategory = pastMonthExpenses.stream()
                                                           .collect(Collectors.groupingBy(e -> e.getCategory().getId()));

        return categoriesWithoutMonthBudget.stream()
                                           .map(cwmb -> createCurrentMonthCategoryWithoutBudget(cwmb, pastMonthExpensesByCategory.getOrDefault(cwmb.getCategoryId(),
                                                                                                                                               new ArrayList<>())))
                                           .toList();
    }

    // Should this be a static method on BudgetCategoryTO?
    // The expenses are expected to be just for the budgetCategory
    private BudgetCategoryDO createCurrentMonthCategoryWithoutBudget(BudgetCategoryProjection budgetCategory,
                                                                     List<ExpensePO> pastMonthExpenses) {
        var totalSpent = pastMonthExpenses.stream()
                                          .map(ExpensePO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BudgetCategoryDO(budgetCategory.getCategoryId(),
                                    budgetCategory.getName(),
                                    budgetCategory.getAmountInherited().add(budgetCategory.getAmountAssigned()).subtract(totalSpent),
                                    BigDecimal.ZERO,
                                    BigDecimal.ZERO);
    }

    // TODO Somewhat duplicated from ExpensesController, a new utility class should be created
    private BudgetCategoryDO createCurrentMonthCategory(BudgetCategoryProjection c,
                                                        List<ExpensePO> expenses) {
        var totalSpentInCategory = expenses.stream()
                                           .filter(e -> e.getCategory().getName().equals(c.getName()))
                                           .map(ExpensePO::getAmount)
                                           .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BudgetCategoryDO.from(c, totalSpentInCategory);
    }

    @Transactional
    @PostMapping("/{month}/{categoryId}/assignAmount")
    public String setAmount(@PathVariable YearMonth month,
                            @PathVariable int categoryId,
                            @RequestParam(required = false) BigDecimal amount,
                            Model model) {

        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);
        YearMonth previousMonth = month.minusMonths(1);
        var previousBudgetCategory = this.budgetCategoryRepository.findByCategoryIdAndMonth(categoryId,
                                                                                            previousMonth);

        BigDecimal balanceFromLastMonth = previousBudgetCategory.map(balance -> getMonthBalance(balance, previousMonth))
                                                                  .orElse(BigDecimal.ZERO);

        var po = new BudgetCategoryPO(this.categoryRepository.getReferenceById(categoryId),
                                      month,
                                      balanceFromLastMonth,
                                      newAmount);
        this.budgetCategoryRepository.save(po);

        var allCategories = getCategoriesInformation(month);

        addBudgetCategoriesInformationToModel(model, month, allCategories);

        return "budget :: budget-info";
    }

    private BigDecimal getMonthBalance(BudgetCategoryPO previousBudgetCategory, YearMonth month) {
        var expenses = this.expenseRepository.findAllByCategoryAndDateBetween(previousBudgetCategory.getCategory().getId(),
                                                                              month.atDay(1),
                                                                              month.atEndOfMonth());

        BigDecimal totalMonthExpenses = expenses.stream()
                                                .map(ExpensePO::getAmount)
                                                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return previousBudgetCategory.getAmountInherited()
                                     .add(previousBudgetCategory.getAmountAssigned())
                                     .subtract(totalMonthExpenses);
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

    @Transactional
    @PutMapping("/{month}/{categoryId}/updateAmount")
    public String updateAmount(@PathVariable YearMonth month,
                               @PathVariable int categoryId,
                               @RequestParam(required = false) BigDecimal amount,
                               Model model) {
        var newAmount = Optional.ofNullable(amount)
                                .orElse(BigDecimal.ZERO);

        var po = budgetCategoryRepository.findByCategoryIdAndMonth(categoryId, month)
                                         .orElseThrow(); // TODO Handle - Need to decide how this should look in the frontend
        var differenceInAmountAssigned = newAmount.subtract(po.getAmountAssigned());
        po.setAmountAssigned(newAmount);

        // Update future budgets (at most 1 for now)
        budgetCategoryRepository.updateBudgetCategoryAmount(categoryId,
                                                            month,
                                                            differenceInAmountAssigned);

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

    @PostMapping("/copy-from-previous")
    public String generateMonthBudget(@RequestParam YearMonth month,
                                      Model model) {
        var previous = month.minusMonths(1);
        var allCategories = this.categoryRepository.findAll();
        var currentMonthBudgets = this.budgetCategoryRepository.findAll(month);

        List<CategoryPO> categoriesToGenerate;
        if (currentMonthBudgets.isEmpty()) {
            categoriesToGenerate = allCategories;
        } else {
            var categoriesWithBudget = currentMonthBudgets.stream()
                                                          .map(BudgetCategoryProjection::getCategoryId)
                                                          .collect(Collectors.toSet());
            categoriesToGenerate = allCategories.stream()
                                                .filter(c -> !categoriesWithBudget.contains(c.getId()))
                                                .toList();
        }

        Set<BudgetCategoryProjection> budgetCategoryProjections = this.budgetCategoryRepository.findAll(previous,
                                                                                                        categoriesToGenerate.stream().map(CategoryPO::getId).collect(Collectors.toSet()));

        var expenses = this.expenseRepository.findAllWithCategory(previous.atDay(1),
                                                                  previous.atEndOfMonth(),
                                                                  budgetCategoryProjections.stream()
                                                                                           .map(BudgetCategoryProjection::getCategoryId)
                                                                                           .collect(Collectors.toSet()));

        var expensesByCategory = expenses.stream().collect(Collectors.groupingBy(e -> e.getCategory().getId()));

        var result = budgetCategoryProjections.stream()
                                              .map(bc -> createBudgetCategory(month, bc, expensesByCategory.getOrDefault(bc.getCategoryId(),
                                                                                                                         Collections.emptyList())))
                                              .toList();

        this.budgetCategoryRepository.saveAll(result);

        addBudgetCategoriesInformationToModel(model,
                                              month,
                                              getCategoriesInformation(month));

        return "budget :: budget-info";

    }

    private BudgetCategoryPO createBudgetCategory(YearMonth month,
                                                  BudgetCategoryProjection bc,
                                                  List<ExpensePO> expensePOS) {
        var totalSpent = expensePOS.stream()
                                   .map(ExpensePO::getAmount)
                                   .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new BudgetCategoryPO(this.categoryRepository.getReferenceById(bc.getCategoryId()),
                                    month,
                                    bc.getAmountInherited()
                                      .add(bc.getAmountAssigned())
                                      .subtract(totalSpent),
                                    bc.getAmountAssigned());
    }

}


