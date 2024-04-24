package com.nhnacademy.frontend.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CreateTagResponseDto
 *
 * @Author : 박병휘
 * @Date : 2024/03/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDto {
    private Long tagId;
    private String tagName;
}
