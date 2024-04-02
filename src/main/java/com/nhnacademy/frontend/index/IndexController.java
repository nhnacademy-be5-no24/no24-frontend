package com.nhnacademy.frontend.index;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
@Slf4j
public class IndexController {
    @GetMapping(value = {"/index.html","/"})
    public String index(){

        return "index/index";
    }
}
