package com.sparta.fifteen;

import com.sparta.fifteen.dto.ProfileRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.service.ProfileService;
import com.sparta.fifteen.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Timestamp;
import java.util.InputMismatchException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;


    @Test
    public void testUserConstructor() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("StrongPassword123!");
        requestDto.setEmail("test@example.com");

        User user = new User(requestDto);

        assertEquals("testUser", user.getUsername());
        assertEquals("StrongPassword123!", user.getPassword());
        assertEquals("test@example.com", user.getEmail());

    }

    @Test
    public void testUpdateProfile() {
        UserRegisterRequestDto requestDto = new UserRegisterRequestDto();
        requestDto.setUsername("testUser");
        requestDto.setPassword("StrongPassword123!");
        requestDto.setEmail("test@example.com");

        User user = new User(requestDto);

        ProfileRequestDto profileDto = new ProfileRequestDto();
        profileDto.setName("UpdatedName");
        profileDto.setOneline("Updated one-liner");
        profileDto.setCurrentPassword("StrongPassword123!");
        profileDto.setNewPassword("NewStrongPassword123!");
        profileDto.setCheckNewPassword("NewStrongPassword123!");

        user.updateProfile(profileDto);

        assertEquals("UpdatedName", user.getName());
        assertEquals("Updated one-liner", user.getOneLine());
        assertEquals("NewStrongPassword123!", user.getPassword());
        assertNotNull(user.getModifiedOn());
    }

    @Test
    public void testUserNotFound() {
        // Prepare
        String nonExistingUsername = "nonExistingUser";
        ProfileRequestDto profileDto = new ProfileRequestDto();

        // Validate that InputMismatchException is thrown when user is not found
        InputMismatchException thrown = assertThrows(InputMismatchException.class, () -> profileService.updateProfile(nonExistingUsername, profileDto));
        assertEquals("사용자가 없습니다.", thrown.getMessage());
    }




}
