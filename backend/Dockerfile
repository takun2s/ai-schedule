FROM openjdk:8-jdk-alpine

WORKDIR /app

#ENV JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
#ENV PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/jvm/java-1.8-openjdk/jre/bin:/usr/lib/jvm/java-1.8-openjdk/bin:$PATH
#ENV PATH=$JAVA_HOME/bin:$PATH

COPY scheduler-web/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"] 
