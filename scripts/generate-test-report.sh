#!/bin/bash

echo "开始生成测试报告..."

# 创建报告目录
mkdir -p test/reports/$(date +%Y%m%d)

# 收集测试结果
echo "收集测试结果..."
cp test/logs/*.log test/reports/$(date +%Y%m%d)/
cp test/results/*.xml test/reports/$(date +%Y%m%d)/

# 生成HTML报告
echo "生成HTML报告..."
mvn surefire-report:report-only
cp target/site/* test/reports/$(date +%Y%m%d)/

# 生成覆盖率报告
echo "生成覆盖率报告..."
mvn jacoco:report
cp target/site/jacoco/* test/reports/$(date +%Y%m%d)/coverage/

# 生成性能报告
echo "生成性能报告..."
cp test/jmeter/results/* test/reports/$(date +%Y%m%d)/performance/
cp test/gatling/results/* test/reports/$(date +%Y%m%d)/performance/

# 生成安全报告
echo "生成安全报告..."
cp test/security/*.json test/reports/$(date +%Y%m%d)/security/

# 生成汇总报告
echo "生成汇总报告..."
cat > test/reports/$(date +%Y%m%d)/summary.md << EOF
# 测试报告汇总

## 测试时间
$(date)

## 测试结果
- 功能测试：$(grep -c "FAILURE" test/reports/$(date +%Y%m%d)/TEST-*.xml) 个失败
- 性能测试：平均响应时间 $(cat test/reports/$(date +%Y%m%d)/performance/response_time.txt) ms
- 安全测试：发现 $(cat test/reports/$(date +%Y%m%d)/security/vulnerabilities.txt) 个漏洞
- 覆盖率：$(cat test/reports/$(date +%Y%m%d)/coverage/coverage.txt)%

## 问题统计
$(cat test/reports/$(date +%Y%m%d)/issues.txt)

## 改进建议
$(cat test/reports/$(date +%Y%m%d)/recommendations.txt)
EOF

echo "测试报告生成完成" 