# 我的厨房 - 企业级食谱分享平台

一个功能完善的食谱分享平台，支持用户认证、食谱管理、社交互动等功能。

## 功能特性

### 用户系统
- [x] JWT 无状态认证
- [x] 用户注册/登录/登出
- [x] Token 刷新机制
- [x] 个人资料管理
- [x] 头像设置

### 食谱管理
- [x] 食谱浏览（首页/分类/收藏）
- [x] 食谱搜索
- [x] 创建食谱（支持多步骤、多食材）
- [x] 编辑/删除食谱
- [x] 收藏/取消收藏
- [x] 评论功能
- [x] 评分系统（1-5星）

### 社交功能
- [x] 关注/取消关注用户
- [x] 关注列表/粉丝列表
- [x] 动态 Feed 发布
- [x] 动态点赞
- [x] 消息通知系统

### 企业级特性
- [x] Redis 缓存（用户、食谱、标签）
- [x] API 文档 (Swagger/OpenAPI)
- [x] 操作日志记录 (AOP)
- [x] 标签系统
- [x] 热门内容排行

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | React 18 + TypeScript |
| 构建工具 | Vite 5 |
| 路由 | React Router 6 |
| 后端框架 | Spring Boot 3.2 |
| ORM | MyBatis 3.0 |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis |
| 认证 | JWT + Spring Security |
| 文档 | Swagger/OpenAPI 3.0 |

## 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+（可选）

## 快速启动

### 1. 配置数据库

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/my_kitchen
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```

### 2. 初始化数据库

```bash
cd backend
mysql -u root -p < src/main/resources/schema.sql
```

### 3. 启动服务

**Windows:**
```bash
双击运行 start.bat
```

**Mac/Linux:**
```bash
chmod +x start.sh
./start.sh
```

**手动启动:**
```bash
# 终端1 - 后端
cd backend
mvn spring-boot:run

# 终端2 - 前端
npm install
npm run dev
```

## 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5174 |
| 后端 API | http://localhost:8080 |
| Swagger 文档 | http://localhost:8080/swagger-ui.html |
| API JSON | http://localhost:8080/api-docs |

## 默认测试账号

| 用户名 | 密码 | 说明 |
|--------|------|------|
| chef_wang | 123456 | 王大厨 |
| sweet_home | 123456 | 甜甜家 |
| healthy_eat | 123456 | 健康饮食 |

## API 接口一览

### 认证相关
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/refresh | 刷新令牌 |
| POST | /api/auth/logout | 退出登录 |

### 用户相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users/{id} | 获取用户信息 |
| PUT | /api/users/{id} | 更新用户信息 |
| GET | /api/users/username/{username} | 按用户名查询 |

### 食谱相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/recipes | 获取所有食谱 |
| GET | /api/recipes/{id} | 获取食谱详情 |
| GET | /api/recipes/category/{category} | 按分类获取 |
| GET | /api/recipes/search | 搜索食谱 |
| POST | /api/recipes | 创建食谱 |
| PUT | /api/recipes/{id} | 更新食谱 |
| DELETE | /api/recipes/{id} | 删除食谱 |

### 收藏相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/favorites/user/{userId} | 获取用户收藏 |
| POST | /api/favorites | 添加收藏 |
| DELETE | /api/favorites | 取消收藏 |

### 标签相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/tags | 获取所有标签 |
| GET | /api/tags/popular | 获取热门标签 |
| POST | /api/tags | 创建标签 |

### 评分相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/ratings/recipe/{id} | 获取评分统计 |
| POST | /api/ratings | 评分 |

### 关注相关
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/follow | 关注用户 |
| DELETE | /api/follow | 取消关注 |
| GET | /api/follow/stats/{userId} | 获取关注统计 |
| GET | /api/follow/following/{userId} | 获取关注列表 |
| GET | /api/follow/followers/{userId} | 获取粉丝列表 |

### 动态相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/feeds | 获取最新动态 |
| POST | /api/feeds | 发布动态 |
| DELETE | /api/feeds/{id} | 删除动态 |

### 通知相关
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/notifications | 获取通知列表 |
| GET | /api/notifications/unread-count | 未读数量 |
| POST | /api/notifications/{id}/read | 标记已读 |

## 项目结构

```
my-kitchen/
├── src/                          # 前端源码
│   ├── pages/                    # 页面组件
│   │   ├── HomePage/            # 首页
│   │   ├── CategoriesPage/      # 分类页
│   │   ├── RecipePage/          # 食谱详情页
│   │   ├── FavoritesPage/       # 收藏页
│   │   ├── LoginPage/           # 登录页
│   │   ├── RegisterPage/        # 注册页
│   │   ├── ProfilePage/         # 个人中心
│   │   └── CreateRecipePage/    # 创建食谱
│   ├── components/               # 公共组件
│   │   ├── Header/              # 页头
│   │   ├── RecipeCard/          # 食谱卡片
│   │   └── CategoryPill/        # 分类标签
│   ├── services/                # API 服务
│   │   └── api.ts               # API 封装
│   └── styles/                  # 样式文件
│
├── backend/                      # 后端源码
│   └── src/main/java/com/mykitchen/
│       ├── controller/           # 控制器
│       │   ├── AuthController.java
│       │   ├── UserController.java
│       │   ├── RecipeController.java
│       │   ├── FavoriteController.java
│       │   ├── CommentController.java
│       │   ├── TagController.java
│       │   ├── RatingController.java
│       │   ├── FollowController.java
│       │   ├── FeedController.java
│       │   └── NotificationController.java
│       ├── service/              # 业务逻辑
│       ├── mapper/              # 数据访问
│       ├── entity/              # 实体类
│       ├── config/              # 配置类
│       │   ├── RedisConfig.java
│       │   ├── SecurityConfig.java
│       │   └── SwaggerConfig.java
│       ├── security/            # 安全模块
│       │   ├── JwtUtil.java
│       │   └── JwtAuthenticationFilter.java
│       └── aspect/              # AOP 切面
│           └── OperationLogAspect.java
│
├── start.bat                     # Windows 启动脚本
├── start.sh                      # Linux/Mac 启动脚本
├── package.json                  # 前端依赖
└── pom.xml                       # 后端依赖
```

## 数据库表结构

| 表名 | 说明 |
|------|------|
| user | 用户表 |
| recipe | 食谱表 |
| ingredient | 食材表 |
| step | 步骤表 |
| favorite | 收藏表 |
| comment | 评论表 |
| tag | 标签表 |
| recipe_tag | 食谱-标签关联表 |
| rating | 评分表 |
| follow | 关注表 |
| feed | 动态表 |
| feed_like | 动态点赞表 |
| notification | 通知表 |
| operation_log | 操作日志表 |

## License

MIT License
