package com.nhnacademy.frontend.book.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookTagResponseDto {
    private Long tagId;
    private String tagName;
}
