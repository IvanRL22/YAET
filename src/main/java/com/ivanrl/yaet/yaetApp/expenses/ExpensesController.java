package com.ivanrl.yaet.yaetApp.expenses;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private final ExpenseRepository repository;
    private final IncomeRepository incomeRepository;

    @GetMapping
    public String getCurrentMonthExpenses(Model model) {
        long numOfMonths = 3; // In the future this should come from the user's preferences
        var now = LocalDate.now();
        var to = now.with(TemporalAdjusters.lastDayOfMonth());
        var from = to.minusMonths(numOfMonths - 1).withDayOfMonth(1);

        // Navigation
        setUpMonthNavigation(model, numOfMonths, now);

        var allExpenses = this.repository.findAllByDateBetween(from, to);

        var allMonths = buildMonths(numOfMonths, from, allExpenses);

        model.addAttribute("allMonths", allMonths);

        return "expenses";
    }

    @GetMapping("/{year}/{month}")
    public String getAllExpenses(Model model,
                                 @PathVariable("year") int year,
                                 @PathVariable("month") int month,
                                 @RequestParam(name = "numOfMonths", required = false, defaultValue = "1") long numOfMonths) {
        var to = LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth());
        var from = to.minusMonths(numOfMonths - 1).withDayOfMonth(1);

        setUpMonthNavigation(model, numOfMonths, to);

        var allExpenses = this.repository.findAllByDateBetween(from, to);

        var allMonths = buildMonths(numOfMonths, from, allExpenses);

        model.addAttribute("allMonths", allMonths);

        return "expenses";
    }

    private List<MonthOverview> buildMonths(long numOfMonths, LocalDate from, List<ExpensePO> allExpenses) {
        return Stream.iterate(YearMonth.from(from), yearMonth -> yearMonth.plusMonths(1))
                .limit(numOfMonths)
                .map(yearMonth -> {

                    var monthExpenses = allExpenses.stream()
                            .filter(expensePO -> yearMonth.equals(YearMonth.from(expensePO.getDate())))
                            .collect(Collectors.groupingBy(ExpensePO::getCategory))
                            .entrySet().stream()
                            .map(buildCategoryExpense())
                            .sorted((a, b) -> b.totalAmount().compareTo(a.totalAmount()))
                            .toList();
                    var monthFrom = LocalDate.of(yearMonth.getYear(), yearMonth.getMonthValue(), 1);

                    var monthTotalIncome = this.incomeRepository.getTotalIncome(monthFrom, monthFrom.with(TemporalAdjusters.lastDayOfMonth()));

                    return MonthOverview.from(yearMonth, monthExpenses, monthTotalIncome);
                })
                .toList();
    }

    private static Function<Map.Entry<CategoryPO, List<ExpensePO>>, CategoryExpense> buildCategoryExpense() {
        return entry -> new CategoryExpense(
                entry.getKey().getName(),
                // Sums all amounts
                entry.getValue()
                        .stream()
                        .map(ExpensePO::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                // Maps POs to TO
                entry.getValue()
                        .stream()
                        .map(e -> new Expense(e.getId(), e.getCategory().getName(), e.getPayee(), e.getAmount(), e.getDate()))
                        .sorted(Comparator.comparing(Expense::date))
                        .toList());
    }

    private static void setUpMonthNavigation(Model model, long numOfMonths, LocalDate to) {
        var current = YearMonth.from(to);
        var previous = current.minusMonths(1);
        var next = current.plusMonths(1);

        model.addAttribute("previous", previous);
        model.addAttribute("current", current);
        model.addAttribute("numOfMonths", numOfMonths);
        model.addAttribute("next", next);
    }
}

record Category(Integer id, String name, String description) {

    public static Category from(CategoryPO po) {
        return new Category(po.getId(), po.getName(), po.getDescription());
    }
}
record Expense(int id, String category, String payee, BigDecimal amount, LocalDate date) {}
record CategoryExpense(String category, BigDecimal totalAmount, List<Expense> expenses) {}
record MonthOverview(YearMonth month,
                     List<CategoryExpense> categories,
                     BigDecimal totalExpense,
                     BigDecimal totalIncome,
                     BigDecimal balance) {

    public static MonthOverview from(YearMonth month, List<CategoryExpense> categories, BigDecimal totalIncome) {
        var totalExpense = categories.stream()
                .map(CategoryExpense::totalAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        return new MonthOverview(month, categories, totalExpense, totalIncome, totalIncome.subtract(totalExpense));
    }

    @SuppressWarnings("unused")
    public String getMonthText() {
        return "%s of %d".formatted(month.getMonth(), month.getYear());
    }
}
