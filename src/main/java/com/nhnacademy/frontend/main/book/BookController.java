package com.nhnacademy.frontend.main.book;

import com.nhnacademy.frontend.book.dto.response.BookResponseDto;
import com.nhnacademy.frontend.book.dto.response.BookResponseList;
import com.nhnacademy.frontend.category.dto.CategoryInfoResponseDto;
import com.nhnacademy.frontend.category.dto.CategoryInfoResponseList;
import com.nhnacademy.frontend.category.dto.ParentCategoryResponseDto;
import com.nhnacademy.frontend.main.member.dto.MemberInfoResponseDto;
import com.nhnacademy.frontend.main.review.dto.response.CustomerDto;
import com.nhnacademy.frontend.main.review.dto.response.ReviewPageResponseDto;
import com.nhnacademy.frontend.util.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * Book Detail Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class BookController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    @Value("${category.domestic}")
    private String domesticCategoryId;

    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    public BookController(RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/book/{categoryId}/{bookIsbn}")
    public ModelAndView getBookPage(HttpServletRequest request,
                                    @PathVariable("categoryId") Long categoryId,
                                    @PathVariable("bookIsbn") String bookIsbn,
                                    @RequestParam(defaultValue = "1") int pageSize,
                                    @RequestParam(defaultValue = "10") int offset) {
        // 하위 카테고리 조회
        ResponseEntity<ParentCategoryResponseDto> categoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/parents/" + categoryId,
                ParentCategoryResponseDto.class
        );

        ParentCategoryResponseDto parentCategoryResponseDto = categoryResponseEntity.getBody();

        // 부모 카테고리 조회
        ResponseEntity<CategoryInfoResponseList> parentCategoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/all-parents/" + categoryId,
                CategoryInfoResponseList.class
        );

        List<CategoryInfoResponseDto> categoryInfoResponseDtoList = parentCategoryResponseEntity.getBody().getContent();
        Collections.reverse(categoryInfoResponseDtoList);

        // 책 정보 수집
        ResponseEntity<BookResponseDto> bookResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/books/isbn/" + bookIsbn,
                BookResponseDto.class
        );

        ResponseEntity<ReviewPageResponseDto> reviewsByBookIsbnResponse = restTemplate.getForEntity(
                requestUrl + ":" + port + "/shop/reviews/bookIsbn/" + bookIsbn + "?pageSize=" + (pageSize - 1) + "&offset=" + offset,
                ReviewPageResponseDto.class
        );

        BookResponseDto bookResponseDto = bookResponseEntity.getBody();

        Long customerNo = AuthUtil.getCustomerNo(
                requestUrl,
                port,
                request,
                redisTemplate,
                restTemplate
        );

        ModelAndView mav = new ModelAndView("index/main/book/book");

        mav.addObject("childCategories", parentCategoryResponseDto.getChildCategories());
        mav.addObject("allParentCategories", categoryInfoResponseDtoList);
        mav.addObject("domesticCategoryId", domesticCategoryId);
        mav.addObject("bookInfo", bookResponseDto);

        mav.addObject("review", reviewsByBookIsbnResponse.getBody().getReviewList());
        mav.addObject("currentPage", pageSize);
        mav.addObject("pageSize", pageSize);
        mav.addObject("offset", offset);
        mav.addObject("totalPages", 10);

        ResponseEntity<CustomerDto> memberInfo = restTemplate.getForEntity(
                requestUrl + ":" + port + "/auth/customer/" + customerNo,
                CustomerDto.class
        );

        String memberId = memberInfo.getBody().getCustomerId();
        if (memberId.length() >= 3) {
            memberId = memberId.substring(0, 2) + memberId.substring(2).replaceAll(".", "*");
        }

        mav.addObject("memberId", memberId);

        return mav;
    }

    @GetMapping("/book/{category}")
    public ModelAndView getBookListPage(@PathVariable("category") Long categoryId,
                                        @RequestParam(defaultValue="1") int pageSize,
                                        @RequestParam(defaultValue="10") int offset){
        // 하위 카테고리 조회
        ResponseEntity<ParentCategoryResponseDto> categoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/parents/" + categoryId,
                ParentCategoryResponseDto.class
        );

        ParentCategoryResponseDto parentCategoryResponseDto = categoryResponseEntity.getBody();

        // 카테고리에 맞는 책 조회
        ResponseEntity<BookResponseList> bookResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/books/category/" + categoryId + "?pageSize=" + (pageSize - 1) + "&offset=" + offset,
                BookResponseList.class
        );

        List<BookResponseDto> bookResponseDtoPage = bookResponseEntity.getBody().getContent();

        // 부모 카테고리 조회
        ResponseEntity<CategoryInfoResponseList> parentCategoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/all-parents/" + categoryId,
                CategoryInfoResponseList.class
        );

        List<CategoryInfoResponseDto> categoryInfoResponseDtoList = parentCategoryResponseEntity.getBody().getContent();
        Collections.reverse(categoryInfoResponseDtoList);

        ModelAndView mav = new ModelAndView("index/main/book/book_list");
        mav.addObject("childCategories", parentCategoryResponseDto.getChildCategories());
        mav.addObject("parentCategory", parentCategoryResponseDto);
        mav.addObject("bookList", bookResponseDtoPage);
        mav.addObject("allParentCategories", categoryInfoResponseDtoList);
        mav.addObject("domesticCategoryId", domesticCategoryId);

        mav.addObject("currentPage", pageSize);
        mav.addObject("pageSize", pageSize);
        mav.addObject("offset", offset);
        mav.addObject("totalPages", bookResponseEntity.getBody().getMaxPage());

        return mav;
    }
}
