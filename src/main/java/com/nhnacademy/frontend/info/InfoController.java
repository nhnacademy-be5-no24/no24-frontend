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
    /**
     * get User information main page
     * @Param none.
     * @return mav
     */
    @GetMapping
    public ModelAndView info() {
        ModelAndView mav = new ModelAndView("index/info/main");

        return mav;
    }

    /**
     * get User information page
     * @Param none.
     * @return mav
     */
    @GetMapping("/user")
    public ModelAndView userInfoPage() {
        ModelAndView mav = new ModelAndView("index/info/check");

        return mav;
    }

    /**
     * check User password
     * @Param MemberPasswordDto
     * @return mav
     */
    @PostMapping("/user")
    public ModelAndView userInfoCheck() {
        ModelAndView mav = new ModelAndView("index/info/user");

        return mav;
    }


    /**
     * get User Address page
     * @Param none.
     * @return
     */
    @GetMapping("/address")
    public ModelAndView userAddress() {
        ModelAndView mav = new ModelAndView("index/info/address");

        return mav;
    }

    /**
     * add User Address
     * @Param AddressDto
     * @return mav
     */
    @PostMapping("/address/add")
    public ModelAndView userAddressPost() {
        ModelAndView mav = new ModelAndView("index/info/address");

        return mav;
    }

    /**
     * get Save User Address page
     * @Param none.
     * @return mav
     */
    @GetMapping("/address/add")
    public ModelAndView addUserAddressPage() {
        ModelAndView mav = new ModelAndView("index/info/address_save");

        mav.addObject("title", "배송주소 추가");

        return mav;
    }

    /**
     * modify Save User Address
     * @Param HttpServletRequest request
     * @return mav
     */
    @PostMapping("/address/modify")
    public ModelAndView modifyUserAddressPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/address_save");

        // todo: mav.addObject() 설정 필요
        System.out.println(request.getParameter("address_id"));
        mav.addObject("title", "배송주소 수정");

        return mav;
    }

    /**
     * delete Save User Address
     * @Param HttpServletRequest request
     * @return mav
     */
    @PostMapping("/address/delete")
    public ModelAndView deleteUserAddress(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/info/address");

        // todo: address_id에 대한 삭제 처리 필요

        return mav;
    }


    /**
     * modify User default Address
     * @Param HttpServletRequest request
     * @return mav
     */
    @PostMapping("/address/set/default")
    public ModelAndView setDefaultUserAddress(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/info/address");

        // todo: address_id의 default address 설정 처리 필요
//        System.out.println("modify to default!");

        return mav;
    }

    /**
     * get User Reviews page
     * @Param none.
     * @return mav
     */
    @GetMapping("/review")
    public ModelAndView getUserReviewPage() {
        ModelAndView mav = new ModelAndView("index/info/review");

        return mav;
    }

    /**
     * get User Tier page
     * @Param none.
     * @return mav
     */
    @GetMapping("/tier")
    public ModelAndView getUserTierPage() {
        ModelAndView mav = new ModelAndView("index/info/tier");

        return mav;
    }

    /**
     * get User Coupon page
     * @Param none.
     * @return mav
     */
    @GetMapping("/coupon")
    public ModelAndView getUserCouponPage() {
        ModelAndView mav = new ModelAndView("index/info/coupon");

        return mav;
    }

    /**
     * get User Point page
     * @Param none.
     * @return mav
     */
    @GetMapping("/point")
    public ModelAndView getUserPointPage() {
        ModelAndView mav = new ModelAndView("index/info/point");

        return mav;
    }

    /**
     * get User Order page
     * @Param none.
     * @return mav
     */
    @GetMapping("/order")
    public ModelAndView getUserOrderPage() {
        ModelAndView mav = new ModelAndView("index/info/order");

        return mav;
    }

    /**
     * get User OrderDetail page
     * @Param HttpServletRequest request.
     * @return mav
     */
    @PostMapping("/order/detail")
    public ModelAndView getUserOrderDetailPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/order_detail");

        // todo: get orderId by HttpServletRequest
        // mav.addObject("orderDetail", orderDetailList);

        return mav;
    }

    /**
     * delete User Order
     * @Param none.
     * @return mav
     */
    @PostMapping("/order/delete")
    public ModelAndView getUserOrderDelete() {
        ModelAndView mav = new ModelAndView("redirect:/info/order");

        // todo: order delete implement

        return mav;
    }


    /**
     * delete User OrderDetail
     * @Param none.
     * @return mav
     */
    @PostMapping("/order/detail/delete")
    public ModelAndView getUserOrderDetailDelete() {
        ModelAndView mav = new ModelAndView("redirect:/info/order");

        // todo: orderDetail delete implement

        return mav;
    }

    @GetMapping("/inquiry")
    public ModelAndView getUserInquiry() {
        ModelAndView mav = new ModelAndView("index/info/inquiry");

        return mav;
    }
}
