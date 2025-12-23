# Forum Java - Developer Guide

## Project Overview

This is a community forum platform built with Spring Boot, featuring article/FAQ creation, commenting, liking, following, messaging, and content moderation capabilities.

### Technology Stack

**Backend**: Spring Boot 2.3.3, Spring MVC, MyBatis 2.1.3  
**Database**: MySQL with HikariCP connection pool  
**Frontend**: Vue.js (admin), Bootstrap (user interface), Thymeleaf  
**Key Libraries**: Fastjson 1.2.69, Lombok 1.16.10, PageHelper 5.1.8  
**Integrations**: Qiniu (file storage), JavaMail, GitHub OAuth

## Architecture

The project uses a modular Maven structure:

- `forum-domain`: Business entities and repositories
- `forum-app`: Application logic and managers
- `forum-api`: DTOs and service interfaces
- `forum-facade`: API implementations and validation
- `forum-infrastructure`: Data access (DAOs, MyBatis mappers) and external services
- `forum-portal`: Web controllers and frontend integration
- `forum-starter`: Application entry point

## Core Features

**User Functions**:
- Article/FAQ creation with rich text editor and image paste support
- Commenting, liking, following users
- Private messaging
- User profiles and search

**Admin Functions**:
- Content moderation (audit, sticky posts, official tags)
- User management (enable/disable accounts, role assignment)
- Operation logging

**Workflows**:
- Full CRUD with approval process
- Real-time notifications
- Content search functionality

## Extensibility

The platform provides abstraction layers for enterprise integration:

- **File Storage**: Interface-based design (default: Qiniu)
- **Search Service**: Pluggable backend (default: database)
- **Cache Service**: Custom cache integration support
- **Audit Process**: Built-in content approval workflow

## Getting Started

### Prerequisites

- Java 8+
- Maven 3.6+
- MySQL 5.7+

### Build & Run

```bash
mvn clean install
cd forum-starter
mvn spring-boot:run
```

### Database Setup

1. Create MySQL database
2. Configure connection in `application.properties`
3. Run schema migrations from `forum-infrastructure/resources`

## Reference Documentation

### Framework Documentation
* [Spring Boot 2.3.3 Reference](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/)
* [Spring Web MVC](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-developing-web-applications)
* [MyBatis Spring Boot Starter](https://mybatis.org/spring-boot-starter/mybatis-spring-boot-autoconfigure/)
* [Thymeleaf Templates](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#boot-features-spring-mvc-template-engines)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/htmlsingle/#production-ready)

### Maven & Build
* [Apache Maven Documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin](https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/maven-plugin/reference/html/)

### Tutorials
* [Building REST Services with Spring](https://spring.io/guides/tutorials/bookmarks/)
* [MyBatis Quick Start](https://github.com/mybatis/spring-boot-starter/wiki/Quick-Start)
* [Accessing Data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Handling Form Submission](https://spring.io/guides/gs/handling-form-submission/)
