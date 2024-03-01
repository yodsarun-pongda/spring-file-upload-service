package com.yodsarun.demo.spring.interview.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yodsarun.demo.spring.interview.exception.AuthenticationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class AuthenticateConfig implements HandlerInterceptor {
    @Value("${app-config.auth.auth-key}")
    private String authKey;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        var header = request.getHeader("auth-key");
        if (ObjectUtils.isEmpty(header) || !header.equals(authKey)) {
            throw new AuthenticationException("Unauthorized", HttpStatus.FORBIDDEN);
        }

        return true;
    }
}
