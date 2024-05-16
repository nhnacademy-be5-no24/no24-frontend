package com.nhnacademy.frontend.elastic.service;

import com.nhnacademy.frontend.elastic.dto.response.ElasticResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Elastic 검색 서비스 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */
public interface ElasticService {
    /**
     * 도서 검색
     * @param keyword 검색을 위한 검색어 입니다.
     * @return 검색 결과 리스트를 반환합니다.
     */
    Page<ElasticResponseDto> searchAll(String keyword, int pageSize, int offset);
}
