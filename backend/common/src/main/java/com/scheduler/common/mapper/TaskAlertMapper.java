package com.scheduler.common.mapper;

import com.scheduler.common.model.TaskAlert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务告警记录Mapper接口
 */
@Mapper
public interface TaskAlertMapper {
    
    /**
     * 根据ID查询告警记录
     */
    TaskAlert selectById(Long id);
    
    /**
     * 查询告警记录列表
     */
    List<TaskAlert> selectList(TaskAlert alert);
    
    /**
     * 新增告警记录
     */
    int insert(TaskAlert alert);
    
    /**
     * 修改告警记录
     */
    int update(TaskAlert alert);
    
    /**
     * 删除告警记录
     */
    int deleteById(Long id);
    
    /**
     * 修改告警状态
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 查询未发送的告警记录
     */
    List<TaskAlert> selectUnsentList();
    
    /**
     * 查询任务告警记录数量
     */
    int selectCountByTaskId(Long taskId);

    /**
     * 根据任务ID查询告警记录列表
     */
    List<TaskAlert> selectByTaskId(Long taskId);
} 