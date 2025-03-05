#!/bin/bash

echo "开始配置测试工具..."

# 配置JMeter
echo "配置JMeter..."
mkdir -p test/jmeter
cp test/config/jmeter/* test/jmeter/

# 配置Gatling
echo "配置Gatling..."
mkdir -p test/gatling
cp test/config/gatling/* test/gatling/

# 配置Prometheus
echo "配置Prometheus..."
docker-compose -f docker-compose.test.yml exec prometheus promtool check config /etc/prometheus/prometheus.yml

# 配置Grafana
echo "配置Grafana..."
curl -X POST http://admin:admin@localhost:3000/api/datasources \
  -H "Content-Type: application/json" \
  -d '{"name":"Prometheus","type":"prometheus","url":"http://prometheus:9090","access":"proxy"}'

# 配置ELK
echo "配置ELK..."
curl -X PUT "http://localhost:9200/_template/logstash" \
  -H "Content-Type: application/json" \
  -d @test/config/elasticsearch/template.json

echo "测试工具配置完成" 