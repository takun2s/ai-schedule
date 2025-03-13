package com.scheduler.controller;

import com.scheduler.common.Result;
import com.scheduler.config.JwtConfig;
import com.scheduler.dto.LoginRequest;
import com.scheduler.dto.LoginResponse;
import com.scheduler.model.User;
import com.scheduler.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtConfig jwtConfig;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        userService.register(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.findByUsername(request.getUsername());
            log.info("Login attempt for username: {}", request.getUsername());
            
            if (user == null) {
                log.warn("User not found: {}", request.getUsername());
                return Result.error(401, "用户名或密码错误");
            }

            boolean passwordValid = userService.validatePassword(user, request.getPassword());
            log.info("Password validation result for user {}: {}", user.getUsername(), passwordValid);

            if (!passwordValid) {
                log.warn("Invalid password for user: {}", user.getUsername());
                return Result.error(401, "用户名或密码错误");
            }

            if (!user.getEnabled()) {
                return Result.error(401, "账号已被禁用");
            }

            userService.updateLoginTime(user);
            
            String token = userService.generateToken(user);
            
            LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();

            return Result.success(response);
        } catch (Exception e) {
            log.error("Login error for user: " + request.getUsername(), e);
            return Result.error(500, "登录异常：" + e.getMessage());
        }
    }
}
