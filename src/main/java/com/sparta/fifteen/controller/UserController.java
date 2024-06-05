package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.UserLoginRequestDto;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.security.UserDetailsImpl;
import com.sparta.fifteen.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.InputMismatchException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    private ResponseEntity<?> signup(@RequestBody UserRegisterRequestDto requestDto) {
        try {
            UserRegisterResponseDto responseDto = userService.registerUser(requestDto);
            return ResponseEntity.ok().body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("이미 존재하는 User ID 입니다. 회원가입에 실패하셨습니다.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("잘못된 비밀번호 형식입니다. 회원가입에 실패하셨습니다.");
        }
    }

    @PostMapping("/login")
    private ResponseEntity<?> userLogin(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        try {
            String token = userService.loginUser(requestDto, response);
            return ResponseEntity.ok().body(token);
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요. 로그인에 실패하셨습니다.");
        }
    }

    @PostMapping("/logout")
    private ResponseEntity<?> userLogout(@RequestBody UserLoginRequestDto requestDto, HttpServletResponse response) {
        try {
            // UserDetails에서 사용자 이름 가져오기
            String userDetailsUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            // requestDto에서 사용자 이름 가져오기
            String requestDtoUsername = requestDto.getUsername();

            // 사용자 이름이 일치하는지 확인
            if (userDetailsUsername.equals(requestDtoUsername)) {
                // 로그아웃 실행
                userService.logoutUser(requestDtoUsername);
            }
            return ResponseEntity.ok().body("로그아웃되었습니다.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요. 로그인에 실패하셨습니다.");
        }
    }
}
