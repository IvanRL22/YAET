package com.ivanrl.yaet.yaetApp.web;

import com.ivanrl.yaet.yaetApp.domain.budget.persistence.BudgetCategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.category.persistence.CategoryRepository;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpensePO;
import com.ivanrl.yaet.yaetApp.domain.expense.persistence.ExpenseRepository;
import com.ivanrl.yaet.yaetApp.domain.income.persistence.IncomePO;
import com.ivanrl.yaet.yaetApp.domain.income.persistence.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequestMapping("/new")
@RequiredArgsConstructor
public class AddMovementController {

    private final ExpenseRepository repository;
    private final IncomeRepository incomeRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetCategoryRepository budgetCategoryRepository;

    @GetMapping
    public String baseView(Model model) {
        model.addAttribute("categories", this.categoryRepository.findAll().stream().map(Category::from).toList());
        model.addAttribute("expense", new NewExpense(null, Strings.EMPTY, null, LocalDate.now(), Strings.EMPTY));

        model.addAttribute("lastExpenses", this.repository.findLastExpenses(Pageable.ofSize(10)));

        model.addAttribute("income", new NewIncome(Strings.EMPTY, null, LocalDate.now()));

        model.addAttribute("lastIncomes", this.incomeRepository.findTop10ByOrderByDateDesc());


        return "newExpense";
    }

    @Transactional
    @PostMapping("/expense")
    public String addNewExpense(Model model,
                                @RequestBody NewExpense newExpense) {

        ExpensePO newPO = new ExpensePO(categoryRepository.getReferenceById(newExpense.categoryId()), newExpense.payee(), newExpense.amount(), newExpense.date(), newExpense.comment());
        this.repository.saveAndFlush(newPO); // Need to immediately persist to db

        // Extend expense to future budgets
        this.budgetCategoryRepository.updateBudgetCategoryAmount(newExpense.categoryId(),
                                                                 YearMonth.from(newExpense.date()),
                                                                 newExpense.amount().negate());

        model.addAttribute("message", "A new expense for %s€ was successfully added.".formatted(newPO.getAmount()));
        model.addAttribute("expense", newExpense);

        model.addAttribute("lastExpenses", this.repository.findLastExpenses(Pageable.ofSize(10)));

        // Not great to have to call this everytime
        model.addAttribute("categories", this.categoryRepository.findAll().stream().map(Category::from).toList());

        return "newExpense :: addExpense";
    }

    @GetMapping("/lastExpenses")
    public String getExpensesPage(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {

        model.addAttribute("lastExpenses", this.repository.findLastExpenses(Pageable.ofSize(size).withPage(page)));

        return "newExpense :: lastExpenses";
    }

    @PostMapping("/income")
    public String addNewIncome(Model model,
                               @RequestBody NewIncome newIncome) {
        IncomePO newPO = new IncomePO(newIncome.payer(), newIncome.amount(), newIncome.date());
        this.incomeRepository.save(newPO);

        model.addAttribute("incomeMessage", "A new income for %s€ was successfully added.".formatted(newPO.getAmount()));
        model.addAttribute("income", newIncome);
        model.addAttribute("lastIncomes", this.incomeRepository.findTop10ByOrderByDateDesc());

        return "newExpense :: addIncome";
    }
}

record NewExpense(Integer categoryId, String payee, BigDecimal amount, LocalDate date, String comment) {}
record NewIncome(String payer, BigDecimal amount, LocalDate date) {}
