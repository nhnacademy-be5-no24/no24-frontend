package com.nhnacademy.frontend.auth;

import com.nhnacademy.frontend.auth.dto.MemberCreateRequest;
import com.nhnacademy.frontend.auth.dto.LoginDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 * Login Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class LoginController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        ModelAndView mav = new ModelAndView("index/auth/login");

        return mav;
    }

    @GetMapping("/registerPage")
    public ModelAndView getRegisterPage() {
        ModelAndView mav = new ModelAndView("index/auth/register");
        return mav;
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginDto loginDto) {
        System.out.println(loginDto.toString());
        restTemplate.postForEntity(
                "http://localhost:8081/auth/member/login",
                loginDto,
                LoginDto.class
        );
        return "index/auth/loginSuccess";
    }

    @PostMapping("/register")
    public String createMember(@ModelAttribute MemberCreateRequest memberCreateRequest) {
        restTemplate.postForEntity(
                "http://localhost:8081/auth/member/create",
                memberCreateRequest,
                MemberCreateRequest.class
        );
        return "index/auth/registerSuccess";
    }
}
