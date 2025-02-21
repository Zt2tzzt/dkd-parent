# 原理篇之RBAC权限控制

## 一、Spring Security

Spring Security 是一个功能强大的 Java 安全框架，它提供了全面的安全**认证**和**授权**支持。

与 RBAC 模型结合使用时，Spring Security 能够实现灵活的权限控制。

- **认证（Authentication）**：在 Spring Security 的世界里，认证就像用户登录时，提交的用户名和密码，系统通过这些信息，来验证“你是谁”。
  - Spring Security不仅支持传统的用户名和密码认证，还支持OAuth2、JWT等现代认证方式。
- **授权（Authorization）**：在 Spring Security 中，授权是确认用户在通过认证之后，是否有权限执行某些操作或访问特定资源。

### 1.1.Spring Security 配置

Spring Security 的配置类，是实现安全控制的核心部分。

在配置类中，可以开启 Spring Security 的各种功能，包括认证、授权、会话管理、过滤器添加等等，以确保 Web 应用程序的安全性。

dkd-framework/src/main/java/com/dkd/framework/config/SecurityConfig.java

```java
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 开启方法级别的权限控制 => @PreAutherize
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  ……
}
```

## 二、用户登录流程

管理员在登录页面，输入用户名、密码、验证码后，点击登录按钮，向后端发送请求，后端通过 SpringSecurity 认证管理器进行登录校验。

此功能涉及：

- 前端相关的代码，位于 `views/login.vue` 这个视图组件中，负责实现用户登录界面和交互逻辑。
- 后端处理逻辑，则在 `dkd-admin` 模块的 `SysLoginController` 中，负责接收前端的请求，处理登录逻辑，并返回 token 令牌

### 2.1.前端部分

1. 点击 `login.vue` 中的登录按钮

2. 调用 `login.vue` 中的 `handleLogin` 方法

3. 调用 `store/mondles/user.js` 中的 `login` 方法，将返回结果存入 `useUserStore` 对象中（用于管理用户相关的状态和操作）。

4. 调用 `api/login.js`中的 `login` 方法

5. 调用 `utils/request.js` 中的 `service` 实例基于 axios 发送 ajax 请求（.env.development 文件设置了统一请求路径前缀）

### 2.2.后段部份

#### 2.2.1.SysLoginController

在 `ruoyi-admin` 模块中 `com.ruoyi.web.controller.system.SysLoginController` 类的 `login` 方法，接收前端登录请求。

#### 2.2.2.SysLoginService

在 `ruoyi-framework` 模块中 `com.ruoyi.framework.web.service.SysLoginService` 类的 `login` 方法处理登录逻辑：

1. 验证码校验；
2. 登录前置校验；
3. SS 认证管理器用户校验，调用执行 `UserDetailsServiceImpl.loadUserByUsername` 方法
   1. 认证通过后，创建登录用户对象 LoginUser，包括用户ID、部门ID、用户信息和用户权限信息；
4. 登录成功，记录日志；
5. 修改用户表更新登录信息；
6. 生成 token。

具体的执行的流程如下图：

```mermaid
graph TD
A(登录请求) -->B{验证码校验}
    B -->|yes| C{登录前置校验}
    C -->|yes| D{SS认证管理器用户校验}
    B -->|no| E[登录失败，记录日志]
    C -->|no| E
    D -->|no| E
    D -->|yes| F[登录成功，记录日志]
    F --> G[更新登录用户信息]
    G -->H[生成 token]
    H --> I[登录用户信息 Redis 缓存]
    H --> J(返回 token)
```

> 在配置文件 application.yml 中，定义了 token 的相关信息，注意其中的 secret 要保管好。
>
> dkd-admin/src/main/resources/application.yml
>
> ```yaml
> # token配置
> token:
>   # 令牌自定义标识
>   header: Authorization
>   # 令牌密钥
>   secret: xxxxxx
>   # 令牌有效期（默认30分钟）
>   expireTime: 30
> ```

## 三、用户的角色和权限

### 3.1.前端部分

1. 在全局 `permission.js` 中的 `router.beforeEach` 方法，用于在用户导航到不同路由之前，进行一些预处理。
2. 调用 `store/mondles/user.js` 中的 `getInfo` 方法，将返回结果存入 `useUserStore` 对象中（用于管理用户相关的状态和操作）。
3. 调用 `api/login.js` 中的 `getInfo` 方法

### 3.2.后端部分

#### 3.2.1.SysLoginController

在 `ruoyi-admin` 模块中 `com.ruoyi.web.controller.system.SysLoginController` 类的 `getInfo` 方法，接收前端获取用户信息请求。

#### 3.2.2.SysPermissionService

在 `ruoyi-framework` 模块中 `com.ruoyi.framework.web.service.SysPermissionService` 类

1. `getRolePermission` 方法，查询该用户角色集合；
2. `getMenuPermission` 方法，查询该用户权限（菜单）集合。

具体的执行的流程如下图：

```mermaid
graph TD
A(前端请求) -->B[获取登录用户信息]
    B --> C[查询角色列表]
    C --> D{判断是否为admin}
    D -->|yes| E[返回admin]
    D -->|no| F[根据用户id查询角色列表]
    E --> G[查询权限列表]
    F --> G
    G --> H{判断是否为admin}
    H -->|yes| I[返回*:*:*]
    H -->|no| J[根据角色id查询权限列表]
    I --> K(返回数据)
    J --> K
```

## 四、页面权限

前端封装了一个指令权限，能简单快速的实现按钮级别的权限判断。

**使用权限字符串 v-hasPermi**：`@/directive/permission/hasPermi.js`

```html
<!-- 单个 -->
<el-button v-hasPermi="['system:user:add']">存在权限字符串才能看到</el-button>
<!-- 多个 -->
<el-button v-hasPermi="['system:user:add', 'system:user:edit']">包含权限字符串才能看到</el-button>
```

**使用角色字符串 v-hasRole**`@/directive/permission/hasRole.js`

```html
<!-- 单个 -->
<el-button v-hasRole="['admin']">管理员才能看到</el-button>
<!-- 多个 -->
<el-button v-hasRole="['role1', 'role2']">包含角色才能看到</el-button>
```

## 五、菜单获取

用户登录系统时，看到的侧边栏菜单，根据他的角色权限而有所不同。而超级管理员，是可以查看所有菜单的。

- 实现此功能的前端代码，位于 `src/permission.js` 文件。它在登录成功后，会在跳转到新路由之前，去查询当前用户有权访问的动态菜单的路由列表。
- 后端处理逻辑，则在 `ruoyi-admin` 模块的 `SysLoginController` 中，它负责接收前端发来的请求，处理查询，并构建起一个完整的菜单树结构，然后返回给前端。

> 若依中的菜单层级如下：
>
> ```mermaid
> graph LR
> A[主类目] -->B[工单管理（目录菜单）]
>     A --> C[点位管理（目录菜单）]
>     C --> D[区域管理（组件菜单）]
>     C --> E[点位管理（组件菜单）]
>     C --> F[合作商管理（组件菜单）]
> ```

### 5.1.前端部分

1. 在全局 `permission.js` 中的 `router.beforeEach` 方法，用于在用户导航到不同路由之前进行一些预处理；
2. 调用 `store/mondles/permission.js` 中的 `generateRoutes` 方法，将返回结果存入 `usePermissionStore` 对象中。
3. 调用 `api/menu.js` 中的 `getRouters` 方法

### 5.2.后段部分

#### 5.2.1.SysLoginController

在 `ruoyi-admin` 模块中 `com.ruoyi.web.controller.system.SysLoginController` 类的 `getRouters` 方法，接收前端获取路由信息请求。

#### 5.2.2.ISysMenuService

在 `ruoyi-system` 模块中 `com.ruoyi.web.system.service.ISysMenuService` 类：

1. `selectMenuTreeByUserId` 方法，根据用户ID，查询菜单树信息（递归生成父子菜单）。
2. `buildMenus` 方法，构建前端路由所需要的菜单路由格式 `RouterVo`

具体的执行的流程如下图：

```mermaid
graph TD
A(前端请求) --> B[获取当前登录用户id]
	B --> C[查询菜单权限]
	C --> D{判断是否为admin}
	D -->|yes| E[查询所有菜单权限]
	D -->|no| F[根据用户id查询菜单权限]
	E --> G[构建菜单数]
	F --> G
	G --> H[构建前端路由所需要的菜单]
	H --> I(返回数据)
```

## 六、动态路由加载

1. 用户登录成功后，通过路由 `router/index.js` 跳转到首页，并加载 layout 布局组件。
2. 在 `layout/index.vue` 中加载 sidbar 侧边栏；
3. 在 `layout/components/Sidebar/index.vue` 中，遍历动态路由菜单，在页面显示；
4. 用户点击菜单后，会根据路由的 path，跳转到对应的视图组件在 `<app-main />` 显示

## 七、权限注解

在若依框架中，权限的验证，核心的是使用 Spring Security 提供的权限注解 `@PreAuthorize`。

- `@PreAuthorize` 是 Spring Security 框架中，提供的一个安全注解，用于实现基于注解的访问控制。
  - 它允许开发者在**方法级别**上声明特定的安全约束，以确保只有满足指定条件的用户，才能调用该方法。

  - 当该注解被应用于某个方法时，Spring Security 在该方法执行前，会先对当前用户，进行权限检查。
    - 如果检查通过，方法调用得以继续；

    - 否则，框架会抛出相应的权限异常（如 `AccessDeniedException`），阻止方法执行。


比如下方代码：

dkd-manage/src/main/java/com/dkd/manage/controller/TaskTypeController.java

```java
/**
 * 查询工单类型列表
 */
@PreAuthorize("@ss.hasPermi('manage:taskType:list')")
@GetMapping("/list")
public TableDataInfo list(TaskType taskType) {
    startPage();
    List<TaskType> list = taskTypeService.selectTaskTypeList(taskType);
    return getDataTable(list);
}
```

- `@PreAuthorize` 是 Spring Security 框架的权限注解，在执行方法前执行。

- `@ss.hasPermi('manage:order:list')`，其中的：
  - `@ss` 是指的一个 spring 管理的 bean
    - 位置：ruoyi-framework 模块中的 `com.ruoyi.framework.web.service.PermissionService`
  - `hasPermi` 是 `PermissionService` 类中的一个方法，判断是否拥有该权限。
  - `manage:taskType:list` 为方法的参数。

dkd-framework/src/main/java/com/dkd/framework/web/service/PermissionService.java

```java
/**
 * RuoYi首创 自定义权限实现，ss取自SpringSecurity首字母
 *
 * @author ruoyi
 */
@Service("ss")
public class PermissionService {

    /**
     * 检查用户是否拥有指定的权限
     *
     * @param permission 待检查的权限字符串
     * @return 如果用户拥有指定的权限，则返回true；否则返回false
     */
    public boolean hasPermi(String permission) {
            // 检查传入的权限字符串是否为空
            if (StringUtils.isEmpty(permission)) {
                return false;
            }

            // 获取当前登录的用户信息
            LoginUser loginUser = SecurityUtils.getLoginUser();

            // 检查登录用户是否存在以及用户权限列表是否为空
            if (StringUtils.isNull(loginUser) || CollectionUtils.isEmpty(loginUser.getPermissions())) {
                return false;
            }

            // 将权限字符串设置到权限上下文中，用于后续的权限检查
            PermissionContextHolder.setContext(permission);

            // 调用方法检查用户权限列表中是否包含指定的权限
            return hasPermissions(loginUser.getPermissions(), permission);
    }
  
   // ……
}
```

权限控制流程如下：

```mermaid
graph TD
A(LoginUser) -->|基本信息| B[zetian]
    A -->|角色集合| C[admin]
    A -->|权限集合| D[*:*:*]
```

```mermaid
graph TD
A(查询订单列表) --> B(@Preauthorize注解拦截)
    B -->|@PreAuthorize（“@ss.hasPermi（“manage:taskType”）”）| C[获取当前登录用户信息]
    C --> D{判断当前权限是否登录用户的权限列表中}
    D -->|否| E(权限不足)
    D -->|否| F(放行)
```

### 7.1.权限方法

`@ss` 注解，要求接口拥有用户某些权限才可访问，它拥有如下方法：

| 方法        | 参数   | 描述                                           |
| ----------- | ------ | ---------------------------------------------- |
| hasPermi    | String | 验证用户是否具备某权限                         |
| lacksPermi  | String | 验证用户是否不具备某权限，与 hasPermi 逻辑相反 |
| hasAnyPermi | String | 验证用户是否具有以下任意一个权限               |
| hasRole     | String | 判断用户是否拥有某个角色                       |
| lacksRole   | String | 验证用户是否不具备某角色，与 hasRole 逻辑相反  |
| hasAnyRoles | String | 验证用户是否具有以下任意一个角色，多个逗号分隔 |

权限方法使用示例：

#### 7.1.1.在注解中使用

数据权限示例。

```java
// 符合 system:user:list 权限要求
@PreAuthorize("@ss.hasPermi('system:user:list')")

// 不符合 system:user:list 权限要求
@PreAuthorize("@ss.lacksPermi('system:user:list')")

// 符合 system:user:add 或 system:user:edit 权限要求即可
@PreAuthorize("@ss.hasAnyPermi('system:user:add,system:user:edit')")
```

角色权限示例。

```java
// 属于 user 角色
@PreAuthorize("@ss.hasRole('user')")

// 不属于 user 角色
@PreAuthorize("@ss.lacksRole('user')")

// 属于 user 或者 admin 之一
@PreAuthorize("@ss.hasAnyRoles('user,admin')")
```

#### 7.1.2.编程式的使用

数据权限示例。

```java
if (SecurityUtils.hasPermi("sys:user:edit")) {
    System.out.println("当前用户有编辑用户权限");
}
```

角色权限示例。

```java
if (SecurityUtils.hasRole("admin")) {
    System.out.println("当前用户有admin角色权限");
}
```

#### 7.1.3.公开接口

如果有些接口，不需要验证权限，可以公开访问，只需要加 `@Anonymous` 注解即可。

```java
// @PreAuthorize("@ss.xxxx('....')") 注释或删除掉原有的权限注解
@Anonymous
@GetMapping("/list")
public List<SysXxxx> list(SysXxxx xxxx) {
    return xxxxList;
}
```

