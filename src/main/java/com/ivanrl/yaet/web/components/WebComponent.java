package com.ivanrl.yaet.web.components;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public interface WebComponent {

    void attach(Model model);

    ModelAndView toModelAndView();

}
