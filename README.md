<div align="center">

<h1>梦谱</h1>

<p>通过自然语言对话，让 AI 为你生成完整的前端应用代码</p>

</div>

---

## 📖 项目简介

梦谱是一个创新的无代码开发平台，用户无需编写代码，只需通过自然语言描述需求，AI 就能自动生成完整的前端应用。平台支持多种代码生成模式，从简单的单页面到复杂的 Vue 项目，满足不同场景的需求。

### 核心亮点

- 🤖 **AI 驱动** - 基于 Langchain4j + DeepSeek 大模型，智能理解用户需求
- 💬 **对话式开发** - 像聊天一样开发应用，所见即所得
- 🎨 **多种生成模式** - 支持 HTML、多文件、Vue 项目三种模式
- 🚀 **一键部署** - 生成的应用可直接部署
- 📦 **代码下载** - 支持下载完整源码，可二次开发
- 📸 **实时预览** - 生成完可实时查看效果，并支持在线编辑页面

---

## 🛠 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.x | 核心框架 |
| Langchain4j | 1.10.0 | AI 应用开发框架 |
| MyBatis-Flex | 1.11.5 | ORM 框架 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存 + Session 存储 |
| Redisson | 3.50.0 | 分布式锁 |
| 网页截图服务 | - | 第三方 API（推荐）或 Selenium |
| 腾讯云 COS | 5.6.227 | 对象存储 |
| Prometheus | - | 监控指标 |
| Knife4j | 4.4.0 | API 文档 |

### 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.x | 前端框架 |
| Pinia | 3.0.x | 状态管理 |
| Vue Router | 4.5.x | 路由管理 |
| Ant Design Vue | 4.2.x | UI 组件库 |
| Axios | 1.13.x | HTTP 客户端 |
| Vite | 7.0.x | 构建工具 |
| TypeScript | 5.8.x | 类型支持 |

---

## 📋 环境要求

### 必需环境

| 环境 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 21+ | Java 运行环境 |
| Node.js | 20+ | 前端构建环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存服务 |

---

## 🚀 快速开始

### 方式一：Docker 部署（推荐）

#### 1. 克隆项目

```bash
git clone https://github.com/hzeze/ai-codeless-app-builder.git
cd ai-codeless-app-builder
```

#### 2. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件，配置必要的环境变量
# 必须配置：OPENAI_API_KEY
# 可选配置：COS 相关配置、截图 API 配置等
```

#### 3. 启动服务

```bash
# 启动所有服务（MySQL、Redis、后端、前端）
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f
```

#### 4. 访问应用

- **前端应用**: http://localhost
- **后端 API**: http://localhost:8123
- **API 文档**: http://localhost:8123/api/doc.html

#### 5. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止服务并删除数据卷（慎用）
docker-compose down -v
```

### 方式二：本地开发部署

#### 1. 克隆项目

```bash
git clone https://github.com/hzeze/ai-codeless-app-builder.git
cd ai-codeless-app-builder
```

#### 2. 数据库初始化

```bash
# 登录 MySQL
mysql -u root -p

# 执行建表脚本
source sql/create_table.sql
```

#### 3. 后端配置

修改 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_codeless_app_builder
    username: your_username
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password

# AI 模型配置
langchain4j:
  open-ai:
    chat-model:
      base-url: https://api.deepseek.com
      api-key: your_api_key
      model-name: deepseek-chat

# 腾讯云 COS 配置（可选）
cos:
  client:
    host: your_host
    secretId: your_secret_id
    secretKey: your_secret_key
    region: your_region
    bucket: your_bucket
```

#### 4. 启动后端

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

后端服务将在 `http://localhost:8123` 启动

#### 5. 前端配置与启动

```bash
# 进入前端目录
cd ai-codeless-app-builder-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

#### 6. 访问应用

打开浏览器访问 `http://localhost:5173`

---

## 📁 项目结构

```
ai-codeless-app-builder/
├── sql/                              # 数据库脚本
│   └── create_table.sql
├── src/                              # 后端源码
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hz/aicodelessappbuilder/
│   │   │       ├── ai/               # AI 核心模块
│   │   │       │   ├── guardrail/    # 输入安全护栏
│   │   │       │   ├── model/        # AI 模型定义
│   │   │       │   └── tools/        # AI 工具集
│   │   │       ├── config/           # 配置类
│   │   │       ├── controller/       # 控制器
│   │   │       ├── core/             # 核心业务
│   │   │       │   ├── builder/      # 项目构建器
│   │   │       │   ├── handler/      # 流式处理器
│   │   │       │   ├── parser/       # 代码解析器
│   │   │       │   └── saver/        # 文件保存器
│   │   │       ├── exception/        # 异常处理
│   │   │       ├── manager/          # 管理器
│   │   │       ├── mapper/           # MyBatis Mapper
│   │   │       ├── model/            # 实体模型
│   │   │       ├── monitor/          # 监控模块
│   │   │       ├── ratelimiter/      # 限流模块
│   │   │       ├── service/          # 业务服务
│   │   │       └── utils/            # 工具类
│   │   └── resources/
│   │       ├── prompt/               # AI Prompt 模板
│   │       └── application.yml       # 配置文件
│   └── test/                         # 测试代码
├── ai-codeless-app-builder-frontend/ # 前端源码
│   ├── src/
│   │   ├── api/                      # API 接口
│   │   ├── components/               # 公共组件
│   │   ├── layouts/                  # 布局组件
│   │   ├── pages/                    # 页面组件
│   │   │   ├── admin/                # 管理员页面
│   │   │   ├── app/                  # 应用页面
│   │   │   └── user/                 # 用户页面
│   │   ├── router/                   # 路由配置
│   │   ├── stores/                   # 状态管理
│   │   └── utils/                    # 工具函数
│   └── package.json
└── pom.xml                           # Maven 配置
```

---

## ⚙️ 配置说明

### AI 模型配置

本项目默认使用 DeepSeek API，也支持其他兼容 OpenAI API 格式的模型：

```yaml
langchain4j:
  open-ai:
    chat-model:
      base-url: https://api.deepseek.com  # 或其他 API 地址
      api-key: your_api_key
      model-name: deepseek-chat
    streaming-chat-model:
      base-url: https://api.deepseek.com
      api-key: your_api_key
      model-name: deepseek-chat
      max-tokens: 8192
```

### Redis 配置

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: 123456
      ttl: 3600
  session:
    store-type: redis
    timeout: 2592000  # 30 天
```

### 限流配置

系统内置基于 Redisson 的分布式限流，默认配置：

```java
@RateLimit(limitType = RateLimitType.IP, rate = 5, rateInterval = 60)
```

表示：每个 IP 在 60 秒内最多 5 次请求。

---

## 📝 API 文档

启动后端后，访问 Knife4j 接口文档：

```
http://localhost:8123/api/doc.html
```

### 主要接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/app/add` | POST | 创建应用 |
| `/api/app/chat/gen/code` | GET | AI 对话生成代码 (SSE) |
| `/api/app/deploy` | POST | 部署应用 |
| `/api/app/download/{appId}` | GET | 下载应用代码 |
| `/api/user/register` | POST | 用户注册 |
| `/api/user/login` | POST | 用户登录 |

---

## ⚠️ 注意事项

1. **API Key 安全**：请勿将 API Key 提交到版本控制，建议使用配置文件或配置中心管理

2. **Redis 密码**：生产环境务必设置 Redis 密码

3. **网页截图方案**：推荐使用第三方截图 API，相比 Selenium 方案更稳定，无需担心 Chrome 版本更新导致的驱动兼容问题

4. **文件存储**：如不使用腾讯云 COS，可修改代码使用本地存储

5. **跨域配置**：开发环境已配置跨域，生产环境请根据实际情况调整

6. **Session 过期**：默认 Session 过期时间为 30 天，可在配置中调整

---

## 🤝 贡献指南

欢迎提交 Issue 和 Pull Request！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

<div align="center">
⭐ 如果这个项目对你有帮助，请给一个 Star ⭐
</div>
