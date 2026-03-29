# Forum-Java 项目架构文档

## 1. 项目概述

Forum-Java 是一个基于 Spring Boot 的现代化开源社区平台,采用 DDD(领域驱动设计)架构风格,提供论坛讨论和知识问答功能。

- **版本**: 0.0.1-SNAPSHOT
- **Spring Boot**: 2.3.3.RELEASE
- **JDK版本**: 1.8
- **官网**: https://www.forumjava.com
- **演示地址**: http://opensource.developers.pub

## 2. 整体架构

项目采用**分层架构 + DDD设计模式**,通过 Maven 多模块进行组织,遵循单向依赖原则。

### 2.1 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        forum-starter                         │
│                    (应用启动入口层)                           │
└──────────────────────┬──────────────────────────────────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
┌───────▼────────┐            ┌───────▼────────┐
│  forum-portal  │            │  forum-facade  │
│   (Web控制器)  │            │  (API网关层)   │
└───────┬────────┘            └───────┬────────┘
        │                             │
        └──────────────┬──────────────┘
                       │
              ┌────────▼────────┐
              │   forum-app     │
              │  (应用服务层)   │
              │  - Manager      │
              │  - Listener     │
              │  - Transfer     │
              └────────┬────────┘
                       │
              ┌────────▼────────┐
              │  forum-domain   │
              │   (领域层)      │
              │  - Entity       │
              │  - Repository   │
              │  - Service      │
              └────────┬────────┘
                       │
        ┌──────────────┴──────────────┐
        │                             │
┌───────▼──────────┐         ┌────────▼────────┐
│forum-infrastructure│         │  forum-common   │
│   (基础设施层)      │         │   (公共工具)    │
│  - DAL/DAO        │         │  - Utils        │
│  - Cache          │         │  - Constants    │
│  - File           │         │  - Enums        │
│  - Search         │         └─────────────────┘
│  - Mail           │
│  - GitHub OAuth   │
└───────────────────┘
```

### 2.2 模块依赖关系

```
forum-starter
  ├── forum-portal (Web展示层)
  │     └── forum-app
  └── forum-facade (API接口层)
        └── forum-app

forum-app (应用服务层)
  ├── forum-api
  └── forum-domain

forum-infrastructure (基础设施层)
  └── forum-domain

forum-domain (领域层)
  └── forum-common

forum-api (接口定义层)
  (独立,仅定义接口规范)
```

## 3. 模块详解

### 3.1 forum-starter (启动模块)

**职责**: 应用启动入口,整合所有模块

**主要内容**:
- Spring Boot 主启动类
- 配置文件 (application.properties)
- 资源文件 (static, templates)

**关键配置**:
- 数据库配置
- 缓存配置
- 文件存储配置
- 邮件服务配置

---

### 3.2 forum-portal (Web控制器层)

**职责**: 提供面向用户的Web页面展示和交互

**包结构**:
```
pub.developers.forum.portal.controller
  ├── IndexController          # 首页
  ├── ArticleInfoController    # 文章详情
  ├── FaqInfoController        # 问答详情
  ├── FaqListController        # 问答列表
  ├── UserController           # 用户中心
  ├── MessageController        # 消息通知
  ├── InterestController       # 关注相关
  ├── SearchController         # 搜索功能
  ├── TagController            # 标签管理
  └── GithubController         # GitHub OAuth登录
```

**技术栈**:
- Spring MVC
- Thymeleaf (服务端渲染)
- Bootstrap (前端UI框架)

---

### 3.3 forum-facade (API接口层)

**职责**: 对外提供RESTful API接口

**特点**:
- 统一的API规范
- 独立于Portal层,可单独部署
- 可供第三方系统集成

---

### 3.4 forum-api (接口定义模块)

**职责**: 定义服务接口、请求模型、响应模型

**包结构**:
```
pub.developers.forum.api
  ├── model                    # 公共模型
  │   ├── PageRequestModel     # 分页请求
  │   ├── PageResponseModel    # 分页响应
  │   └── ResultModel          # 统一返回结构
  ├── request                  # 请求对象
  │   ├── article/*            # 文章相关请求
  │   ├── faq/*                # 问答相关请求
  │   ├── user/*               # 用户相关请求
  │   ├── comment/*            # 评论相关请求
  │   ├── tag/*                # 标签相关请求
  │   ├── message/*            # 消息相关请求
  │   ├── config/*             # 配置相关请求
  │   ├── file/*               # 文件上传请求
  │   └── github/*             # GitHub OAuth请求
  ├── response                 # 响应对象
  │   ├── article/*
  │   ├── faq/*
  │   ├── user/*
  │   ├── comment/*
  │   ├── tag/*
  │   └── message/*
  └── service                  # 服务接口定义
      ├── ArticleApiService
      ├── FaqApiService
      ├── UserApiService
      ├── CommentApiService
      ├── TagApiService
      ├── MessageApiService
      ├── FileApiService
      └── ApprovalApiService
```

---

### 3.5 forum-app (应用服务层)

**职责**: 业务编排、流程控制、事件处理

**包结构**:
```
pub.developers.forum.app
  ├── manager                  # 业务管理器 (核心业务逻辑编排)
  │   ├── ArticleManager       # 文章管理
  │   ├── FaqManager           # 问答管理
  │   ├── CommentManager       # 评论管理
  │   ├── UserManager          # 用户管理
  │   ├── TagManager           # 标签管理
  │   ├── MessageManager       # 消息管理
  │   ├── FileManager          # 文件管理
  │   ├── ApprovalManager      # 审核管理
  │   ├── AbstractPostsManager # 帖子抽象管理器
  │   └── AbstractLoginManager # 登录抽象管理器
  ├── listener                 # 事件监听器
  │   ├── ArticleCreateListener      # 文章创建事件
  │   ├── ArticleUpdateListener      # 文章更新事件
  │   ├── FaqCreateListener          # 问答创建事件
  │   ├── FaqUpdateListener          # 问答更新事件
  │   ├── FoodArticleCreateListener  # Feed流-文章创建
  │   ├── FoodFaqCreateListener      # Feed流-问答创建
  │   ├── FoodCommentCreateListener  # Feed流-评论创建
  │   ├── FoodUserFollowListener     # Feed流-用户关注
  │   ├── FoodPostsDeleteListener    # Feed流-帖子删除
  │   └── FoodApprovalCreateListener # Feed流-审核创建
  ├── support                  # 支持工具类
  └── transfer                 # 对象转换器 (DTO <-> Entity)
```

**设计模式**:
- **Manager模式**: 负责业务流程编排,调用领域服务
- **Event-Driven**: 通过监听器解耦业务模块
- **Template Method**: AbstractPostsManager、AbstractLoginManager提供模板方法

---

### 3.6 forum-domain (领域层)

**职责**: 核心业务领域模型、领域服务、仓储接口定义

**包结构**:
```
pub.developers.forum.domain
  ├── entity                   # 领域实体
  │   ├── BaseEntity           # 基础实体
  │   ├── BasePosts            # 帖子基类
  │   ├── Article              # 文章实体
  │   ├── ArticleType          # 文章分类
  │   ├── Faq                  # 问答实体
  │   ├── User                 # 用户实体
  │   ├── Comment              # 评论实体
  │   ├── Tag                  # 标签实体
  │   ├── Message              # 消息实体
  │   ├── Follow               # 关注实体
  │   ├── Approval             # 审核实体
  │   └── Config               # 配置实体
  ├── repository               # 仓储接口(DDD Repository模式)
  │   ├── ArticleRepository
  │   ├── FaqRepository
  │   ├── UserRepository
  │   ├── CommentRepository
  │   ├── TagRepository
  │   ├── MessageRepository
  │   └── ...
  └── service                  # 领域服务接口
      └── MessageService       # 消息领域服务
```

**设计原则**:
- 充血模型: 实体包含业务逻辑
- 仓储模式: 封装数据访问细节
- 领域服务: 处理跨实体的业务逻辑

---

### 3.7 forum-infrastructure (基础设施层)

**职责**: 实现领域层定义的接口,提供技术基础设施

**包结构**:
```
pub.developers.forum.infrastructure
  ├── *RepositoryImpl.java     # 仓储接口实现
  │   ├── ArticleRepositoryImpl
  │   ├── FaqRepositoryImpl
  │   ├── UserRepositoryImpl
  │   ├── AbstractPostsRepository
  │   └── ...
  ├── dal                      # 数据访问层
  │   ├── dao                  # MyBatis Mapper接口
  │   │   ├── ArticleDAO
  │   │   ├── FaqDAO
  │   │   ├── UserDAO
  │   │   └── ...
  │   └── dataobject           # 数据库对象(DO)
  │       ├── ArticleDO
  │       ├── FaqDO
  │       └── ...
  ├── cache                    # 缓存服务
  │   ├── CacheService         # 缓存接口
  │   └── CacheServiceImpl     # 缓存实现
  ├── file                     # 文件存储
  │   ├── FileService          # 文件服务接口
  │   └── QiniuFileService     # 七牛云存储实现
  ├── search                   # 搜索服务
  │   ├── SearchService        # 搜索接口
  │   └── SearchServiceImpl    # 搜索实现
  ├── mail                     # 邮件服务
  │   ├── MailService          # 邮件接口
  │   └── MailServiceImpl      # 邮件实现
  ├── github                   # GitHub OAuth
  │   └── GithubService        # GitHub登录服务
  ├── audit                    # 审核服务
  │   ├── AuditService         # 审核接口
  │   └── AuditServiceImpl     # 审核实现
  ├── transfer                 # DO <-> Entity 转换
  └── fix                      # 修复工具类
```

**技术实现**:
- **持久化**: MyBatis + MySQL + HikariCP
- **缓存**: 抽象缓存接口 (可接入Redis等)
- **文件存储**: 七牛云SDK
- **邮件**: JavaMail
- **分页**: PageHelper

---

### 3.8 forum-common (公共模块)

**职责**: 提供跨模块的通用工具和常量

**主要内容**:
- 工具类 (Utils)
- 常量定义 (Constants)
- 枚举类型 (Enums)
- 异常定义 (Exceptions)
- 公共注解 (Annotations)

## 4. 核心技术栈

### 4.1 后端技术

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.3.3.RELEASE | 应用容器 |
| Spring MVC | - | Web框架 |
| MyBatis | 2.1.3 | 持久层框架 |
| MySQL | - | 关系型数据库 |
| HikariCP | - | 数据库连接池 |
| PageHelper | 5.1.8 | 分页插件 |
| FastJSON | 1.2.69 | JSON序列化 |
| Guava | 15.0 | 工具库 |
| Lombok | 1.16.10 | 代码简化 |
| JavaMail | 1.6.2 | 邮件发送 |
| Qiniu SDK | 7.4.0 | 七牛云存储 |
| Netty-SocketIO | 1.7.7 | WebSocket支持 |
| Thymeleaf | - | 模板引擎 |

### 4.2 前端技术

| 技术 | 说明 |
|------|------|
| Vue.js | 管理后台框架 |
| iView | 管理后台UI |
| Bootstrap | 用户端UI |
| Mavon-Editor | Markdown编辑器 |

### 4.3 测试框架

| 技术 | 版本 |
|------|------|
| JUnit | 4.13.2 |
| Mockito | 3.9.0 |
| Spring Test | - |
| JaCoCo | 0.8.7 (代码覆盖率) |

## 5. 核心业务流程

### 5.1 文章发布流程

```
1. 用户提交文章 (Portal/ArticleController)
   ↓
2. 请求转发到 ArticleManager (forum-app)
   ↓
3. 调用 ArticleRepository 保存实体 (forum-domain)
   ↓
4. ArticleRepositoryImpl 执行数据库操作 (forum-infrastructure)
   ↓
5. 发布 ArticleCreateEvent 事件
   ↓
6. ArticleCreateListener 监听处理
   - 触发审核流程
   - 更新Feed流
   - 发送通知
```

### 5.2 用户登录流程

```
1. 用户提交登录请求
   ↓
2. UserManager 调用 AbstractLoginManager
   ↓
3. 验证用户名密码 (UserRepository)
   ↓
4. 生成Token/Session
   ↓
5. 记录操作日志 (OptLogRepository)
   ↓
6. 返回用户信息
```

### 5.3 审核流程

```
1. 内容提交 (文章/问答)
   ↓
2. 创建审核记录 (Approval Entity)
   ↓
3. 调用审核服务 (AuditService)
   ↓
4. 管理员审核操作
   ↓
5. 更新审核状态
   ↓
6. 发布审核结果事件
   ↓
7. 通知内容作者
```

## 6. 数据模型设计

### 6.1 核心实体关系

```
User (用户)
  ├── 1:N → Article (文章)
  ├── 1:N → Faq (问答)
  ├── 1:N → Comment (评论)
  ├── M:N → Follow (关注关系)
  └── 1:N → Message (消息)

Article (文章)
  ├── N:1 → ArticleType (分类)
  ├── M:N → Tag (标签)
  ├── 1:N → Comment (评论)
  └── 1:1 → Approval (审核)

Faq (问答)
  ├── M:N → Tag (标签)
  ├── 1:N → Comment (评论)
  └── 1:1 → Approval (审核)

Comment (评论)
  ├── N:1 → User (作者)
  └── N:1 → Posts (所属帖子)
```

### 6.2 实体继承关系

```
BaseEntity (基础实体)
  ├── id (Long)
  ├── createAt (Date)
  └── updateAt (Date)

BasePosts extends BaseEntity (帖子基类)
  ├── title (String)
  ├── content (String)
  ├── authorId (Long)
  ├── viewCount (Integer)
  ├── approvalState (ApprovalState)
  ├── Article
  └── Faq
```

## 7. 扩展机制

项目提供多个可扩展的抽象接口,便于企业定制:

### 7.1 文件存储扩展

```java
pub.developers.forum.infrastructure.file.FileService
```
- 默认实现: 七牛云存储
- 可扩展: OSS、本地存储、私有云存储

### 7.2 缓存服务扩展

```java
pub.developers.forum.infrastructure.cache.CacheService
```
- 默认实现: 内存缓存
- 可扩展: Redis、Memcached

### 7.3 搜索服务扩展

```java
pub.developers.forum.infrastructure.search.SearchService
```
- 默认实现: 数据库模糊查询
- 可扩展: ElasticSearch、Solr

### 7.4 审核服务扩展

```java
pub.developers.forum.infrastructure.audit.AuditService
```
- 默认实现: 内部审核流程
- 可扩展: 对接企业审核中心

## 8. 架构特点

### 8.1 优势

1. **清晰的分层架构**
   - 各层职责明确
   - 单向依赖,低耦合
   - 易于维护和扩展

2. **DDD设计**
   - 充血领域模型
   - 仓储模式隔离数据访问
   - 领域服务封装业务规则

3. **事件驱动**
   - 通过监听器解耦业务模块
   - 便于扩展新功能

4. **可扩展性**
   - 抽象服务接口,支持多种实现
   - 可接入企业内部系统

5. **分布式支持**
   - 分布式Session
   - 支持集群部署
   - 调用链日志

### 8.2 设计模式应用

- **Repository模式**: 数据访问抽象
- **Manager模式**: 业务编排
- **Template Method**: 抽象基类提供模板方法
- **Factory模式**: 对象创建
- **Strategy模式**: 登录策略(邮箱登录、GitHub登录)
- **Observer模式**: 事件监听机制

## 9. 部署架构

```
┌─────────────────────────────────────────────┐
│              负载均衡 (Nginx)                │
└──────────┬──────────────────┬───────────────┘
           │                  │
    ┌──────▼──────┐    ┌──────▼──────┐
    │  App Node 1 │    │  App Node 2 │
    │(forum-java) │    │(forum-java) │
    └──────┬──────┘    └──────┬──────┘
           │                  │
           └────────┬─────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
   ┌────▼─────┐         ┌──────▼──────┐
   │  MySQL   │         │   缓存/存储  │
   │ (主从)   │         │  - Redis    │
   └──────────┘         │  - Qiniu    │
                        └─────────────┘
```

## 10. 开发规范

### 10.1 命名规范

- **Controller**: `XxxController`
- **Manager**: `XxxManager`
- **Service**: `XxxService` / `XxxServiceImpl`
- **Repository**: `XxxRepository` / `XxxRepositoryImpl`
- **Entity**: 名词,如 `User`、`Article`
- **DO**: `XxxDO`
- **DAO**: `XxxDAO`
- **Request**: `XxxRequest`
- **Response**: `XxxResponse`

### 10.2 包命名规范

```
pub.developers.forum
  ├── api         # 接口定义
  ├── app         # 应用服务
  ├── domain      # 领域层
  ├── infrastructure  # 基础设施
  ├── portal      # Web控制器
  ├── facade      # API网关
  └── common      # 公共模块
```

### 10.3 代码分层规范

| 层次 | 职责 | 禁止事项 |
|------|------|----------|
| Controller | 接收请求、参数校验、返回响应 | 不允许直接调用Repository |
| Manager | 业务编排、流程控制 | 不允许直接操作数据库 |
| Domain | 领域逻辑、实体行为 | 不允许依赖具体技术实现 |
| Repository | 数据访问抽象 | 只定义接口,不实现 |
| Infrastructure | 技术实现 | 不允许包含业务逻辑 |

## 11. 后续优化建议

1. **微服务改造**
   - 可将文章、问答、用户等拆分为独立微服务
   - 引入服务注册中心(Eureka/Nacos)
   - API网关统一入口

2. **缓存优化**
   - 接入Redis实现分布式缓存
   - 热点数据缓存策略

3. **搜索优化**
   - 接入ElasticSearch提升搜索体验
   - 全文检索支持

4. **性能优化**
   - 读写分离
   - 数据库分库分表
   - CDN加速静态资源

5. **安全加固**
   - API限流
   - 接口签名验证
   - XSS/CSRF防护

6. **监控运维**
   - 接入APM监控
   - 日志集中管理(ELK)
   - 健康检查和告警

---

**文档版本**: v1.0  
**更新日期**: 2025-12-23  
**维护者**: forum-java团队
