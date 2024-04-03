package com.nhnacademy.frontend.info;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * User info Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/03
 */
@Controller
@RequestMapping("/info")
public class InfoController {
    @GetMapping
    public ModelAndView info() {
        ModelAndView mav = new ModelAndView("index/info/main");

        return mav;
    }

    @GetMapping("/user")
    public ModelAndView userInfoPage() {
        ModelAndView mav = new ModelAndView("index/info/check");

        return mav;
    }

    @PostMapping("/user")
    public ModelAndView userInfoCheck() {
        ModelAndView mav = new ModelAndView("index/info/user");

        return mav;
    }

    @GetMapping("/address")
    public ModelAndView userAddress() {
        ModelAndView mav = new ModelAndView("index/info/address");

        return mav;
    }

    @PostMapping("/address/add")
    public ModelAndView userAddressPost() {
        ModelAndView mav = new ModelAndView("index/info/address");

        return mav;
    }

    @GetMapping("/address/add")
    public ModelAndView addUserAddressPage() {
        ModelAndView mav = new ModelAndView("index/info/address_save");

        mav.addObject("title", "배송주소 추가");

        return mav;
    }

    @PostMapping("/address/modify")
    public ModelAndView modifyUserAddressPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/address_save");

        // todo: mav.addObject() 설정 필요
        System.out.println(request.getParameter("address_id"));
        mav.addObject("title", "배송주소 수정");

        return mav;
    }

    @PostMapping("/address/delete")
    public ModelAndView deleteUserAddress(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/info/address");

        // todo: address_id에 대한 삭제 처리 필요

        return mav;
    }

    @PostMapping("/address/set/default")
    public ModelAndView setDefaultUserAddress(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/info/address");

        // todo: address_id의 default address 설정 처리 필요
        System.out.println("modify to default!");

        return mav;
    }
}
