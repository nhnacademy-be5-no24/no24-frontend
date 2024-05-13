package com.nhnacademy.frontend.loaderio;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 부하 테스트 컨트롤러
 *
 * @Author : 박병휘
 * @Date : 2024/05/13
 */
@RestController
public class LoadTestController {

    @GetMapping("/loaderio-204381adb0eab139a78a39d3acb01d6b/")
    public String LoadTest() {
        return "loaderio-204381adb0eab139a78a39d3acb01d6b<br>";
    }
}
