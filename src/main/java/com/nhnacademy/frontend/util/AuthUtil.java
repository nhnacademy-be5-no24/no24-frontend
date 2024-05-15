package com.nhnacademy.frontend.util;

import com.nhnacademy.frontend.util.exception.NotFoundToken;
import com.nhnacademy.frontend.util.exception.UnauthorizedTokenException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Properties;

/**
 * Auth에 대한 Util 클래스
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
public class AuthUtil {
    public static Long getCustomerNo(String requestUrl,
                              String port,
                              HttpServletRequest request,
                              RedisTemplate redisTemplate,
                              RestTemplate restTemplate) {
        HttpSession session = request.getSession();
        String jSessionId = session.getId();

        // get user information
        HashOperations<String, String, String> authHashOperations = redisTemplate.opsForHash();
        String authorization = authHashOperations.get(jSessionId, "Authorization");
        String refreshToken = authHashOperations.get(jSessionId, "RefreshToken");

        if(authorization == null || authorization.equals("")) {
            throw new NotFoundToken("Token is not found");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", authorization);
        headers.add("RefreshToken", refreshToken);

        // get customerNo
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(
                requestUrl + ":" + port +  "/auth/get/customerNo",
                new HttpEntity<>(null, headers),
                Long.class
        );

        if(responseEntity.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new UnauthorizedTokenException("Unauthorized token");
        }

        Long customerNo = responseEntity.getBody();

        return customerNo;
    }

    public static String createKey() {
        // 랜덤 객체 생성
        SecureRandom random = new SecureRandom();

        // 6자리 숫자 코드 생성
        int min = 100000; // 최소값
        int max = 999999; // 최대값
        int code = random.nextInt(max - min + 1) + min;

        return String.valueOf(code);
    }

    public boolean sendEmail(String fromEmail, String password, String receiverEmail, String subject, String text) {
        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.setProperty("mail.smtp.host", host);
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");

        // 보내는 사람 계정 정보 설정
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });


        // 메일 내용 작성
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiverEmail));
            msg.setSubject(subject);
            msg.setContent(text,  "text/html; charset=utf-8");

            // 메일 보내기
            Transport.send(msg);
        } catch(MessagingException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
}
