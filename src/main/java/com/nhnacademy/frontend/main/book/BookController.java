package com.nhnacademy.frontend.main.book;

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
    @GetMapping("/book/{categoryId}/{bookId}")
    public ModelAndView getBookPage(@PathVariable("categoryId") Long categoryId,
                                    @PathVariable("bookId") Long bookId) {
        ModelAndView mav = new ModelAndView("index/main/book/book");

        return mav;
    }

    @GetMapping("/book/{category}")
    public ModelAndView getBookListPage(@PathVariable("category") String category){
        ModelAndView mav = new ModelAndView("index/main/book/book_list");

        return mav;
    }
}
