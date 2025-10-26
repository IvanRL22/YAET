package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.ManageCategoriesUseCase;
import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoriesController {

    private final SeeCategoriesUseCase seeCategoriesUseCase;
    private final ManageCategoriesUseCase manageCategoriesUseCase;

    @GetMapping
    public String getExpenses(Model model) {
        var categories = this.seeCategoriesUseCase.getAll().stream()
                .map(CategoryTO::from)
                .sorted()
                .toList();

        model.addAttribute("categories", categories);

        return "categories";
    }

    @GetMapping("/{id}")
    public String getDetail(@PathVariable int id,
                            Model model) {

        var category = UpdateCategoryRequestTO.from(this.seeCategoriesUseCase.getById(id));

        model.addAttribute("request", category);

        return "categories :: #category-detail-content";
    }

    @PutMapping
    public List<ModelAndView> update(@Valid @ModelAttribute("request") UpdateCategoryRequestTO request,
                       BindingResult bindingResult,
                       Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                               bindingResult.getAllErrors().stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .toList());
            return List.of(new ModelAndView("categories :: #result-information", model.asMap()));
        }


        var result = this.manageCategoriesUseCase.update(request.toDomainModel());
        model.addAttribute("messages", List.of("The category was updated"));
        model.addAttribute("categories", List.of(CategoryTO.from(result))); // Needs to be a list due to the way the HTML is defined

        return List.of(new ModelAndView("categories :: #result-information", model.asMap()),
                new ModelAndView("categories :: category-item", model.asMap()));
    }

    @PutMapping("/{id}/reorder")
    @ResponseStatus(HttpStatus.NO_CONTENT) // So that Spring does not try to return a template
    public void reorder(@PathVariable int id,
                        @RequestParam int newPosition) {
        this.manageCategoriesUseCase.reorder(id, newPosition);
    }
}
