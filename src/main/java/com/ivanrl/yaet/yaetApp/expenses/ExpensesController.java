package com.ivanrl.yaet.yaetApp.expenses;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpensesController {

    private final ExpenseRepository repository;
    private final IncomeRepository incomeRepository;

    @GetMapping
    public String getCurrentMonthExpenses(Model model) {
        var now = LocalDate.now();
        var from = LocalDate.now().withDayOfMonth(1);
        var to = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        // Navigation
        var previous = YearMonth.of(now.getYear(), now.getMonth()).minusMonths(1);
        var next = YearMonth.of(now.getYear(), now.getMonth()).plusMonths(1);

        model.addAttribute("previous", previous);
        model.addAttribute("next", next);

        // Month information
        var categories = getExpenses();
        var totalIncome = incomeRepository.getTotalIncome(from, to);
        var totalExpense = categories
                .stream()
                .map(CategoryExpense::totalAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        var monthOverview = new MonthOverview(
                YearMonth.of(now.getYear(), now.getMonth()),
                categories,
                totalExpense,
                totalIncome,
                totalExpense.subtract(totalIncome)
                );

        model.addAttribute("allMonths", List.of(monthOverview));

        return "expenses";
    }

    @GetMapping("/{year}/{month}")
    public String getAllExpenses(Model model,
                                 @PathVariable("year") int year,
                                 @PathVariable("month") int month) {
        var from = LocalDate.of(year, month, 1);
        var to = from.with(TemporalAdjusters.lastDayOfMonth());

        // Navigation
        var previous = YearMonth.of(from.getYear(), from.getMonth()).minusMonths(1);
        var next = YearMonth.of(from.getYear(), from.getMonth()).plusMonths(1);

        model.addAttribute("previous", previous);
        model.addAttribute("next", next);

        // Month information
        var categories = getExpenses(from, to);
        var totalIncome = incomeRepository.getTotalIncome(from, to);
        var totalExpense = categories
                .stream()
                .map(CategoryExpense::totalAmount)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);

        var monthOverview = new MonthOverview(
                YearMonth.of(from.getYear(), from.getMonth()),
                categories,
                totalExpense,
                totalIncome,
                totalExpense.subtract(totalIncome)
        );

        model.addAttribute("allMonths", List.of(monthOverview));

        return "expenses";
    }

    @GetMapping("/new")
    public String newExpense(Model model) {
        model.addAttribute("expense", new NewExpense(Strings.EMPTY, Strings.EMPTY, null, LocalDate.now()));
        model.addAttribute("income", new NewIncome(Strings.EMPTY, null, LocalDate.now()));

        return "newExpense";
    }

    @PostMapping("/new")
    public String addNewExpense(Model model,
                                @RequestBody NewExpense newExpense) {

        ExpensePO newPO = new ExpensePO(newExpense.category(), newExpense.payee(), newExpense.amount(), newExpense.date());
        this.repository.save(newPO);

        model.addAttribute("message", "A new expense for %s€ was successfully added.".formatted(newPO.getAmount()));
        model.addAttribute("expense", newExpense);

        return "newExpense :: expenseForm";
    }

    @PostMapping("/income/new")
    public String addNewIncome(Model model,
                               @RequestBody NewIncome newIncome) {
        IncomePO newPO = new IncomePO(newIncome.payer(), newIncome.amount(), newIncome.date());
        this.incomeRepository.save(newPO);

        model.addAttribute("incomeMessage", "A new income for %s€ was successfully added.".formatted(newPO.getAmount()));
        return "newExpense :: incomeForm";
    }

    private List<CategoryExpense> getExpenses() {
        var from = LocalDate.now().withDayOfMonth(1);
        var to = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

        return getExpenses(from, to);
    }

    private List<CategoryExpense> getExpenses(LocalDate from, LocalDate to) {
        return this.repository.findAllByDateBetween(from, to, ExpenseRepository.defaultSorting)
                .stream()
                .collect(Collectors.groupingBy(ExpensePO::getCategory))
                .entrySet().stream()
                .map(entry -> new CategoryExpense(
                        entry.getKey(),
                        // Sums all amounts
                        entry.getValue()
                                .stream()
                                .map(ExpensePO::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        // Maps POs to TO
                        entry.getValue()
                                .stream()
                                .map(e -> new SimpleExpense(e.getPayee(), e.getAmount(), e.getDate()))
                                .sorted(Comparator.comparing(SimpleExpense::date))
                                .toList()))
                .sorted((a, b) -> b.totalAmount().compareTo(a.totalAmount()))
                .toList();
    }
}

record NewExpense(String category, String payee, BigDecimal amount, LocalDate date) {}
record NewIncome(String payer, BigDecimal amount, LocalDate date) {}
record SimpleExpense(String payee, BigDecimal amount, LocalDate date) {}
record CategoryExpense(String category, BigDecimal totalAmount, List<SimpleExpense> expenses) {}
record MonthOverview(YearMonth month,
                     List<CategoryExpense> categories,
                     BigDecimal totalExpense,
                     BigDecimal totalIncome,
                     BigDecimal balance) {

    @SuppressWarnings("unused")
    public String getMonthText() {
        return "%s of %d".formatted(month.getMonth(), month.getYear());
    }
}
