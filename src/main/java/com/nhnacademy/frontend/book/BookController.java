package com.nhnacademy.frontend.book;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

/**
 * Book Detail Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class BookController {
    @GetMapping("/book/{bookId}")
    public ModelAndView getBookPage(@PathVariable("bookId") Long bookId) {
        ModelAndView mav = new ModelAndView("index/book/book");

        return mav;
    }
}
