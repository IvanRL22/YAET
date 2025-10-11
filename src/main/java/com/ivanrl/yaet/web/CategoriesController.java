package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoriesController {

    private final SeeCategoriesUseCase seeCategoriesUseCase;

    @GetMapping
    public String getExpenses(Model model) {
        var categories = this.seeCategoriesUseCase.getAll().stream()
                .map(CategoryTO::from)
                .sorted()
                .toList();

        model.addAttribute("categories", categories);

        return "categories";
    }
}
