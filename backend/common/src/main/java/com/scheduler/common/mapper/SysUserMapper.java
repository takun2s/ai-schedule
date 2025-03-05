package com.scheduler.common.mapper;

import com.scheduler.common.model.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface SysUserMapper {
    
    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(String username);
    
    /**
     * 根据ID查询用户
     */
    SysUser selectById(Long id);
    
    /**
     * 查询用户列表
     */
    List<SysUser> selectList(SysUser user);
    
    /**
     * 新增用户
     */
    int insert(SysUser user);
    
    /**
     * 修改用户
     */
    int update(SysUser user);
    
    /**
     * 删除用户
     */
    int deleteById(Long id);
    
    /**
     * 修改密码
     */
    int updatePassword(@Param("id") Long id, @Param("password") String password);
} 