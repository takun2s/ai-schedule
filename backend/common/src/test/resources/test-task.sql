-- 插入测试任务
INSERT INTO task (name, type, content, params, working_dir, timeout, alert_type, alert_receiver, status)
VALUES 
('测试Shell任务', 1, 'ls -l', '-R', '/tmp', 300, 'email', 'test@example.com', 1),
('测试JAR任务', 2, '/path/to/test.jar', '--config config.json', '/path/to/jar/dir', 600, 'dingtalk', 'your-dingtalk-token', 1);

-- 插入测试告警配置
INSERT INTO task_alert (task_id, type, target, subject, content, rule, rule_content, status)
VALUES 
(1, 'email', 'test@example.com', '任务执行失败通知', '任务执行失败，请检查', 'FAILURE', NULL, 0),
(1, 'dingtalk', 'your-dingtalk-token', '任务执行超时通知', '任务执行超时，请检查', 'TIMEOUT', NULL, 0),
(2, 'webhook', 'http://your-webhook-url', '任务执行结果通知', '任务执行完成', 'CUSTOM', '{"type":"EXIT_CODE","value":0}', 0); 