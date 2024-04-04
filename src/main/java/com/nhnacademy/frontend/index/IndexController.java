package com.nhnacademy.frontend.index;

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
    @GetMapping(value = {"/index.html","/"})
    public ModelAndView index(){
        ModelAndView mav = new ModelAndView("index/index");

        return mav;
    }
}
