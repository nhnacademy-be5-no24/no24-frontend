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
public class BookResponsePage {
    private List<BookResponseDto> bookResponseDtoList;

    public List<BookResponseDto> getBookResponseDtoList() {
        return bookResponseDtoList;
    }

    public void setBookResponseDtoList(List<BookResponseDto> bookResponseDtoList) {
        this.bookResponseDtoList = bookResponseDtoList;
    }
}
