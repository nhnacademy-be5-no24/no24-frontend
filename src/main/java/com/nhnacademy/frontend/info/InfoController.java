package com.nhnacademy.frontend.info;

import com.nhnacademy.frontend.address.dto.request.AddressCreateRequestDto;
import com.nhnacademy.frontend.address.dto.request.AddressModifyRequestDto;
import com.nhnacademy.frontend.address.dto.response.AddressResponseDto;
import com.nhnacademy.frontend.address.dto.response.AddressResponseDtoList;
import com.nhnacademy.frontend.coupon.dto.response.CouponMemberResponseDtoList;
import com.nhnacademy.frontend.main.order.dto.response.OrdersResponseDtoList;
import com.nhnacademy.frontend.point.dto.PointResponseDto;
import com.nhnacademy.frontend.point.dto.PointResponseDtoList;
import com.nhnacademy.frontend.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * User info Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/03
 */
@Controller
@RequestMapping("/info")
@RequiredArgsConstructor
public class InfoController {
    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    /**
     * get User information main page
     * @Param none.
     * @return mav
     */
    @GetMapping
    public ModelAndView info(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/main");

        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        ResponseEntity<Long> point = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/point/" + customerNo,
                Long.class);

        mav.addObject("point", point.getBody());

        return mav;
    }

    /**
     * get User information page
     * @Param none.
     * @return mav
     */
    @GetMapping("/user")
    public ModelAndView userInfoPage(HttpServletRequest request) {
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
    public ModelAndView userAddress(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/address");

        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        ResponseEntity<AddressResponseDtoList> addressResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/address/customer/" + customerNo,
                AddressResponseDtoList.class);

        mav.addObject("addressList", addressResponseEntity.getBody().getAddressResponseDtoList());


        return mav;
    }

    /**
     * add User Address
     * @Param AddressDto
     * @return mav
     */
    @PostMapping("/address/save")
    public ModelAndView userAddressSave(HttpServletRequest request,
                                        AddressCreateRequestDto addressCreateRequestDto) {
        ModelAndView mav = new ModelAndView("redirect:/info/address");
        boolean isDefault = request.getParameter("defaultAddress").equals("on");

        Long customerNo = AuthUtil.getCustomerNo(
                requestUrl,
                port,
                request,
                redisTemplate,
                restTemplate);

        addressCreateRequestDto.setIsDefault(isDefault);
        addressCreateRequestDto.setCustomerNo(customerNo);

        restTemplate.postForEntity(
                requestUrl + ":" + port + "/shop/address/create",
                addressCreateRequestDto,
                null
        );

        return mav;
    }

    /**
     * add User Address
     * @Param AddressDto
     * @return mav
     */
    @PostMapping("/address/modify")
    public ModelAndView userAddressModify(HttpServletRequest request,
                                          AddressModifyRequestDto addressModifyRequestDto) {
        ModelAndView mav = new ModelAndView("redirect:/info/address");
        boolean isDefault = request.getParameter("defaultAddress").equals("on");

        addressModifyRequestDto.setIsDefault(isDefault);

        restTemplate.put(
                requestUrl + ":" + port + "/shop/address/modify/" + request.getParameter("addressId"),
                addressModifyRequestDto
        );

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
    @PostMapping("/address/modify/page")
    public ModelAndView modifyUserAddressPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/address_save");

        Long addressId = Long.parseLong(request.getParameter("address_id"));

        ResponseEntity<AddressResponseDto> addressResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/address/" + addressId,
                AddressResponseDto.class
        );

        mav.addObject("title", "배송주소 수정");
        mav.addObject("address", addressResponseEntity.getBody());

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

        Long addressId = Long.parseLong(request.getParameter("address_id"));

        restTemplate.delete(
                requestUrl + ":" + port + "/shop/address/delete/" + addressId
        );

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

        Long addressId = Long.parseLong(request.getParameter("address_id"));

        AddressResponseDto addressResponseDto = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/address/" + addressId,
                AddressResponseDto.class
        ).getBody();

        AddressModifyRequestDto addressModifyRequestDto = AddressModifyRequestDto.builder()
                .alias(addressResponseDto.getAlias())
                .receiverName(addressResponseDto.getReceiverName())
                .receiverPhoneNumber(addressResponseDto.getReceiverPhoneNumber())
                .zipcode(addressResponseDto.getZipcode())
                .address(addressResponseDto.getAddress())
                .addressDetail(addressResponseDto.getAddressDetail())
                .isDefault(true)
                .build();

        restTemplate.put(
                requestUrl + ":" + port + "/shop/address/modify/" + addressId,
                addressModifyRequestDto
        );

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
    public ModelAndView getUserCouponPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/coupon");
        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        try {
            ResponseEntity<CouponMemberResponseDtoList> couponMemberResponseDtoListResponseEntity = restTemplate.getForEntity(
                    requestUrl + ":" + port + "/shop/coupon/member/" + customerNo + "?pageSize=0&offset=10",
                    CouponMemberResponseDtoList.class
            );

            mav.addObject("couponList", couponMemberResponseDtoListResponseEntity.getBody()
                    .getCouponMemberResponseDtoList());
        } catch(Exception e) {
            mav.setViewName("redirect:/error");
        }

        return mav;
    }

    /**
     * get User Point page
     * @Param none.
     * @return mav
     */
    @GetMapping("/point")
    public ModelAndView getUserPointPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/point");
        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        List<PointResponseDto> response = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/points/" + customerNo + "?page=0&size=10",
                PointResponseDtoList.class
        ).getBody().getPointResponseDtoList();

        mav.addObject("pointList", response);

        return mav;
    }

    /**
     * get User Order page
     * @Param none.
     * @return mav
     */
    @GetMapping("/order")
    public ModelAndView getUserOrderPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("index/info/order");

        Long customerNo = AuthUtil.getCustomerNo(requestUrl, port, request, redisTemplate, restTemplate);

        OrdersResponseDtoList ordersReponseDtoList = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/orders/customer/" + customerNo,
                OrdersResponseDtoList.class
        ).getBody();

        mav.addObject("orderList", ordersReponseDtoList.getOrdersResponseDtos());

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
