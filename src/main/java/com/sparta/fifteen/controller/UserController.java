package com.sparta.fifteen.controller;

import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
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
    private ResponseEntity<?> signup(@RequestBody @Valid UserRegisterRequestDto requestDto) {
        try {
            UserRegisterResponseDto responseDto = userService.registerUser(requestDto);
            return ResponseEntity.ok().body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("이미 존재하는 User ID 입니다. 회원가입에 실패하셨습니다.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("잘못된 비밀번호 형식입니다. 회원가입에 실패하셨습니다.");
        }
    }
}
