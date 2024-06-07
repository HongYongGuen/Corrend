package com.sparta.fifteen.service;

import com.sparta.fifteen.dto.UserRegisterRequestDto;
import com.sparta.fifteen.dto.UserRegisterResponseDto;
import com.sparta.fifteen.entity.User;
import com.sparta.fifteen.entity.UserStatusEnum;
import com.sparta.fifteen.error.PasswordMismatchException;
import com.sparta.fifteen.error.UserAlreadyExistsException;
import com.sparta.fifteen.error.UserNotFoundException;
import com.sparta.fifteen.error.UserWithdrawnException;
import com.sparta.fifteen.repository.UserRepository;
import com.sparta.fifteen.service.token.LogoutAccessTokenService;
import com.sparta.fifteen.service.token.RefreshTokenService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Timestamp;
import java.util.InputMismatchException;

@Service
@Validated
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final LogoutAccessTokenService logoutAccessTokenService;
    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenService refreshTokenService,
                       LogoutAccessTokenService logoutAccessTokenService,
                       EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.logoutAccessTokenService = logoutAccessTokenService;
        this.emailVerificationService = emailVerificationService;
    }

    public UserRegisterResponseDto registerUser(UserRegisterRequestDto requestDto) {
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            User existingUser = userRepository.findByUsername(requestDto.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));
            if (existingUser.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
                throw new UserWithdrawnException("탈퇴한 ID는 재사용할 수 없습니다.");
            }
            throw new UserAlreadyExistsException("이미 존재하는 ID");
        }

        if (requestDto.getPassword().length() < 10) {
            throw new InputMismatchException("잘못된 비밀번호 형식");
        }

        User user = initializeUser(requestDto);
        userRepository.save(user);

        //emailVerificationService.sendVerificationEmail(user);
        return new UserRegisterResponseDto(user);
    }

    public void withdrawUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("사용자 ID가 존재하지 않습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordMismatchException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatusCode().equals(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()))) {
            throw new UserWithdrawnException("이미 탈퇴한 사용자입니다.");
        }

        user.setStatusCode(String.valueOf(UserStatusEnum.WITHDRAWN.getStatus()));
        user.setModifiedOn(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);

        refreshTokenService.deleteByUser(user);
    }

    private User initializeUser(UserRegisterRequestDto requestDto) {
        User user = new User(requestDto);
        user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setStatusCode(String.valueOf(UserStatusEnum.PENDING.getStatus()));

        String verificationCode = emailVerificationService.generateVerificationCode();
        user.setEmailVerificationCode(verificationCode);
        user.setEmailVerificationSendTime(new Timestamp(System.currentTimeMillis()));
        return user;
    }
}
