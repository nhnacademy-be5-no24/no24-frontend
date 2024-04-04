package com.nhnacademy.frontend.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Login Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class LoginController {
    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        ModelAndView mav = new ModelAndView("index/auth/login");

        return mav;
    }
}
