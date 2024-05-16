package com.nhnacademy.frontend.elastic.repository.impl;

import com.nhnacademy.frontend.elastic.domain.ElasticDocument;
import com.nhnacademy.frontend.elastic.repository.ElasticRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elastic 검색 Custom Repository 구현체 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */

@RequiredArgsConstructor
public class ElasticRepositoryImpl implements ElasticRepositoryCustom {
    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<ElasticDocument> findAll(String keyword, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.boolQuery()
                        .should(QueryBuilders.matchQuery("book_title.jaso", keyword))
                        .should(QueryBuilders.matchQuery("book_title", keyword))
                        .should(QueryBuilders.matchQuery("book_title.ngram", keyword))
                        .should(QueryBuilders.matchQuery("book_desc", keyword))
                        .should(QueryBuilders.matchQuery("book_desc.jaso", keyword))
                        .should(QueryBuilders.matchQuery("book_desc.ngram", keyword))
                        .should(QueryBuilders.matchQuery("author", keyword))
                        .should(QueryBuilders.matchQuery("author.jaso", keyword))
                        .should(QueryBuilders.matchQuery("author.ngram", keyword))
                        .should(QueryBuilders.matchQuery("book_isbn", keyword))
                        .should(QueryBuilders.matchQuery("book_publisher", keyword))
                        .should(QueryBuilders.matchQuery("book_publisher.jaso", keyword))
                        .should(QueryBuilders.matchQuery("book_publisher.ngram", keyword))
                )
                .withPageable(pageable)
                .build();

        SearchHits<ElasticDocument> searchHits = elasticsearchOperations.search(nativeSearchQuery, ElasticDocument.class);

        List<ElasticDocument> searchResults = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        return new PageImpl<>(searchResults, pageable, searchHits.getTotalHits());
    }
}
