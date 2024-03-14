package com.nhnacademy.hello.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Controller
public class IndexController {
    @GetMapping(value = {"/index.html","/"})
    public ModelAndView index() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();

        ModelAndView mav = new ModelAndView("index/index");
        mav.addObject("ip", localHost.getHostAddress());

        return mav;
    }
}
