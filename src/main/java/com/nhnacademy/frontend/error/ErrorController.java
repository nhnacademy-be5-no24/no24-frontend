package com.nhnacademy.frontend.error;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/05/15
 */
@Controller
public class ErrorController {
    @GetMapping("/error")
    public String error() {
        return "index/error";
    }
}
