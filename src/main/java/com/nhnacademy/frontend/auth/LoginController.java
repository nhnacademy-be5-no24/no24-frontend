package com.nhnacademy.frontend.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.dto.MemberCreateRequest;
import com.nhnacademy.frontend.auth.dto.LoginDto;
import com.nhnacademy.frontend.auth.dto.OAuthRequestDto;
import com.nhnacademy.frontend.auth.dto.TokenDto;
import com.nhnacademy.frontend.util.AuthUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Value("${payco.url}")
    private String paycoUrl;

    @Value("${payco.client.id}")
    private String clientId;

    @Value("${payco.client.secret}")
    private String clientSecret;

    @Value("${payco.password}")
    private String paycoPassword;

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

        mav.addObject("paycoUrl", paycoUrl);

        return mav;
    }

    @GetMapping("/auth")
    public ModelAndView getAuthPage(HttpServletRequest httpServletRequest,
                                    @RequestParam String code,
                                    @RequestParam String state) {
        ObjectMapper objectMapper = new ObjectMapper();
        ModelAndView mav = new ModelAndView("redirect:/");
        String url = "https://id.payco.com/oauth2.0/token?"
                + "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&state=" + state
                + "&code=" + code;

        try {
            JsonNode jsonResponse = objectMapper.readTree(
                    restTemplate.getForEntity(
                            url,
                            String.class
                    ).getBody()
            );

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            httpHeaders.set("client_id", clientId);
            httpHeaders.set("access_token", jsonResponse.get("access_token").asText());

            jsonResponse = objectMapper.readTree(
                    restTemplate.postForEntity(
                            "https://apis-payco.krp.toastoven.net/payco/friends/find_member_v2.json",
                            new HttpEntity<>(null, httpHeaders),
                            String.class
                    ).getBody()
            );

            JsonNode member = jsonResponse.get("data").get("member");
            String idNo = member.get("idNo").asText();
            String email = member.get("email").asText();
            String mobile = member.get("mobile").asText();
            String name = member.get("name").asText();

            int year = LocalDate.now().getYear() - Integer.parseInt(member.get("ageGroup").asText());
            int month = Integer.parseInt(member.get("birthdayMMdd").asText().substring(0, 2));
            int day = Integer.parseInt(member.get("birthdayMMdd").asText().substring(2));
            LocalDate birthday = LocalDate.of(year, month, day);

            boolean isExist = restTemplate.getForEntity(
                    requestUrl + ":" + port +  "/auth/member/exist/" + idNo,
                    Boolean.class
            ).getBody();

            // 유저 정보가 없는 경우 회원가입
            if(!isExist) {
                MemberCreateRequest memberCreateRequest = MemberCreateRequest.builder()
                        .customerId(idNo)
                        .customerPassword(idNo + paycoPassword)
                        .customerName(name)
                        .customerPhoneNumber(mobile)
                        .customerEmail(email)
                        .customerBirthday(birthday)
                        .build();

                restTemplate.postForEntity(
                        requestUrl + ":" + port + "/auth/member/create",
                        memberCreateRequest,
                        MemberCreateRequest.class
                );
            }

            // 로그인
            RestTemplate restTemplate = new RestTemplate();
            HttpSession session = httpServletRequest.getSession();
            String jSessionId = session.getId();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 요청 본문 데이터 설정 (form-data)
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            map.add("username", idNo);
            map.add("password", idNo + paycoPassword);

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

            return mav;
        } catch(Exception e) {
            mav.setViewName("redirect:/error");
            return mav;
        }
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
