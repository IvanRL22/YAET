package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.domain.SeeCategoriesUseCase;
import com.ivanrl.yaet.domain.expense.ManageExpensesUseCase;
import com.ivanrl.yaet.domain.expense.NewExpenseRequest;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import com.ivanrl.yaet.domain.income.persistence.IncomePO;
import com.ivanrl.yaet.domain.income.persistence.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/new")
@RequiredArgsConstructor
public class AddMovementController {

    private final IncomeRepository incomeRepository;
    private final ManageExpensesUseCase manageExpensesUseCase;
    private final SeeExpensesUseCase seeExpensesUseCase;
    private final SeeCategoriesUseCase seeCategoriesUseCase;

    @GetMapping
    public String baseView(Model model) {
        model.addAttribute("categories", this.seeCategoriesUseCase.getAll());
        model.addAttribute("expense", NewExpenseRequest.empty());

        model.addAttribute("lastExpenses", this.seeExpensesUseCase.getLastExpenses());

        model.addAttribute("income", new NewIncome(Strings.EMPTY, null, LocalDate.now()));

        model.addAttribute("lastIncomes", this.incomeRepository.findTop10ByOrderByDateDesc());


        return "newExpense";
    }

    @Transactional
    @PostMapping("/expense")
    public String addNewExpense(Model model,
                                @RequestBody NewExpenseRequest request) {

        var expense = this.manageExpensesUseCase.addExpense(request);

        model.addAttribute("message", "A new expense for %s€ was successfully added.".formatted(expense.amount()));
        model.addAttribute("expense", request);

        model.addAttribute("lastExpenses", this.seeExpensesUseCase.getLastExpenses());

        // Not great to have to call this everytime
        model.addAttribute("categories", this.seeCategoriesUseCase.getAll());

        return "newExpense :: addExpense";
    }

    @GetMapping("/lastExpenses")
    public String getExpensesPage(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {

        model.addAttribute("lastExpenses", this.seeExpensesUseCase.getExpenses(Pageable.ofSize(size).withPage(page)));

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

record NewIncome(String payer, BigDecimal amount, LocalDate date) {}
