package com.sparta.fifteen;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.entity.Comment;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentTest {

    @Test
    public void testUpdateComment() {
        // Given
        User user = new User();
        NewsFeed newsFeed = new NewsFeed();
        Comment comment = new Comment("Old comment", user, newsFeed);
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setComment("Updated comment");

        // When
        comment.update(requestDto);

        // Then
        assertEquals("Updated comment", comment.getComment());
    }

    @Test
    public void testUpdateLikes() {
        // Given
        User user = new User();
        NewsFeed newsFeed = new NewsFeed();
        Comment comment = new Comment("Test comment", user, newsFeed);

        // When
        comment.updatelikes(5L);

        // Then
        assertEquals(5L, comment.getLikes());
    }
}
