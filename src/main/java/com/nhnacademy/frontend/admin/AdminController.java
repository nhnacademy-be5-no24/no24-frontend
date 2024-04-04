package com.nhnacademy.frontend.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Admin Page Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
@RequestMapping("/admin")
public class AdminController {
    /**
     * @Param none
     * @return mav
     */
    @GetMapping
    public ModelAndView getAdminPage() {
        ModelAndView mav = new ModelAndView("index/admin/main");

        return mav;
    }
}
