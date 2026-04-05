# 项目技术栈

## 基础框架
- **Spring Boot**: 2.3.3.RELEASE
- **Java**: 1.8

## 核心依赖

### 持久层
- **MyBatis**: 2.1.3 (mybatis-spring-boot-starter)
- **MySQL**: mysql-connector-java
- **分页插件**: PageHelper 5.1.8

### Web框架
- **Spring Boot Web**: spring-boot-starter-web
- **Servlet API**: 3.1.0

### 工具库
- **Lombok**: 1.16.10
- **FastJSON**: 1.2.69
- **Guava**: 15.0
- **Apache Commons Codec**: 1.8
- **Apache HttpClient**: httpclient

### 实时通信
- **Netty-SocketIO**: 1.7.7

### 邮件服务
- **JavaMail**: 1.6.2

### 云服务
- **七牛云SDK**: 7.4.0 (qiniu-java-sdk)

### 监控与管理
- **Spring Boot Actuator**: spring-boot-starter-actuator
- **Spring AOP**: spring-boot-starter-aop

## 测试框架
- **JUnit**: 4.13.2
- **Mockito**: 3.9.0 (mockito-core, mockito-inline)
- **Spring Test**: spring-test
- **JaCoCo**: 0.8.7 (代码覆盖率)

## 构建工具
- **Maven**: Apache Maven
- **Spring Boot Maven Plugin**: spring-boot-maven-plugin

## 项目模块结构
- **forum-starter**: 启动模块
- **forum-app**: 应用层
- **forum-domain**: 领域层
- **forum-infrastructure**: 基础设施层
- **forum-common**: 公共模块
- **forum-api**: API接口层
- **forum-portal**: 门户层
- **forum-facade**: 外观层
