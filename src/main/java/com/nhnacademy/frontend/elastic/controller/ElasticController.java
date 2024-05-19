package com.nhnacademy.frontend.elastic.controller;

import com.nhnacademy.frontend.category.dto.CategoryResponseDto;
import com.nhnacademy.frontend.category.dto.ParentCategoryResponseDto;
import com.nhnacademy.frontend.elastic.dto.response.ElasticResponseDto;
import com.nhnacademy.frontend.elastic.service.ElasticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 검색을 처리할 Controller 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/book")
public class ElasticController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final ElasticService elasticService;
    private final RestTemplate restTemplate;

    /**
     * 상품 제목 검색 <br/>
     * 메인화면에서 검색을 처리하는 메소드 입니다.
     *
     * @param keyword 검색어
     * @return 검색 결과 페이지
     */
    @GetMapping("/{categoryId}/search")
    public ModelAndView getBooks(@RequestParam(name = "keyword") String keyword,
                                 @RequestParam(defaultValue="1") int pageSize,
                                 @RequestParam(defaultValue="10") int offset,
                                 @PathVariable(name = "categoryId") Long categoryId) {

        Page<ElasticResponseDto> dtoList = elasticService.searchAll(keyword, categoryId, pageSize - 1, offset);

        ModelAndView modelAndView = new ModelAndView("index/main/search/main_search");
        modelAndView.addObject("bookList", dtoList.getContent());

        ResponseEntity<CategoryResponseDto> categoryResponseEntity = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/" + categoryId,
                CategoryResponseDto.class
        );

        // 하위 카테고리 조회
        ResponseEntity<ParentCategoryResponseDto> response = restTemplate.getForEntity(
                requestUrl + ":" + port +  "/shop/categories/parents/" + categoryId,
                ParentCategoryResponseDto.class
        );

        modelAndView.addObject("totalElements", dtoList.getTotalElements());
        modelAndView.addObject("currentPage", pageSize);
        modelAndView.addObject("pageSize", pageSize);
        modelAndView.addObject("offset", offset);
        modelAndView.addObject("totalPages", dtoList.getTotalPages());
        modelAndView.addObject("keyword", keyword);

        modelAndView.addObject("parentCategory", categoryResponseEntity.getBody());
        modelAndView.addObject("childCategories", response.getBody().getChildCategories());

        return modelAndView;
    }
}
