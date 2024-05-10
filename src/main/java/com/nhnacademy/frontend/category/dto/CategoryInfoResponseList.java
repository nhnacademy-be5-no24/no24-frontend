package com.nhnacademy.frontend.category.dto;

import lombok.Data;

import java.util.List;

/**
 * BookList 객체를 받기 위한 dto
 *
 * @Author : 박병휘
 * @Date : 2024/04/24
 */
@Data
public class CategoryInfoResponseList {
    private List<CategoryInfoResponseDto> content;

    public List<CategoryInfoResponseDto> getContent() {
        return content;
    }

    public void setContent(List<CategoryInfoResponseDto> content) {
        this.content = content;
    }
}
