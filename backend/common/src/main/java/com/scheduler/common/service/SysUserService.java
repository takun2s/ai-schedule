package com.scheduler.common.service;

import com.scheduler.common.model.SysUser;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends UserDetailsService {
    
    /**
     * 根据用户名查询用户
     */
    SysUser getByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    SysUser getById(Long id);
    
    /**
     * 查询用户列表
     */
    List<SysUser> getList(SysUser user);
    
    /**
     * 新增用户
     */
    void add(SysUser user);
    
    /**
     * 修改用户
     */
    void update(SysUser user);
    
    /**
     * 删除用户
     */
    void delete(Long id);
    
    /**
     * 修改密码
     */
    void updatePassword(Long id, String oldPassword, String newPassword);
} 