package com.nhnacademy.frontend.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.auth.dto.MemberCreateRequest;
import com.nhnacademy.frontend.auth.dto.LoginDto;
import com.nhnacademy.frontend.auth.dto.OAuthRequestDto;
import com.nhnacademy.frontend.auth.dto.TokenDto;
import com.nhnacademy.frontend.main.member.dto.MemberInfoResponseDto;
import com.nhnacademy.frontend.util.AuthUtil;
import com.nhnacademy.frontend.util.AuthorizationDto;
import com.nhnacademy.frontend.util.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.nhnacademy.frontend.util.AuthUtil.createKey;

/**
 * Login Controller
 *
 * @Author : 박병휘
 * @Date : 2024/04/04
 */
@Controller
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
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

    @Value("${email.address}")
    private String fromEmail;

    @Value("${email.password}")
    private String password;

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
            String email = member.get("email") == null ? "미입력" : member.get("email").asText();
            String mobile = member.get("mobile") == null ? "미입력" : member.get("mobile").asText();
            String name = member.get("name") == null ? "미입력" : member.get("name").asText();
            LocalDate birthday = null;

            try {
                int year = LocalDate.now().getYear() - Integer.parseInt(member.get("ageGroup").asText());
                int month = Integer.parseInt(member.get("birthdayMMdd").asText().substring(0, 2));
                int day = Integer.parseInt(member.get("birthdayMMdd").asText().substring(2));
                birthday = LocalDate.of(year, month, day);
            } catch(Exception e) {
                birthday = LocalDate.now();
            }

            boolean isExist = restTemplate.getForEntity(
                    requestUrl + ":" + port +  "/auth/member/exist/" + idNo,
                    Boolean.class
            ).getBody();

            System.out.println("register id");
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
            System.out.println("register end");
            System.out.println("login");

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
            System.out.println("login end");

            // header의 authorization와 refreshToken을 얻어온다.
            HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
            String authorization = responseEntity.getHeaders().get("Authorization").get(0);
            String refreshToken = responseEntity.getHeaders().get("RefreshToken").get(0);

            // Redis session에 저장.
            hashOperations.put(jSessionId, "Authorization", authorization);
            hashOperations.put(jSessionId, "RefreshToken", refreshToken);

            return mav;
        } catch(Exception e) {
            System.out.println(("error: " + e.getMessage()));
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
    public ModelAndView login(HttpServletRequest httpServletRequest) {
        ModelAndView mav = new ModelAndView("redirect:/");
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
        String status = responseEntity.getHeaders().get("MemberStatus").get(0);

        if(!status.equals("ACTIVE")) {
            if(status.equals("INACTIVE")) {
                String token = authorization.substring(7);
                ResponseEntity<MemberInfoResponseDto> memberDto = restTemplate
                        .getForEntity(requestUrl + ":" + port + "/auth/member/token/" + token, MemberInfoResponseDto.class);

                mav.setViewName("index/auth/member_active");
                mav.addObject("member", memberDto.getBody());
            }
            else {
                if(status.equals("LEAVE"))
                    mav.addObject("message", "탈퇴한 회원입니다.");
                else if(status.equals("BAN"))
                    mav.addObject("message", "차단된 회원입니다.");
                mav.setViewName("index/auth/login");
            }
            return mav;
        }

        // Redis session에 저장.
        hashOperations.put(jSessionId, "Authorization", authorization);
        hashOperations.put(jSessionId, "RefreshToken", refreshToken);

        String value = (String) redisTemplate.opsForHash().get(jSessionId, "Authorization");

        return mav;
    }

    @PostMapping("/send/code")
    public ResponseEntity<Void> sendCode(@RequestBody EmailDto emailDto) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        AuthUtil authUtil = new AuthUtil();

        // redis에 activeKey 저장
        String activeKey = createKey();

        hashOperations.delete("activeKey-" + emailDto.getMemberId(), "activeKey");

        hashOperations.put("activeKey-" + emailDto.getMemberId(), "activeKey", activeKey);
        redisTemplate.expire("activeKey-" + emailDto.getMemberId(), 5, TimeUnit.MINUTES);

        // email 전송
        authUtil.sendEmail(fromEmail, password, emailDto.getEmail(), "No24 Bookstore 휴면 해제 인증 키", "인증 키는 " + activeKey + " 입니다.");

        return ResponseEntity.ok().build();
    }

    @PostMapping("/member/active")
    public ResponseEntity<Void> memberActive(@RequestBody AuthorizationDto authorizationDto) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        AuthUtil authUtil = new AuthUtil();

        String code = hashOperations.get("activeKey-" + authorizationDto.getMemberId(), "activeKey");

        if(code.equals(authorizationDto.getAuthorizationCode())) {
            restTemplate.postForEntity(
                    requestUrl + ":" + port + "/auth/member/changeToActive",
                    authorizationDto.getMemberId(),
                    null
            );

            hashOperations.delete("activeKey-" + authorizationDto.getMemberId(), "activeKey");

            return ResponseEntity.ok().build();
        }
        else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/register")
    public String createMember(@ModelAttribute MemberCreateRequest memberCreateRequest) {
        ResponseEntity<MemberCreateRequest> response = restTemplate.postForEntity(
                requestUrl + ":" + port + "/auth/member/create",
                memberCreateRequest,
                MemberCreateRequest.class
        );

        // 포인트 적립
        restTemplate.postForEntity(
                requestUrl + ":" + port + "/shop/points/id/" + memberCreateRequest.getCustomerId(),
                null,
                null
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
