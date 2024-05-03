package com.nhnacademy.frontend.grade.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 등급 응답 dto
 *
 * @Author : 박병휘
 * @Date : 2024/05/03
 */
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GradeResponseDtoList {
    List<GradeResponseDto> gradeResponseDtoList;
}
