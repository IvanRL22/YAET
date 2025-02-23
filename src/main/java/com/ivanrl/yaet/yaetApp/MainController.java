package com.ivanrl.yaet.yaetApp;

import com.ivanrl.yaet.yaetApp.expenses.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    private final ExpenseRepository repository;


    @GetMapping("home")
    public String main(Model model) {

        return "home";
    }

}
