package com.nhnacademy.frontend.elastic.repository;

import com.nhnacademy.frontend.elastic.domain.ElasticDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Elastic 검색 레포지토리 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */
@Repository
public interface ElasticRepository extends ElasticsearchRepository<ElasticDocument, String>, ElasticRepositoryCustom {
}
