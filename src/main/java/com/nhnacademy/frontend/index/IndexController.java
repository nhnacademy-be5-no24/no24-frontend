package com.nhnacademy.frontend.index;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Main Page Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class IndexController {
    @Value("${category.domestic}")
    private String domesticCategoryId;

    @GetMapping(value = {"/index.html","/"})
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("redirect:/book/" + domesticCategoryId);

        return mav;
    }
}
