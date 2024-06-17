package com.sparta.fifteen.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private String comment;

    public void setComment(String updatedComment) {
        this.comment = updatedComment;
    }
}
