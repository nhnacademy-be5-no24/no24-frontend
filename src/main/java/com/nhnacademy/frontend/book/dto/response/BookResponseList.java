package com.nhnacademy.frontend.book.dto.response;

import lombok.Data;

import java.util.List;

/**
 * BookList 객체를 받기 위한 dto
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
@Data
public class BookResponseList {
    private List<BookResponseDto> content;

    public List<BookResponseDto> getContent() {
        return content;
    }

    public void setContent(List<BookResponseDto> content) {
        this.content = content;
    }
}
