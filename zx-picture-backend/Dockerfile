# 阶段1 编译和打包项目
FROM maven:3.9-amazoncorretto-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段2：构建最终运行镜像
FROM openjdk:17-jdk-slim
# 暴露应用端口(提供文档和网络配置信息)
# 向外界声明这个容器内部有一个服务正在监听 EXPOSE 后指定的端口。
EXPOSE 8123
WORKDIR /app
COPY --from=build /app/target/zx-picture-backend-0.0.1-SNAPSHOT.jar zx-picture-backend-0.0.1-SNAPSHOT.jar
ENV LANG C.UTF-8
ENTRYPOINT ["java", "-jar", "zx-picture-backend-0.0.1-SNAPSHOT.jar"]