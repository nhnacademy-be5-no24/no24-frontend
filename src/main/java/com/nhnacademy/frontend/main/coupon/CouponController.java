package com.nhnacademy.frontend.main.coupon;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Order Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/08
 */
@Controller
@RequestMapping("/coupon")
public class CouponController {

    /**
     * getCouponPage
     * @return mav
     */
    @GetMapping
    public ModelAndView getCouponListPage() {
        ModelAndView mav = new ModelAndView("index/main/coupon/coupon");

        return mav;
    }
}
