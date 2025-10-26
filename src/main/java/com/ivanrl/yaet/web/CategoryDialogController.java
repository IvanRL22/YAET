package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.category.ManageCategoriesUseCase;
import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/category-dialog")
@RequiredArgsConstructor
public class CategoryDialogController {

    private final ManageCategoriesUseCase manageCategoriesUseCase;
    private final SeeCategoriesUseCase seeCategoriesUseCase;


    @GetMapping
    public String getDialog(Model model) {

        model.addAttribute("request", NewCategoryRequestTO.empty());

        return "category-dialog";
    }

    @PutMapping
    public List<ModelAndView> create(@Valid @ModelAttribute("request") NewCategoryRequestTO request,
                                     BindingResult bindingResult,
                                     Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                               bindingResult.getAllErrors().stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .toList());
            return List.of(new ModelAndView("category-dialog :: #result-information", model.asMap()));
        }

        this.manageCategoriesUseCase.create(request.toDomain());
        model.addAttribute("messages", List.of("The category was created"));

        var categories = this.seeCategoriesUseCase.getAll().stream().map(CategoryTO::from).sorted().toList();
        model.addAttribute(categories);

        return List.of(new ModelAndView("category-dialog :: #result-information", model.asMap()),
                       new ModelAndView("categories :: #category-list", Map.of("categories", categories)));
    }
}
