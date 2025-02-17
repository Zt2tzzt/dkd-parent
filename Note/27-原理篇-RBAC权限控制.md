# 原理篇之RBAC权限控制

## 一、Spring Security

Spring Security 是一个功能强大的 Java 安全框架，它提供了全面的安全**认证**和**授权**支持。

与 RBAC 模型结合使用时，Spring Security 能够实现灵活的权限控制。

- **认证（Authentication）**：在 Spring Security 的世界里，认证就像用户登录时，提交的用户名和密码，系统通过这些信息，来验证“你是谁”。
  - Spring Security不仅支持传统的用户名和密码认证，还支持OAuth2、JWT等现代认证方式。
- **授权（Authorization）**：在 Spring Security 中，授权是确认用户在通过认证之后，是否有权限执行某些操作或访问特定资源。

### 1.1.Spring Security 配置

Spring Security 的配置类，是实现安全控制的核心部分。

在配置累着，开启 Spring Security 各种功能，以确保 Web 应用程序的安全性，包括认证、授权、会话管理、过滤器添加等。

dkd-framework/src/main/java/com/dkd/framework/config/SecurityConfig.java

```java
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 开启方法级别的权限控制 => @PreAutherize
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  ……
}
```