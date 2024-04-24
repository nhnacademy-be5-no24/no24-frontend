package com.nhnacademy.frontend.main.book;

import com.nhnacademy.frontend.book.dto.BookResponseDto;
import com.nhnacademy.frontend.book.dto.BookResponseList;
import com.nhnacademy.frontend.category.dto.CategoryInfoResponseDto;
import com.nhnacademy.frontend.category.dto.CategoryInfoResponseList;
import com.nhnacademy.frontend.category.dto.ParentCategoryResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

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

    public BookController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/book/{categoryId}/{bookIsbn}")
    public ModelAndView getBookPage(@PathVariable("categoryId") Long categoryId,
                                    @PathVariable("bookIsbn") Long bookIsbn) {
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

        BookResponseDto bookResponseDto = bookResponseEntity.getBody();

        ModelAndView mav = new ModelAndView("index/main/book/book");

        mav.addObject("allParentCategories", categoryInfoResponseDtoList);
        mav.addObject("domesticCategoryId", domesticCategoryId);
        mav.addObject("bookInfo", bookResponseDto);

        return mav;
    }

    @GetMapping("/book/{category}")
    public ModelAndView getBookListPage(@PathVariable("category") Long categoryId){
        // 하위 카테고리 조회
        ResponseEntity<ParentCategoryResponseDto> categoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/parents/" + categoryId,
                ParentCategoryResponseDto.class
        );

        ParentCategoryResponseDto parentCategoryResponseDto = categoryResponseEntity.getBody();

        // 카테고리에 맞는 책 조회
        ResponseEntity<BookResponseList> bookResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/books/category/" + categoryId + "?pageSize=0&offset=10",
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

        return mav;
    }
}
