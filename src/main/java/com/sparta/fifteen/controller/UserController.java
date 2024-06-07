package com.sparta.fifteen.controller;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.dto.UserRequestDto;
import com.sparta.fifteen.service.AuthenticationService;
import com.sparta.fifteen.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.InputMismatchException;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    public UserController(UserService userService, AuthenticationService authenticationService) {
        this.userService = userService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/user")
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

    @PostMapping("/user/login")
    private ResponseEntity<?> userLogin(@RequestBody UserRequestDto requestDto) {
        try {
            String token = authenticationService.loginUser(requestDto);
            return ResponseEntity.ok().header(JwtConfig.staticHeader, JwtConfig.staticTokenPrefix + token).body(token);
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요. 로그인에 실패하셨습니다.");
        }
    }

    @PostMapping("/user/logout")
    private ResponseEntity<?> userLogout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String accessToken = authorizationHeader.replace(JwtConfig.staticTokenPrefix, "");

            // UserDetails에서 사용자 이름 가져오기
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println(accessToken + ", " + username);
            // requestDto에서 사용자 이름 가져오기
            //String requestDtoUsername = requestDto.getUsername();

            authenticationService.logoutUser(accessToken, username);
            // 사용자 이름이 일치하는지 확인
            //if (userDetailsUsername.equals(requestDtoUsername)) {
                // 로그아웃 실행

            //}
            return ResponseEntity.ok().body("로그아웃되었습니다.");
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요. 로그인에 실패하셨습니다.");
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        String newAccessToken = authenticationService.refreshToken(request);
        if (newAccessToken != null){
            return ResponseEntity.ok()
                    .header(JwtConfig.staticHeader, JwtConfig.staticTokenPrefix + newAccessToken)
                    .body(newAccessToken);
        }else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token.");
        }
    }

    @PutMapping("/user")
    public ResponseEntity<?> userWithdraw(@RequestBody UserRequestDto requestDto) {
        try {
            // UserDetails에서 현재 로그인한 사용자 이름 가져오기
            String userDetailsUsername = SecurityContextHolder.getContext().getAuthentication().getName();
            // 요청된 DTO에서 사용자 이름 가져오기
            String requestDtoUsername = requestDto.getUsername();

            // 현재 로그인한 사용자와 탈퇴하려는 사용자가 같은지 확인
            if (userDetailsUsername.equals(requestDtoUsername)) {
                userService.withdrawUser(requestDtoUsername, requestDto.getPassword());
                return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
            } else {
                // 현재 로그인한 사용자와 탈퇴하려는 사용자가 다른 경우
                return ResponseEntity.badRequest().body("현재 로그인한 사용자가 아닙니다.");
            }
        } catch (InputMismatchException e) {
            return ResponseEntity.badRequest().body("아이디 또는 비밀번호를 확인해주세요.");
        }
    }

}
