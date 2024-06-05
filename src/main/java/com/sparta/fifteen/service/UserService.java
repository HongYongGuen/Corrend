package com.sparta.fifteen.service;

import com.sparta.fifteen.config.JwtConfig;
import com.sparta.fifteen.dto.UserLoginRequestDto;
import com.sparta.fifteen.entity.RefreshToken;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.repository.RefreshTokenRepository;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.InputMismatchException;
import java.util.Optional;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto requestDto) {
        // username 유효성 검사
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 ID");
        }
        // password 유효성 검사
        if (requestDto.getPassword().length() < 10) {
            throw new InputMismatchException("잘못된 비밀번호 형식");
        }
        User user = new User(requestDto);
        user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setStatusCode(String.valueOf(UserStatusEnum.NORMAL.getStatus()));
        userRepository.save(user);
        return new UserRegisterResponseDto(user);
    }

    public String loginUser(UserLoginRequestDto requestDto, HttpServletResponse response) {
        // DB에서 username 먼저 조회
        Optional<User> optionalUser = userRepository.findByUsername(requestDto.getUsername());

        if(optionalUser.isPresent()) {
            User registeredUser = optionalUser.get();
            // User 상태가 WITHDRAW면 예외 처리
            if(registeredUser.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
                throw new IllegalArgumentException("탈퇴한 계정");
            }
            // 토큰 생성 위치
            if(passwordEncoder.matches(requestDto.getPassword(), registeredUser.getPassword())) {
                String accessToken = JwtTokenProvider.generateAccessToken(requestDto.getUsername());
                String refreshToken = JwtTokenProvider.generateRefreshToken();
                Long expirationTime = JwtConfig.staticRefreshTokenExpiration;

                RefreshToken userRefreshToken = registeredUser.getUserRefreshToken();
                if(userRefreshToken == null) {
                    userRefreshToken = RefreshToken.from(registeredUser.getUsername(), refreshToken, expirationTime);
                    registeredUser.setUserRefreshToken(userRefreshToken);
                } else {
                    userRefreshToken.updateRefreshToken(refreshToken);
                }
                userRepository.save(registeredUser);

                // Header에 accessToken 추가
                response.setHeader("Authorization", "Bearer " + accessToken);

                return "성공";
            }
        }
        throw new InputMismatchException("아이디, 패스워드 불일치");
    }

    public void logoutUser(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            System.out.println("User found: " + user.getUsername());

            // RefreshToken 확인
            RefreshToken userRefreshToken = user.getUserRefreshToken();
            if (userRefreshToken != null) {
                System.out.println("RefreshToken found: " + userRefreshToken.getId());

                // 리프레시 토큰 찾기
                RefreshToken refreshToken = refreshTokenService.findRefreshTokenById(userRefreshToken.getId());
                if (refreshToken != null) {
                    System.out.println("RefreshToken in DB found: " + refreshToken.getId());

                    // User의 refreshToken 참조를 null로 설정
                    user.setUserRefreshToken(null);
                    userRepository.save(user); // 변경사항 저장
                    System.out.println("User-RefreshToken association cleared");

                    // RefreshToken 삭제
                    refreshTokenService.deleteRefreshTokenById(refreshToken.getId());
                    System.out.println("RefreshToken deleted: " + refreshToken.getId());
                } else {
                    System.out.println("RefreshToken not found in DB");
                }
            } else {
                // 리프레시 토큰이 없는 경우 처리
                System.out.println("User has no RefreshToken");
            }
        } else {
            System.out.println("User not found");
        }
    }
}