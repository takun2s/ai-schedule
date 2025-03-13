package com.scheduler.controller;

import com.scheduler.common.Result;
import org.quartz.CronExpression;
import org.springframework.web.bind.annotation.*;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/utils")
public class UtilController {

    @PostMapping("/cron/next")
    public Result<Date> getNextExecuteTime(@RequestBody Map<String, String> params) {
        try {
            String expression = params.get("expression");
            if (expression == null || expression.trim().isEmpty()) {
                return Result.error(400, "Cron表达式不能为空");
            }
            
            CronExpression cronExpression = new CronExpression(expression);
            Date nextValidTime = cronExpression.getNextValidTimeAfter(new Date());
            
            if (nextValidTime == null) {
                return Result.error(400, "无法计算下次执行时间");
            }
            
            return Result.success(nextValidTime);
        } catch (ParseException e) {
            return Result.error(400, "Cron表达式格式错误: " + e.getMessage());
        }
    }
}
