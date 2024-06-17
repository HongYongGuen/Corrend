package com.sparta.fifteen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.fifteen.controller.CommentController;
import com.sparta.fifteen.dto.CommentRequestDto;
import com.sparta.fifteen.dto.CommentResponseDto;
import com.sparta.fifteen.entity.NewsFeed;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.service.CommentService;
import com.sparta.fifteen.service.NewsFeedService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebMvcTest(controllers = CommentController.class)
@AutoConfigureMockMvc
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private NewsFeedService newsFeedService;

    @InjectMocks
    private CommentController commentController;

    @Test
    public void testCreateComment() throws Exception {
        Long newsFeedId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setComment("This is a test comment");

        User mockUser = mock(User.class);
        mockUser.setId(1L);

        NewsFeed mockNewsFeed = mock(NewsFeed.class);
        mockNewsFeed.setId(newsFeedId);

        CommentResponseDto responseDto = new CommentResponseDto(
                1L,                     // id
                "Test comment",         // comment
                10L,                    // like
                1L,                     // userId
                1L,                     // newsFeedId
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now()     // updatedAt
        );
        responseDto.setCommentId(1L);
        responseDto.setComment(requestDto.getComment());

        when(newsFeedService.findNewsFeedById(newsFeedId)).thenReturn(mockNewsFeed);
        when(commentService.createComment(any(User.class), any(NewsFeed.class), any(CommentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/{newsfeedId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.commentId").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value(requestDto.getComment()));
    }

    @Test
    public void testGetComments() throws Exception {
        Long newsFeedId = 1L;
        NewsFeed mockNewsFeed = new NewsFeed();
        mockNewsFeed.setId(newsFeedId);

        CommentResponseDto responseDto = new CommentResponseDto(
                1L,                     // id
                "Test comment",         // comment
                10L,                    // like
                1L,                     // userId
                1L,                     // newsFeedId
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now()     // updatedAt
        );
        responseDto.setCommentId(1L);
        responseDto.setComment("Test comment");

        when(newsFeedService.findNewsFeedById(newsFeedId)).thenReturn(mockNewsFeed);
        when(commentService.getComments(mockNewsFeed)).thenReturn(Collections.singletonList(responseDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/comments/{newsfeedId}", newsFeedId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].commentId").value(responseDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].comment").value(responseDto.getComment()));
    }

    @Test
    public void testUpdateComment() throws Exception {
        Long commentId = 1L;
        CommentRequestDto requestDto = new CommentRequestDto();
        requestDto.setComment("Updated comment");

        User mockUser = new User();
        mockUser.setId(1L);

        CommentResponseDto responseDto = new CommentResponseDto(
                1L,                     // id
                "Test comment",         // comment
                10L,                    // like
                1L,                     // userId
                1L,                     // newsFeedId
                LocalDateTime.now(),    // createdAt
                LocalDateTime.now()     // updatedAt
        );
        responseDto.setCommentId(commentId);
        responseDto.setComment(requestDto.getComment());

        when(commentService.updateComment(any(User.class), any(Long.class), any(CommentRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.commentId").value(responseDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.comment").value(responseDto.getComment()));
    }

    @Test
    public void testDeleteComment() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/{commentId}", commentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("댓글 삭제에 성공했습니다."));
    }
}
