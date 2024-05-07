package com.nhnacademy.frontend.elastic.controller;

import com.nhnacademy.frontend.elastic.dto.response.ElasticResponseDto;
import com.nhnacademy.frontend.elastic.service.ElasticService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final ElasticService elasticService;

    /**
     * 상품 제목 검색 <br/>
     * 메인화면에서 검색을 처리하는 메소드 입니다.
     *
     * @param keyword 검색어
     * @return 검색 결과 페이지
     */
    @GetMapping("/search")
    public ModelAndView getBooks(@RequestParam(name = "keyword") String keyword) {
        List<ElasticResponseDto> dtoList = elasticService.searchAll(keyword);

        ModelAndView modelAndView = new ModelAndView("index/main/search/main_search");
        modelAndView.addObject("bookList", dtoList);

        return modelAndView;
    }
}
