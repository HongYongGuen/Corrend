package com.sparta.fifteen;

import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.entity.Comment;
import com.sparta.fifteen.entity.LikeComment;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LikeCommentTest {

    @Test
    public void testCreateLikeComment() {
        // Given
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("StrongPassword123!");
        requestDto.setEmail("test@example.com");

        User user = new User(requestDto); // Dummy user for test
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setComment("test comment");
        Comment comment = new Comment(commentRequestDto.getComment(), user, new NewsFeed());
        LikeComment likeComment = new LikeComment(user, comment);

        // Then
        assertNotNull(likeComment.getUser());
        assertEquals(comment, likeComment.getComment());
    }
}
