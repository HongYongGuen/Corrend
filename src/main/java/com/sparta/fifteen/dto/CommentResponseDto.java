package com.sparta.fifteen.dto;

import com.sparta.fifteen.entity.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String comment;
    private Long like;
    private Long userId;
    private Long newsFeedId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponseDto(Long id, String comment, Long like, Long userId, Long newsFeedId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.comment = comment;
        this.like = like;
        this.userId = userId;
        this.newsFeedId = newsFeedId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static CommentResponseDto toDto(Comment comment) {
        return new CommentResponseDto(comment.getId(), comment.getComment(), comment.getLikes(), comment.getUser().getId(), comment.getNewsfeed().getId(), comment.getCreatedAt(), comment.getModifiedAt());
    }

    public void setCommentId(long l) {
        this.id = l;
    }


}
