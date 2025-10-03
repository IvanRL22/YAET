package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import com.ivanrl.yaet.domain.expense.ManageExpensesUseCase;
import com.ivanrl.yaet.domain.expense.NewExpenseRequest;
import com.ivanrl.yaet.domain.expense.SeeExpensesUseCase;
import com.ivanrl.yaet.domain.income.ManageIncomeUseCase;
import com.ivanrl.yaet.domain.income.NewIncomeRequest;
import com.ivanrl.yaet.domain.income.SeeIncomesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/new")
@RequiredArgsConstructor
public class AddMovementController {

    private final ManageExpensesUseCase manageExpensesUseCase;
    private final SeeExpensesUseCase seeExpensesUseCase;
    private final SeeCategoriesUseCase seeCategoriesUseCase;
    private final ManageIncomeUseCase manageIncomeUseCase;
    private final SeeIncomesUseCase seeIncomesUseCase;

    @GetMapping
    public ModelAndView baseView(Model model) {
        model.addAttribute("categories", this.seeCategoriesUseCase.getAll());
        model.addAttribute("expense", NewExpenseRequest.empty());

        model.addAttribute("lastExpenses", this.seeExpensesUseCase.getLastExpenses());

        model.addAttribute("income", NewIncomeRequest.empty());

        model.addAttribute("lastIncomes", this.seeIncomesUseCase.getIncomes());


        return new ModelAndView("newExpense", model.asMap());
    }

    @Transactional
    @PostMapping("/expense")
    public ModelAndView addNewExpense(Model model,
                                @RequestBody NewExpenseRequest request) {

        var expense = this.manageExpensesUseCase.addExpense(request);

        model.addAttribute("message", "A new expense for %s€ was successfully added.".formatted(expense.amount()));
        model.addAttribute("expense", request);

        model.addAttribute("lastExpenses", this.seeExpensesUseCase.getLastExpenses());

        // Not great to have to call this everytime
        model.addAttribute("categories", this.seeCategoriesUseCase.getAll());

        return new ModelAndView("newExpense :: addExpense", model.asMap());
    }

    @GetMapping("/lastExpenses")
    public ModelAndView getExpensesPage(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  Model model) {

        model.addAttribute("lastExpenses", this.seeExpensesUseCase.getLastExpenses(Pageable.ofSize(size).withPage(page)));

        return new ModelAndView("newExpense :: lastExpenses", model.asMap());
    }

    @PostMapping("/income")
    public ModelAndView addNewIncome(Model model,
                               @RequestBody NewIncomeRequest newIncome) {
        var income = this.manageIncomeUseCase.addNewIncome(newIncome);

        model.addAttribute("incomeMessage", "A new income for %s€ was successfully added.".formatted(income.amount()));
        model.addAttribute("income", newIncome); // Using same data is fine, as long as nothing changes when persisting
        model.addAttribute("lastIncomes", this.seeIncomesUseCase.getIncomes());

        return new ModelAndView("newExpense :: addIncome", model.asMap());
    }
}

