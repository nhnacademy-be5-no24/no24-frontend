package com.nhnacademy.frontend.elastic.repository;

import com.nhnacademy.frontend.elastic.domain.ElasticDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.util.List;

/**
 * Elastic 검색 NativeQuery를 사용하기 위한 interface 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */

public interface ElasticRepositoryCustom {
    /**
     * 도서 검색
     * @param keyword 검색을 위한 검색어 입니다.
     * @return 검색 결과를 반환합니다.
     */
    List<ElasticDocument> findAll(String keyword);
}
