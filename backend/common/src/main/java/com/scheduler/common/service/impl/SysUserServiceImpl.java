package com.scheduler.common.service.impl;

import com.scheduler.common.exception.BaseException;
import com.scheduler.common.mapper.SysUserMapper;
import com.scheduler.common.model.SysUser;
import com.scheduler.common.service.SysUserService;
import com.scheduler.common.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统用户服务实现类
 */
@Slf4j
@Service
public class SysUserServiceImpl implements SysUserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = getByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return user;
    }

    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public SysUser getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public List<SysUser> getList(SysUser user) {
        return userMapper.selectList(user);
    }

    @Override
    public void add(SysUser user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            throw new BaseException("用户名已存在");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userMapper.insert(user);
    }

    @Override
    public void update(SysUser user) {
        // 检查用户是否存在
        if (getById(user.getId()) == null) {
            throw new BaseException("用户不存在");
        }
        userMapper.update(user);
    }

    @Override
    public void delete(Long id) {
        // 检查用户是否存在
        if (getById(id) == null) {
            throw new BaseException("用户不存在");
        }
        userMapper.deleteById(id);
    }

    @Override
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        // 检查用户是否存在
        SysUser user = getById(id);
        if (user == null) {
            throw new BaseException("用户不存在");
        }
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BaseException("旧密码错误");
        }
        // 更新密码
        userMapper.updatePassword(id, passwordEncoder.encode(newPassword));
    }
} 