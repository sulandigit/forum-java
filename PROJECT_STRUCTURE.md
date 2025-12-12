# Forum-Java 项目结构说明文档

## 项目概述

forum-java 是一个基于 Spring Boot 开发的现代化开源社区平台，采用多模块 Maven 工程结构，实现了论坛讨论和知识问答功能。

- **官网地址**: https://www.forumjava.com
- **开源版演示**: http://opensource.developers.pub
- **商业版演示**: https://www.developers.pub
- **技术文档**: https://www.developers.pub/wiki/1166300

## 技术栈

### 后端技术
- **框架**: Spring Boot 2.3.3.RELEASE
- **构建工具**: Maven
- **Java版本**: 1.8
- **持久层**: MyBatis + PageHelper (分页插件)
- **数据库**: MySQL
- **连接池**: HikariCP
- **模板引擎**: Thymeleaf
- **JSON处理**: Fastjson 1.2.69
- **邮件**: javax.mail
- **文件存储**: 七牛云 SDK
- **测试框架**: JUnit 4.13.2, Mockito 3.9.0
- **代码覆盖率**: JaCoCo 0.8.7

### 前端技术
- **管理后台**: Vue.js + iView UI
- **用户端**: Bootstrap
- **编辑器**: mavon-editor (Markdown)

## 模块架构

项目采用 DDD (领域驱动设计) 分层架构，共包含 8 个子模块：

```
forum-java (父模块)
├── forum-api           # API 接口定义层
├── forum-app           # 应用服务层
├── forum-common        # 公共组件层
├── forum-domain        # 领域模型层
├── forum-facade        # 门面层
├── forum-infrastructure # 基础设施层
├── forum-portal        # 展示层
└── forum-starter       # 启动模块
```

### 1. forum-api (API 接口定义层)

**职责**: 定义对外暴露的接口契约

**依赖关系**:
- Spring Web (用于接口注解)
- Lombok (简化代码)

**特点**:
- 仅包含接口定义，不包含实现
- 定义 RESTful API 规范
- 供其他服务调用或集成

---

### 2. forum-app (应用服务层)

**职责**: 编排业务流程，协调领域服务完成复杂业务逻辑

**目录结构**:
```
pub.developers.forum.app
├── listener    # 事件监听器
├── manager     # 业务管理器 (编排领域服务)
├── support     # 辅助工具类
└── transfer    # 数据传输对象转换
```

**依赖关系**:
- forum-api (接口定义)
- forum-domain (领域模型)

**特点**:
- 实现业务流程编排
- 处理事务边界
- 监听领域事件
- 协调多个领域服务

---

### 3. forum-common (公共组件层)

**职责**: 提供全局通用工具类、常量、枚举等

**特点**:
- 无外部依赖
- 被其他所有模块依赖
- 包含工具类、常量定义、通用枚举

---

### 4. forum-domain (领域模型层)

**职责**: 核心业务逻辑和领域模型

**目录结构**:
```
pub.developers.forum.domain
├── entity          # 领域实体
│   └── value      # 值对象
├── repository     # 仓储接口 (数据访问抽象)
└── service        # 领域服务
```

**依赖关系**:
- forum-common (公共组件)

**特点**:
- 包含核心业务逻辑
- 定义领域实体和值对象
- 定义仓储接口 (具体实现在 infrastructure 层)
- 纯粹的领域模型，不依赖基础设施

---

### 5. forum-facade (门面层)

**职责**: 为外部系统提供统一的调用入口

**特点**:
- 封装内部复杂性
- 提供简化的接口
- 适配不同调用方需求

---

### 6. forum-infrastructure (基础设施层)

**职责**: 提供技术基础设施支持，实现领域层定义的接口

**目录结构**:
```
pub.developers.forum.infrastructure
├── audit       # 审核服务实现
├── cache       # 缓存服务实现
├── dal         # 数据访问层
│   ├── dao         # MyBatis DAO 接口
│   └── dataobject  # 数据库对象 (DO)
├── file        # 文件存储服务实现 (七牛云)
├── fix         # 数据修复工具
├── github      # GitHub 集成
├── mail        # 邮件服务实现
├── search      # 搜索服务实现
└── transfer    # 数据对象转换
```

**特点**:
- 实现 domain 层定义的 repository 接口
- 提供文件存储、缓存、搜索等技术服务
- 封装第三方服务 (七牛云、邮件等)
- 包含 MyBatis 数据访问实现

---

### 7. forum-portal (展示层)

**职责**: 处理 HTTP 请求，渲染页面和返回数据

**目录结构**:
```
pub.developers.forum.portal
├── controller
│   ├── admin    # 管理后台控制器
│   └── rest     # RESTful API 控制器
├── request      # 请求对象定义
└── support      # 控制器辅助类
```

**前端资源**:
```
src/main/forum-vue/    # Vue.js 管理后台
src/main/resources/    # Thymeleaf 模板和静态资源
```

**依赖关系**:
- forum-api (接口定义)
- forum-common (公共组件)
- Spring Boot Starter Thymeleaf (服务端渲染)
- jsoup 1.11.3 (HTML 解析)

**特点**:
- 提供 RESTful API 接口
- Thymeleaf 渲染服务端页面
- Vue.js 实现管理后台前端
- 请求参数验证和转换

---

### 8. forum-starter (启动模块)

**职责**: 应用启动入口，整合所有模块

**依赖关系**:
- forum-facade (门面层)
- forum-portal (展示层)
- forum-infrastructure (基础设施层)

**特点**:
- 包含 Spring Boot 主启动类
- 聚合所有依赖模块
- 可打包为可执行 JAR 或 WAR
- 最终打包名称: `forum-java`

**配置文件位置**:
```
src/main/resources/
├── application.yml             # 主配置文件
├── application-dev.yml         # 开发环境配置
├── application-prod.yml        # 生产环境配置
└── mybatis/mapper/             # MyBatis XML 映射文件
```

---

## 模块依赖关系图

```
forum-starter
    ├─→ forum-facade
    ├─→ forum-portal
    │       ├─→ forum-api
    │       └─→ forum-common
    └─→ forum-infrastructure
            └─→ forum-dal

forum-facade
    └─→ forum-app
            ├─→ forum-api
            └─→ forum-domain
                    └─→ forum-common
```

**依赖原则**:
- 依赖方向: 上层依赖下层，下层不依赖上层
- domain 层只依赖 common，保持领域纯粹性
- infrastructure 层实现 domain 层定义的接口
- starter 模块聚合所有依赖，作为最终可运行产物

---

## 功能特性

### 用户端功能
- **文章**: 发布、编辑、删除、评论、点赞、分类筛选
- **问答**: 提问、回答、关注、设置最佳答案、状态筛选
- **用户**: 个人资料、关注好友、粉丝管理
- **消息**: 关注通知、评论通知、已读管理
- **搜索**: 全文搜索文章和问答
- **标签**: 标签管理和内容筛选

### 管理后台功能
- **用户管理**: 禁用/启用、管理员权限设置
- **内容审核**: 文章/问答审核、可见性控制
- **运营功能**: 官方标记、置顶、加精
- **分类管理**: 文章分类、标签管理
- **操作日志**: 操作记录查询

### 技术特性
- **前端**:
  - 响应式设计 (PC + 移动端)
  - 自定义主题颜色
  - 编辑器快捷键 (Ctrl+S 保存, Ctrl+V 粘贴图片)
  
- **后端**:
  - 调用链日志追踪
  - 分布式 Session (支持集群部署)
  - 角色权限分级
  - 接口权限校验
  
- **可扩展接口**:
  - 审核服务接口 (可接入第三方审核中心)
  - 文件存储接口 (可替换存储服务)
  - 缓存服务接口 (可替换缓存实现)
  - 搜索服务接口 (可接入 ES 等搜索引擎)

---

## 构建与运行

### 构建命令
```bash
# 编译打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 生成测试覆盖率报告
mvn test
# 报告位置: target/site/jacoco/index.html
```

### 运行方式
```bash
# 开发模式运行
mvn spring-boot:run

# 运行打包后的 JAR
java -jar forum-starter/target/forum-java.jar

# 指定配置文件
java -jar forum-java.jar --spring.profiles.active=prod
```

---

## 目录结构总览

```
forum-java/
├── forum-api/                  # API 接口定义
│   └── src/main/java/pub/developers/forum/api/
├── forum-app/                  # 应用服务层
│   └── src/main/java/pub/developers/forum/app/
│       ├── listener/           # 事件监听器
│       ├── manager/            # 业务管理器
│       ├── support/            # 辅助工具
│       └── transfer/           # 对象转换
├── forum-common/               # 公共组件
│   └── src/main/java/pub/developers/forum/common/
├── forum-domain/               # 领域模型层
│   └── src/main/java/pub/developers/forum/domain/
│       ├── entity/             # 领域实体
│       │   └── value/         # 值对象
│       ├── repository/         # 仓储接口
│       └── service/            # 领域服务
├── forum-facade/               # 门面层
│   └── src/main/java/pub/developers/forum/facade/
├── forum-infrastructure/       # 基础设施层
│   └── src/main/java/pub/developers/forum/infrastructure/
│       ├── audit/              # 审核服务
│       ├── cache/              # 缓存服务
│       ├── dal/                # 数据访问层
│       │   ├── dao/           # MyBatis DAO
│       │   └── dataobject/    # 数据对象
│       ├── file/               # 文件存储
│       ├── mail/               # 邮件服务
│       └── search/             # 搜索服务
├── forum-portal/               # 展示层
│   └── src/main/
│       ├── java/pub/developers/forum/portal/
│       │   ├── controller/
│       │   │   ├── admin/     # 管理后台
│       │   │   └── rest/      # REST API
│       │   ├── request/        # 请求对象
│       │   └── support/        # 辅助类
│       ├── forum-vue/          # Vue 管理后台
│       │   ├── src/
│       │   ├── build/
│       │   ├── config/
│       │   └── package.json
│       └── resources/          # Thymeleaf 模板
├── forum-starter/              # 启动模块
│   └── src/main/
│       ├── java/pub/developers/forum/starter/
│       └── resources/
│           ├── application.yml
│           └── mybatis/mapper/
├── test-case-result/           # 测试结果
├── pom.xml                     # 父 POM
├── mvnw                        # Maven Wrapper
├── LICENSE                     # 开源协议
└── README.md                   # 项目说明
```

---

## 开发规范

### 包命名规范
所有 Java 包遵循统一前缀: `pub.developers.forum.{module}`

### 分层职责
- **portal**: 接收请求，参数校验，调用 app 层
- **app**: 业务编排，事务管理，调用 domain 层
- **domain**: 核心业务逻辑，定义领域模型和仓储接口
- **infrastructure**: 实现技术细节，提供基础设施支持
- **api**: 定义对外接口契约
- **facade**: 封装复杂调用，提供简化接口

### 数据对象转换
- **DO (DataObject)**: 数据库对象，位于 infrastructure/dal/dataobject
- **Entity**: 领域实体，位于 domain/entity
- **Request**: 请求对象，位于 portal/request
- **VO/DTO**: 视图/传输对象，根据需要定义
- 各层之间通过 transfer 类进行对象转换

---

## 许可协议

**社区版只允许个人使用。商业用途请联系作者购买商业授权。**

详见 LICENSE 文件。

---

## 相关链接

- 官方讨论区: https://www.developers.pub/
- 安装指南: https://www.developers.pub/wiki/1166300/1005736
- 开源版文档: https://www.developers.pub/wiki/1166300
