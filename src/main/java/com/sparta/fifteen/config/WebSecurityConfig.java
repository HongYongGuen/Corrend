package com.sparta.fifteen.config;

import com.sparta.fifteen.jwt.JwtAuthenticationFilter;
import com.sparta.fifteen.log.LoggingAspect;
import com.sparta.fifteen.security.UserDetailsServiceImpl;
import com.sparta.fifteen.service.LogoutAccessTokenService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableAspectJAutoProxy
public class WebSecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    private final LogoutAccessTokenService logoutAccessTokenService;

    public WebSecurityConfig(UserDetailsServiceImpl userDetailsService, LogoutAccessTokenService logoutAccessTokenService){
        this.userDetailsService = userDetailsService;
        this.logoutAccessTokenService = logoutAccessTokenService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception{
        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception{
        return new JwtAuthenticationFilter(userDetailsService, logoutAccessTokenService);
    }

    @Bean
    public LoggingAspect loggingAspect() throws Exception{
        return new LoggingAspect();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf((csrf) -> csrf.disable());

        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers(HttpMethod.POST,"/api/user/**").permitAll() // '/api/user/'로 시작하는 요청 모두 접근 허가
                        .requestMatchers(HttpMethod.GET, "api/newsfeed/**").permitAll() // 뉴스피드 관련 GET 요청은 인증없이 허용
                        .requestMatchers( "/","/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/api/verify").permitAll()
                        .anyRequest().authenticated()); // 그 외 모든 요청 인증 필요

        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}