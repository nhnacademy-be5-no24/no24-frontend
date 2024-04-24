package com.nhnacademy.frontend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 *  Redis 데이터 저장 및 조회가 되는지 확인하는 controller.
 *
 *  @Author : jinjiwon
 *  @Date : 25/03/2024
 */
@RequiredArgsConstructor
@RequestMapping("/redis")
@RestController
public class RedisController {
    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/get/{key}")
    public ResponseEntity<Object> getRedisKey(@PathVariable String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String value = valueOperations.get(key);
        return ResponseEntity.status(HttpStatus.OK).body(value);
    }

    @PostMapping("/set/{key}")
    public ResponseEntity<Object> setRedisKey(@PathVariable String key) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(key, "20240412 test");
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
