package com.sparta.fifteen.entity;

import com.sparta.fifteen.dto.NewsFeedRequestDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class NewsFeedTest {

    @Test
    public void testCreateNewsFeedWithDto() {
        // Given
        NewsFeedRequestDto requestDto = new NewsFeedRequestDto();
        requestDto.setAuthorId(1L);
        requestDto.setContent("Test content");

        // When
        NewsFeed newsFeed = new NewsFeed(requestDto);

        // Then
        assertNotNull(newsFeed);
        assertEquals(1L, newsFeed.getAuthorId());
        assertEquals("Test content", newsFeed.getContent());

    }

    @Test
    public void testUpdateLikes() {
        // Given
        NewsFeed newsFeed = new NewsFeed();
        newsFeed.setLikes(5L);

        // When
        newsFeed.updateLikes(3L);

        // Then
        assertEquals(8L, newsFeed.getLikes());
    }

    @Test
    public void testEmptyConstructor() {
        // Given, When
        NewsFeed newsFeed = new NewsFeed();

        // Then
        assertEquals(1L, newsFeed.getAuthorId()); // Default authorId value
        assertEquals("", newsFeed.getContent()); // Default content value
        assertEquals(0L, newsFeed.getLikes()); // Default likes value
    }
}