package com.scheduler.common.mapper;

import com.scheduler.common.model.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务Mapper接口
 */
@Mapper
public interface TaskMapper {
    
    /**
     * 根据ID查询任务
     */
    Task selectById(Long id);
    
    /**
     * 查询任务列表
     */
    List<Task> selectList(Task task);
    
    /**
     * 新增任务
     */
    int insert(Task task);
    
    /**
     * 修改任务
     */
    int update(Task task);
    
    /**
     * 删除任务
     */
    int deleteById(Long id);
    
    /**
     * 修改任务状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 查询启用的任务列表
     */
    List<Task> selectEnabledList();
} 