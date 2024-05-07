package com.nhnacademy.frontend.elastic.service.impl;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.frontend.elastic.domain.ElasticDocument;
import com.nhnacademy.frontend.elastic.dto.response.ElasticResponseDto;
import com.nhnacademy.frontend.elastic.repository.ElasticRepository;
import com.nhnacademy.frontend.elastic.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elastic 검색 서비스 구현체 입니다.
 *
 * @author : 강병구
 * @date : 2024-04-29
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ElasticServiceImpl implements ElasticService {

    private final ElasticRepository elasticRepository;
    private ObjectMapper objectMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ElasticResponseDto> searchAll(String keyword) {
        List<ElasticDocument> searchBooks = elasticRepository.findAll(keyword);

        return searchBooks.stream()
                .map(this::documentTransfer)
                .collect(Collectors.toList());
    }

    /**
     * 검색 반응 Dto 생성을 위한 메소드입니다.
     * @param document Elastic Search에서 반환받은 document 입니다.
     */
    private ElasticResponseDto documentTransfer(ElasticDocument document) {
        return new ElasticResponseDto(
                document.getBookIsbn(),
                document.getBookTitle(),
                document.getBookDesc(),
                document.getBookPublisher(),
                document.getBookPublishedAt(),
                document.getBookFixedPrice(),
                document.getBookSalePrice(),
                document.getBookViews(),
                document.getAuthor(),
                document.getBookImage(),
                document.getBookStatus(),
                document.getLikes()
        );
    }
}
