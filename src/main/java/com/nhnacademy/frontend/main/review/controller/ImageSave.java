package com.nhnacademy.frontend.main.review.controller;

import com.nhnacademy.frontend.main.review.auth.AuthService;
import com.nhnacademy.frontend.main.review.object.FileService;
import com.nhnacademy.frontend.main.review.object.ObjectService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
public class ImageSave {

    @Value("${nhncloud.auth.url}")
    private String authUrl;
    @Value("${nhncloud.auth.tenant-id}")
    private String tenantId = "{Tenant ID}";
    @Value("${nhncloud.auth.username}")
    private String username = "{NHN Cloud Account}";
    @Value("${nhncloud.auth.password}")
    private String password = "{API Password}";
    @Value("${nhncloud.storage.url}")
    private String storageUrl;
    @Value("${nhncloud.storage.container}")
    private String containerName;

    private AuthService authService;
    private FileService fileService;
    private ObjectService objectService;

    @PostMapping("/upload-image")
    public String uploadImage(@RequestParam("file") MultipartFile imageFile, @RequestParam("bookIsbn") String bookIsbn) {
        try {
            // api url 설정
            authService = new AuthService(authUrl, tenantId, username, password);
            fileService = new FileService();
            String token = new JSONObject(authService.requestToken())
                    .getJSONObject("access")
                    .getJSONObject("token")
                    .getString("id");


            objectService = new ObjectService(storageUrl, token);

            RestTemplate restTemplate = new RestTemplate();

            String objectName = bookIsbn + "." + imageFile.getOriginalFilename().split("\\.")[1];

            // 파일 업로드
            InputStream inputStream = imageFile.getInputStream();
            objectService.uploadObject(containerName, objectName, inputStream);

            return objectName;
        } catch (IOException e) {
            // 파일을 찾을 수 없는 경우 예외 처리
            e.printStackTrace();
            return null;
        }
    }
}

