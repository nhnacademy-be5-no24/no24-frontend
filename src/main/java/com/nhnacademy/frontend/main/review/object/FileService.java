package com.nhnacademy.frontend.main.review.object;

import lombok.Data;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * 설명
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
@Data
public class FileService {
    private String imageUrl;
    private String destinationFile = "image.jpg";

    public InputStream getFileByUrl(String imageUrl) {
        try {
            // URL에서 이미지 다운로드
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            InputStream inputStream = connection.getInputStream();

            return inputStream;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
