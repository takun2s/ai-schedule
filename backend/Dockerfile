FROM openjdk:8-jdk-slim

WORKDIR /app

# 复制Maven构建的jar文件
COPY target/scheduler-1.0.0-SNAPSHOT.jar app.jar

# 复制配置文件
COPY src/main/resources/application-docker.yml /app/config/application.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:/app/config/application.yml"]
