package com.scheduler.service.impl;

import com.scheduler.model.User;
import com.scheduler.repository.UserRepository;
import com.scheduler.service.UserService;
import com.scheduler.config.JwtConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtConfig jwtConfig;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    @Transactional
    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(User user) {
        User existing = findById(user.getId());
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(existing.getPassword());
        }
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updateLoginTime(User user) {
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void setEnabled(Long userId, boolean enabled) {
        User user = findById(userId);
        user.setEnabled(enabled);
        userRepository.save(user);
    }

    @Override
    public boolean validatePassword(User user, String password) {
        String encodedPassword = user.getPassword();
        log.debug("Validating password for user: {}", user.getUsername());
        log.debug("Encoded password from DB: {}", encodedPassword);
        boolean matches = passwordEncoder.matches(password, encodedPassword);
        log.debug("Password validation result: {}", matches);
        return matches;
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!validatePassword(user, oldPassword)) {
            throw new RuntimeException("旧密码不正确");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    @Override
    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        
        // 确保角色列表不为空且包含 ROLE_ 前缀
        List<String> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Collections.singletonList("ROLE_USER");
        } else {
            roles = roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .collect(Collectors.toList());
        }
        claims.put("roles", roles);
        
        return jwtConfig.createToken(user.getUsername(), claims);
    }
}
