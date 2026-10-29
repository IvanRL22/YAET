package com.ivanrl.yaet.web;

import com.ivanrl.yaet.domain.ValidationError;
import com.ivanrl.yaet.domain.category.CategoryDO;
import com.ivanrl.yaet.domain.category.ManageCategoriesUseCase;
import com.ivanrl.yaet.domain.category.SeeCategoriesUseCase;
import com.ivanrl.yaet.web.notifications.NotificationGenerator;
import jakarta.servlet.http.HttpServletResponse;
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
                                     Model model,
                                     HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors",
                               bindingResult.getAllErrors().stream()
                                            .map(DefaultMessageSourceResolvable::getDefaultMessage)
                                            .toList());
            return List.of(new ModelAndView("category-dialog :: #result-information", model.asMap()));
        }

        // TODO Not a nice way to handle this, need to find a good way to add notifications for modal dialogs
        CategoryDO newCategory;
        try {
            newCategory = this.manageCategoriesUseCase.create(request.toDomain());
        } catch (ValidationError ve) {
            model.addAttribute("errors", List.of(ve.getMessage()));
            return List.of(new ModelAndView("category-dialog :: #result-information", model.asMap()));
        }

        var categories = this.seeCategoriesUseCase.getAll().stream().map(CategoryTO::from).sorted().toList();
        model.addAttribute(categories);

        response.addHeader("HX-Trigger", "closeDialog");

        return List.of(new ModelAndView("categories :: #category-list", Map.of("categories", categories)),
                       NotificationGenerator.info("Category '%s' was created.".formatted(newCategory.name())));
    }
}
