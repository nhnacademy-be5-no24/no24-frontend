package com.nhnacademy.hello.index;

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
    public ModelAndView index(HttpServletRequest request) throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        ModelAndView mav = new ModelAndView("index/index");
        mav.addObject("ip", localHost.getHostAddress());
        mav.addObject("port", request.getServerPort());

        log.info("hello world");

        return mav;
    }
}
