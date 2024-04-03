package com.nhnacademy.frontend.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IndexController {
    @GetMapping(value = {"/index.html","/"})
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("index/index");

        return mav;
    }
}
