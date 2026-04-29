# 我的厨房 - 美食食谱分享平台

## 1. Concept & Vision

一个温馨、实用的食谱分享平台，灵感来自「下厨房」。界面以食物摄影为核心，营造出"厨房"的温暖氛围。整体设计简洁优雅，让用户专注于发现和分享美食。

## 2. Design Language

### Aesthetic Direction
温暖的厨房风格 - 以暖色调为主，配合大量留白和食物摄影，呈现出"家的味道"。

### Color Palette
- Primary: `#FF6B35` (温暖橙色 - 代表烹饪的热情)
- Secondary: `#2D3436` (深灰色 - 文字)
- Accent: `#00B894` (清新绿色 - 成功/新鲜)
- Background: `#FFFBF7` (米白色 - 温暖的底色)
- Surface: `#FFFFFF` (白色卡片)
- Text Primary: `#2D3436`
- Text Secondary: `#636E72`
- Border: `#EAE0D5`

### Typography
- Headings: `'Noto Serif SC', serif` - 优雅的中文衬线
- Body: `'Noto Sans SC', sans-serif` - 清晰可读

### Spatial System
- Base unit: 8px
- Card padding: 16px
- Section gap: 32px
- Container max-width: 1200px

### Motion Philosophy
- 入场动画: 卡片从下往上淡入 (translateY: 20px -> 0, opacity: 0 -> 1, 300ms ease-out)
- 悬停效果: 图片轻微放大 (scale: 1.02), 阴影加深
- 页面切换: 内容淡入 (opacity, 200ms)

### Visual Assets
- Icons: Lucide React (线条风格，stroke-width: 2)
- 食物图片: Unsplash 食物摄影
- 装饰元素: 圆润的边角, 柔和的阴影

## 3. Layout & Structure

### Header
- Logo: "我的厨房" 文字logo
- 导航: 首页 / 分类 / 收藏
- 搜索框 (居中)
- 用户头像/登录入口

### 首页
1. Hero区域 - 精选食谱轮播/大图
2. 快捷分类入口 - 早餐/午餐/晚餐/甜点等图标
3. 热门食谱 - 2-3列网格
4. 推荐作者 - 横向滚动卡片

### 食谱详情页
- 大图展示
- 标题、作者信息、收藏数
- 食材清单
- 烹饪步骤 (编号卡片)
- 相关推荐

### 分类页
- 左侧分类列表
- 右侧食谱网格

### 响应式策略
- Desktop: 多列布局
- Tablet: 2列
- Mobile: 单列，汉堡菜单

## 4. Features & Interactions

### 核心功能
1. **浏览食谱** - 瀑布流/网格展示
2. **搜索** - 实时搜索食谱名称
3. **收藏** - 点击心形图标收藏/取消
4. **分类筛选** - 按菜系/场景/难度筛选
5. **查看详情** - 完整食谱页面
6. **评论互动** - 对食谱进行评论

### 交互细节
- 卡片悬停: 阴影加深 + 图片缩放
- 收藏按钮: 心形填充动画
- 搜索框: 聚焦时边框高亮
- 加载状态: 骨架屏占位

### 状态处理
- 空状态: "暂无符合条件的食谱"
- 加载状态: 骨架屏动画
- 错误状态: 友好提示 + 重试按钮

## 5. Component Inventory

### Header
- Logo文字，Home链接
- 导航链接 (当前页面高亮)
- 搜索输入框
- 状态: default

### RecipeCard
- 图片 (16:12 比例)
- 标题 (最多2行)
- 作者头像 + 名称
- 收藏按钮
- 状态: default / hover / favorited

### CategoryPill
- 圆形图标 + 文字
- 状态: default / selected

### RecipeDetail
- 顶部大图
- 元信息区
- 食材列表
- 步骤列表
- 收藏按钮

### SearchBar
- 搜索图标 + 输入框
- 状态: default / focused / with-value

## 6. Technical Approach

### 前端技术栈
- **Framework**: React 18 + TypeScript
- **Build**: Vite (端口: 5174)
- **Routing**: React Router v6
- **Icons**: Lucide React
- **Styling**: CSS Modules
- **HTTP Client**: Fetch API

### 后端技术栈
- **Framework**: Spring Boot 3.2.0
- **ORM**: MyBatis 3.0.3
- **Database**: MySQL 8.0
- **Java Version**: 17
- **API Port**: 8080

### 数据库设计
- `user` - 用户表
- `recipe` - 食谱表
- `ingredient` - 食材表
- `step` - 步骤表
- `favorite` - 收藏表
- `comment` - 评论表

### API 接口

#### 食谱相关
- `GET /api/recipes` - 获取所有食谱
- `GET /api/recipes/{id}` - 获取食谱详情
- `GET /api/recipes/category/{category}` - 按分类获取
- `GET /api/recipes/search?keyword=` - 搜索食谱
- `GET /api/recipes/popular` - 获取热门食谱

#### 收藏相关
- `GET /api/favorites/user/{userId}` - 获取用户收藏
- `GET /api/favorites/check` - 检查是否收藏
- `POST /api/favorites` - 添加收藏
- `DELETE /api/favorites` - 取消收藏

#### 评论相关
- `GET /api/comments/recipe/{recipeId}` - 获取食谱评论
- `GET /api/comments/recent` - 获取最新评论
- `POST /api/comments` - 添加评论
- `DELETE /api/comments/{id}` - 删除评论

#### 用户相关
- `GET /api/users/{id}` - 获取用户信息
- `POST /api/users/register` - 用户注册
- `PUT /api/users/{id}` - 更新用户信息

### 项目结构

```
my-kitchen/
├── frontend/               # 前端 (可选，当前直接在根目录)
│   ├── src/
│   │   ├── components/     # UI组件
│   │   ├── pages/          # 页面组件
│   │   ├── services/       # API服务
│   │   ├── types/          # TypeScript类型
│   │   └── styles/          # 全局样式
│   └── vite.config.js
│
└── backend/                 # 后端 Spring Boot 项目
    ├── src/main/java/com/mykitchen/
    │   ├── controller/     # 控制器
    │   ├── service/        # 服务层
    │   ├── mapper/         # MyBatis Mapper
    │   ├── entity/         # 实体类
    │   └── MyKitchenApplication.java
    └── src/main/resources/
        ├── application.yml
        └── schema.sql      # 数据库脚本
```

## 7. 快速开始

### 前端启动
```bash
cd my-kitchen
npm install
npm run dev
# 访问 http://localhost:5174
```

### 后端启动
1. 确保 MySQL 已运行并创建数据库
2. 执行 `backend/src/main/resources/schema.sql`
3. 修改 `application.yml` 中的数据库配置
```bash
cd backend
mvn spring-boot:run
# API 服务运行在 http://localhost:8080
```
