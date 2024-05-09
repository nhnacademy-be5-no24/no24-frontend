package com.nhnacademy.frontend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * ElasticSearch Server 연결 정보 설정하기 위한 Config 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-26
 */

@Configuration
@ConfigurationProperties(prefix = "elastic")
public class ElasticConfig {
    private String url;
    private String bookIndexName;

    /**
     * ElasticSearch URL 조회.
     *
     * @return elasticsearch url
     */
    public String getUrl() {
        return url;
    }

    /**
     * url 세팅.
     *
     * @param url elasticsearch url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 검색할 ElasticSearch 도서 인덱스 조회.
     *
     * @return 인덱스명
     */
    public String getBookIndexName() {
        return bookIndexName;
    }

    /**
     * 상품 인덱스명 세팅.
     *
     * @param bookIndexName 인덱스명
     */
    public void setBookIndexName(String bookIndexName) {
        this.bookIndexName = bookIndexName;
    }
}
