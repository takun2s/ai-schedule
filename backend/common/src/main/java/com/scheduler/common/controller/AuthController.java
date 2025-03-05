package com.scheduler.common.controller;

import com.scheduler.common.api.Result;
import com.scheduler.common.model.SysUser;
import com.scheduler.common.service.SysUserService;
import com.scheduler.common.util.JwtTokenUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@Tag(name = "认证管理")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private SysUserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, String>> login(@RequestBody SysUser user) {
        // 进行身份验证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 生成token
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(userDetails);

        // 返回结果
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return Result.success(result);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/info")
    public Result<SysUser> getInfo() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Result.success(userService.getByUsername(username));
    }

    @Operation(summary = "修改密码")
    @PostMapping("/password")
    public Result<Void> updatePassword(@RequestBody Map<String, String> params) {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        Long userId = Long.parseLong(params.get("userId"));
        userService.updatePassword(userId, oldPassword, newPassword);
        return Result.success();
    }
} 