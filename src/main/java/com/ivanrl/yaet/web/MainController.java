package com.ivanrl.yaet.web;

import com.ivanrl.yaet.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final UserData userData;

    @GetMapping(value = {"", "/", "/home"})
    public String home(Model model) {

        model.addAttribute("username", userData.getName());

        return "home";
    }

}
