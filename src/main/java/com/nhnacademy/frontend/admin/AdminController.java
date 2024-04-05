package com.nhnacademy.frontend.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

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
     * get Admin Main Page
     * @Param none
     * @return mav
     */
    @GetMapping
    public ModelAndView getAdminPage() {
        ModelAndView mav = new ModelAndView("index/admin/main");

        return mav;
    }

    /**
     * get admin user page
     * @Param none
     * @return mav
     */
    @GetMapping("/user")
    public ModelAndView getAdminUser() {
        ModelAndView mav = new ModelAndView("index/admin/user");

        return mav;
    }

    /**
     * get modify user page
     * @Param none
     * @return mav
     */
    @GetMapping("/user/modify/{memberId}")
    public ModelAndView getManageUser(@PathVariable String memberId) {
        ModelAndView mav = new ModelAndView("index/admin/modify_user");

        return mav;
    }

    /**
     * get admin book page
     * @Param none
     * @return mav
     */
    @GetMapping("/book")
    public ModelAndView getAdminBook() {
        ModelAndView mav = new ModelAndView("index/admin/book");

        return mav;
    }

    /**
     * get managing book page
     * @Param none
     * @return mav
     */
    @GetMapping("/book/modify/{bookIsbn}")
    public ModelAndView getManageBook(@PathVariable String bookIsbn) {
        ModelAndView mav = new ModelAndView("index/admin/modify_book");

        return mav;
    }

    /**
     * get managing book page
     * @Param none
     * @return mav
     */
    @PostMapping("/book/modify/{bookIsbn}")
    public ModelAndView saveManageBook(@PathVariable String bookIsbn, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/book");

        // todo: 저장 알고리즘 구현, modify_book.html name 수정 필요.

        return mav;
    }

    /**
     * get admin coupon page
     * @Param none
     * @return mav
     */
    @GetMapping("/coupon")
    public ModelAndView getAdminCoupon() {
        ModelAndView mav = new ModelAndView("index/admin/coupon");

        return mav;
    }

    /**
     * get managing coupon page
     * @Param none
     * @return mav
     */
    @GetMapping("/coupon/modify/{couponId}")
    public ModelAndView getManageCoupon(@PathVariable Long couponId) {
        ModelAndView mav = new ModelAndView("index/admin/modify_coupon");

        return mav;
    }

    /**
     * get managing coupon page
     * @Param none
     * @return mav
     */
    @PostMapping("/coupon/modify/{couponId}")
    public ModelAndView saveCoupon(@PathVariable Long couponId, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/coupon");

        return mav;
    }

    /**
     * get admin wrapping page
     * @Param none
     * @return mav
     */
    @GetMapping("/wrap")
    public ModelAndView getAdminWrapping() {
        ModelAndView mav = new ModelAndView("index/admin/wrapping");

        return mav;
    }

    /**
     * get managing wrapping page
     * @Param none
     * @return mav
     */
    @GetMapping("/wrap/modify/{wrapId}")
    public ModelAndView getManageWrapping(@PathVariable Long wrapId) {
        ModelAndView mav = new ModelAndView("index/admin/modify_wrapping");

        return mav;
    }

    /**
     * get admin tier page
     * @Param none
     * @return mav
     */
    @GetMapping("/tier")
    public ModelAndView getAdminTier() {
        ModelAndView mav = new ModelAndView("index/admin/tier");

        return mav;
    }

    /**
     * get admin tier managing page
     * @Param none
     * @return mav
     */
    @GetMapping("/tier/modify/{tierId}")
    public ModelAndView getModifyTierPage(@PathVariable("tierId") Long tierId) {
        ModelAndView mav = new ModelAndView("index/admin/modify_tier");

        return mav;
    }

    /**
     * modify tier policy
     * @Param none
     * @return mav
     */
    @PostMapping("/tier/modify/{tierId}")
    public ModelAndView modifyTier(@PathVariable("tierId") Long tierId) {
        ModelAndView mav = new ModelAndView("redirect:/admin/tier");

        return mav;
    }
}
