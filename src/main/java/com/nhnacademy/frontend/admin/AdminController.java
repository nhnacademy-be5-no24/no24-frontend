package com.nhnacademy.frontend.admin;

import com.nhnacademy.frontend.book.dto.request.BookCreateRequestDto;
import com.nhnacademy.frontend.book.dto.request.BookRequestDto;
import com.nhnacademy.frontend.book.dto.response.BookResponseDto;
import com.nhnacademy.frontend.book.dto.response.BookResponsePage;
import com.nhnacademy.frontend.category.dto.CategoryResponseDto;
import com.nhnacademy.frontend.coupon.dto.CouponTarget;
import com.nhnacademy.frontend.coupon.dto.Status;
import com.nhnacademy.frontend.coupon.dto.request.CouponRequestDto;
import com.nhnacademy.frontend.coupon.dto.response.CouponResponseDto;
import com.nhnacademy.frontend.coupon.dto.response.CouponResponseDtoList;
import com.nhnacademy.frontend.grade.dto.request.GradeRequestDto;
import com.nhnacademy.frontend.grade.dto.response.GradeResponseDto;
import com.nhnacademy.frontend.grade.dto.response.GradeResponseDtoList;
import com.nhnacademy.frontend.main.order.dto.response.WrapResponseDtoList;
import com.nhnacademy.frontend.wrap.dto.request.ModifyWrapRequestDto;
import com.nhnacademy.frontend.wrap.dto.request.WrapRequestDto;
import com.nhnacademy.frontend.wrap.dto.response.WrapResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * Admin Page Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RestTemplate restTemplate;

    /**
     * get Admin Main Page
     * @Param
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
        ResponseEntity<BookResponsePage> bookResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/books?pageSize=0&offset=10",
                BookResponsePage.class
        );
        List<BookResponseDto> bookList = bookResponseEntity.getBody().getBookResponseDtoList();
        mav.addObject("bookList", bookList);

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
        ResponseEntity<BookResponseDto> bookResponseDtoResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/books/isbn/" + bookIsbn,
                BookResponseDto.class
        );


        BookResponseDto bookResponseDto = bookResponseDtoResponseEntity.getBody();
        mav.addObject("book", bookResponseDto);

        return mav;
    }

    /**
     * get managing book page
     * @Param none
     * @return mav
     */
    @PostMapping("/book/modify/{bookIsbn}")
    public ModelAndView modifyBook(@PathVariable String bookIsbn, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/book");

        String title = request.getParameter("bookTitle");
        String description = request.getParameter("bookDescription");
        LocalDate publishedAt = LocalDate.parse(request.getParameter("publishDate"));
        Long fixPrice = Long.parseLong(request.getParameter("fixprice"));
        Long salePrice = Long.parseLong(request.getParameter("salePrice"));
        boolean isPacking = request.getParameter("packagingAvailable") != null;
        int status = Integer.parseInt(request.getParameter("bookStatus"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String publisher = request.getParameter("publisher");
        String author = request.getParameter("author");
        String image = request.getParameter("bookImage");

        BookRequestDto bookRequestDto = BookRequestDto.builder()
                .bookIsbn(bookIsbn)
                .bookTitle(title)
                .bookDescription(description)
                .publishedAt(publishedAt)
                .bookFixedPrice(fixPrice)
                .bookSalePrice(salePrice)
                .bookIsPacking(isPacking)
                .bookViews(0L)
                .bookStatus(status)
                .bookQuantity(quantity)
                .bookImage(image)
                .publisher(publisher)
                .author(author)
                .likes(0L)
                .build();
        restTemplate.put(
                requestUrl + ":" + port + "/shop/books",
                bookRequestDto
        );
        return mav;
    }
    @DeleteMapping("/book/delete/{bookIsbn}")
    public ModelAndView getDeleteBook(
            @PathVariable String bookIsbn
    ){
        ModelAndView mav = new ModelAndView("redirect:/admin/book");
        restTemplate.delete(
                requestUrl + ":" + port + "/shop/books/delete/" + bookIsbn
        );

        return mav;
    }

    /**
     * get creating book page
     * @Param none
     * @return mav
     */
    @GetMapping("/book/add")
    public ModelAndView getSaveBookPage() {
        return new ModelAndView(("index/admin/add_book"));
    }


    /**
     * get creating book page
     * @Param none
     * @return mav
     */
    @PostMapping("/book/add")
    public ModelAndView saveBook(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/book");

        String isbn = request.getParameter("bookIsbn");
        String title = request.getParameter("bookTitle");
        String description = request.getParameter("bookDescription");
        LocalDate publishedAt = LocalDate.parse(request.getParameter("publishDate"));
        Long fixPrice = Long.parseLong(request.getParameter("regularPrice"));
        Long salePrice = Long.parseLong(request.getParameter("salePrice"));
        boolean isPacking = request.getParameter("packagingAvailable") != null;
        int status = Integer.parseInt(request.getParameter("bookStatus"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        String publisher = request.getParameter("publisher");
        String image = request.getParameter("bookImage");
        String author = request.getParameter("author");

        BookCreateRequestDto bookCreateRequestDto = BookCreateRequestDto.builder()
                .bookIsbn(isbn)
                .bookTitle(title)
                .bookDescription(description)
                .publishedAt(publishedAt)
                .bookFixedPrice(fixPrice)
                .bookSalePrice(salePrice)
                .bookIsPacking(isPacking)
                .bookViews(0L)
                .bookStatus(status)
                .bookQuantity(quantity)
                .bookImage(image)
                .bookPublisher(publisher)
                .author(author)
                .likes(0L)
                .build();

            restTemplate.postForEntity(
                requestUrl + ":" + port + "/shop/books/page",
                bookCreateRequestDto,
                BookResponseDto.class
        );
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

        ResponseEntity<CouponResponseDtoList> couponResponseDto = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/coupon?pageSize=0&offset=10",
                CouponResponseDtoList.class
        );
        List<CouponResponseDto> couponList = couponResponseDto.getBody().getCouponResponseDtoList();

        mav.addObject("couponList", couponList);

        return mav;
    }

    /**
     * get managing coupon page
     * @Param none
     * @return mav
     */
    @GetMapping("/coupon/add")
    public ModelAndView getAddCouponPage() {
        ModelAndView mav = new ModelAndView("index/admin/add_coupon");
        mav.addObject("tomorrow", LocalDate.now().plusDays(1));

        return mav;
    }

    /**
     * save coupon
     * @Param none
     * @return mav
     */
    @PostMapping("/coupon/add")
    public ModelAndView saveCoupon(HttpServletRequest request, CouponRequestDto couponRequestDto) {
        ModelAndView mav = new ModelAndView("redirect:/admin/coupon");

        // get data
        String couponName = request.getParameter("couponName");
        String deadline = request.getParameter("expirationDate");

        String couponType = request.getParameter("couponType");
        String discountAmount = request.getParameter("discountAmount");
        String minOrderAmount = request.getParameter("minOrderAmount");
        String discountPercentage = request.getParameter("discountPercentage");
        String maxDiscountAmount = request.getParameter("maxDiscountAmount");

        String couponTarget = request.getParameter("couponTarget");
        String bookISBN = request.getParameter("bookISBN");
        String categoryName = request.getParameter("categoryName");

        // Dto 기본 세팅
        couponRequestDto.setCouponStatus(Status.ACTIVE);

        if(couponRequestDto.getCouponTarget() == CouponTarget.CATEGORY) {
            try {
                ResponseEntity<CategoryResponseDto> categoryResponseEntity = restTemplate.getForEntity(
                        requestUrl + ":" + port + "/shop/categories/name/" + categoryName,
                        CategoryResponseDto.class
                );
                couponRequestDto.setCategoryId(categoryResponseEntity.getBody().getCategoryId());
            } catch (Exception e) {
                log.error("카테고리 이름이 없습니다.");
                mav.setViewName("redirect:/error");

                return mav;
            }
        }

        ResponseEntity<CouponResponseDto> couponResponseEntity = restTemplate.postForEntity(
                requestUrl + ":" + port + "/shop/coupon/create",
                couponRequestDto,
                CouponResponseDto.class
        );

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

        ResponseEntity<CouponResponseDto> couponResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/coupon/" + couponId,
                CouponResponseDto.class
        );

        LocalDate deadline = couponResponseEntity.getBody().getDeadline()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();

        mav.addObject("tomorrow", LocalDate.now().plusDays(1));
        mav.addObject("coupon", couponResponseEntity.getBody());
        mav.addObject("couponDeadline", deadline);

        return mav;
    }

    /**
     * save coupon
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

        ResponseEntity<WrapResponseDtoList> wrapResponseDtoList = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/wraps?page=0&size=10",
                WrapResponseDtoList.class
        );
        List<WrapResponseDto> wrapList = wrapResponseDtoList.getBody().getWrapResponseDtos();

        mav.addObject("wrapList", wrapList);

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

        ResponseEntity<WrapResponseDto> wrapResponseDto = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/wraps/id/" + wrapId,
                WrapResponseDto.class
        );

        mav.addObject("wrap", wrapResponseDto.getBody());

        return mav;
    }

    /**
     * get managing wrapping page
     * @Param none
     * @return mav
     */
    @GetMapping("/wrap/add")
    public ModelAndView getAddWrappingPage() {
        ModelAndView mav = new ModelAndView("index/admin/add_wrapping");

        return mav;
    }

    @PostMapping("/wrap/modify/{wrapId}")
    public ModelAndView modifyWrapping(@PathVariable Long wrapId, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/wrap");

        ModifyWrapRequestDto modifyWrapRequestDto = ModifyWrapRequestDto.builder()
                .wrapId(wrapId)
                .wrapName(request.getParameter("wrapName"))
                .wrapCost(Long.parseLong(request.getParameter("wrapCost")))
                .build();

        restTemplate.put(
                requestUrl + ":" + port + "/shop/wraps",
                modifyWrapRequestDto
        );

        return mav;
    }


    @PostMapping("/wrap/add")
    public ModelAndView addWrapping(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/wrap");

        WrapRequestDto wrapRequestDto = WrapRequestDto.builder()
                .wrapName(request.getParameter("wrapName"))
                .wrapCost(Long.parseLong(request.getParameter("wrapCost")))
                .build();

        restTemplate.postForEntity(
                requestUrl + ":" + port + "/shop/wraps",
                wrapRequestDto,
                null
        );

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

        List<GradeResponseDto> gradeList = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/grade/all",
                GradeResponseDtoList.class
        ).getBody().getGradeResponseDtoList();

        mav.addObject("gradeList", gradeList);

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

        GradeResponseDto grade = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/grade/id/" + tierId,
                GradeResponseDto.class
        ).getBody();

        mav.addObject("grade", grade);

        return mav;
    }

    /**
     * modify tier policy
     * @Param none
     * @return mav
     */
    @PostMapping("/tier/modify/{tierId}")
    public ModelAndView modifyTier(@PathVariable("tierId") Long tierId, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("redirect:/admin/tier");

        GradeResponseDto gradeResponseDto = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/grade/id/" + tierId,
                GradeResponseDto.class
        ).getBody();

        GradeRequestDto gradeRequestDto = GradeRequestDto.builder()
                .gradeId(tierId)
                .gradeName(gradeResponseDto.getGradeName())
                .gradeNameKor(gradeResponseDto.getGradeNameKor())
                .accumulateRate(Long.parseLong(request.getParameter("accumulateRate")))
                .tenPercentCoupon(Integer.parseInt(request.getParameter("tenPercentCoupon")))
                .twentyPercentCoupon(Integer.parseInt(request.getParameter("twentyPercentCoupon")))
                .requiredPayment(Long.parseLong(request.getParameter("requiredPayment")))
                .build();

        restTemplate.put(
                requestUrl + ":" + port + "/shop/grade/update",
                gradeRequestDto
        );

        return mav;
    }
}
