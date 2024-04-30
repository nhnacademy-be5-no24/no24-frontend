package com.nhnacademy.frontend.auth;

import com.nhnacademy.frontend.auth.dto.MemberCreateRequest;
import com.nhnacademy.frontend.auth.dto.LoginDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * Login Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class LoginController {
    @Value("${request.url}")
    private String requestUrl;

    @Value("${request.port}")
    private String port;

    private final RestTemplate restTemplate;
    private final RedisTemplate redisTemplate;

    public LoginController(RestTemplate restTemplate, RedisTemplate redisTemplate) {
        this.restTemplate = restTemplate;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(HttpServletRequest httpServletRequest) {
        ModelAndView mav = new ModelAndView("index/auth/login");

        HttpSession session = httpServletRequest.getSession();
        String jSessionId = session.getId();
        String value = (String) redisTemplate.opsForHash().get(jSessionId, "Authorization");

        if(value != null) {
            mav.setViewName("redirect:/");
        }

        return mav;
    }

    @GetMapping("/registerPage")
    public ModelAndView getRegisterPage() {
        ModelAndView mav = new ModelAndView("index/auth/register");
        return mav;
    }

    @PostMapping("/login")
    public String login(HttpServletRequest httpServletRequest) {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();
        HttpSession session = httpServletRequest.getSession();
        String jSessionId = session.getId();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 요청 본문 데이터 설정 (form-data)
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("username", httpServletRequest.getParameter("username"));
        map.add("password", httpServletRequest.getParameter("password"));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

        ResponseEntity responseEntity = restTemplate.postForEntity(
                requestUrl + ":" + port +  "/login",
                request,
                LoginDto.class
        );

        // header의 authorization와 refreshToken을 얻어온다.
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String authorization = responseEntity.getHeaders().get("Authorization").get(0);
        String refreshToken = responseEntity.getHeaders().get("RefreshToken").get(0);

        // Redis session에 저장.
        hashOperations.put(jSessionId, "Authorization", authorization);
        hashOperations.put(jSessionId, "RefreshToken", refreshToken);

        String value = (String) redisTemplate.opsForHash().get(jSessionId, "Authorization");

        return "redirect:/";
    }

    @PostMapping("/register")
    public String createMember(@ModelAttribute MemberCreateRequest memberCreateRequest) {
        restTemplate.postForEntity(
                requestUrl + ":" + port + "/auth/member/create",
                memberCreateRequest,
                MemberCreateRequest.class
        );
        return "index/auth/registerSuccess";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();
        String jSessionId = session.getId();
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();

        hashOperations.delete(jSessionId, "Authorization");
        hashOperations.delete(jSessionId, "RefreshToken");

        return "redirect:/";
    }
}
