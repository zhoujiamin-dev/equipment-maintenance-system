# 企业设备运维与故障辅助分析系统

一个面向企业设备维护场景的综合实践项目。

系统围绕“设备管理、维修工单、故障辅助分析、维修历史追溯”展开，在基础设备运维业务之上，进一步接入了 Redis、JWT/RBAC、Docker Compose，以及基于 PgVector + Embedding + 大语言模型的 RAG 检索增强故障分析能力。

---

## 一、项目简介

本项目采用前后端分离架构。

前端使用 Vue 3 + TypeScript + Element Plus 构建后台管理页面，后端使用 Spring Boot + MyBatis-Plus + MySQL 实现设备、工单、用户、角色等核心业务。

在设备发生异常后，系统支持录入故障现象，并通过 RAG 检索增强流程，从本地维修知识库中检索相关资料，再结合大语言模型生成故障原因、检查建议和注意事项。

AI 分析结果支持持久化，并可以根据分析记录一键创建维修工单，实现：

故障描述 → 知识检索 → AI 分析 → 保存分析记录 → 创建维修工单 → 状态流转 → 维修历史追溯

的完整业务闭环。

---

## 二、核心功能

### 1. 设备管理
- 设备信息分页查询
- 按设备名称进行模糊查询
- 按设备状态筛选
- 查看设备详情
- 管理设备状态
- 查看设备维修历史
- 管理员删除设备
- Redis 缓存设备详情

### 2. 维修工单
- 新增维修工单
- 工单分页查询
- 按标题、状态筛选
- 工单状态流转：待处理 → 处理中 → 已完成
- 开始维修时联动设备状态
- 完成维修时恢复设备状态
- 保存维修处理结果
- 防止同一设备重复创建未完成工单
- 通过事务保证工单与设备状态更新一致性

### 3. 登录认证与权限
- 用户登录
- BCrypt 密码校验
- JWT 身份认证
- Axios 请求拦截器自动携带 Token
- 后端拦截器校验 Token
- 基础 RBAC 权限控制
- ADMIN / WORKER 角色区分
- 前端按角色控制操作入口
- 后端进行接口级权限校验

### 4. AI 故障辅助分析
- 选择设备
- 输入故障现象
- 调用大语言模型生成辅助分析结果
- 输出可能原因
- 输出检查建议
- 输出注意事项
- 分析结果持久化
- 一键根据分析结果生成维修工单
- 回写分析记录与工单关联关系

### 5. RAG 检索增强
- 本地维修知识库
- 文本知识向量化
- Embedding 服务
- PgVector 向量数据库
- 向量相似度检索
- 根据设备类型过滤知识
- 返回参考维修资料 sources
- 将检索结果作为上下文交给大语言模型
- 对资料不足场景进行保守回答
- 降低无依据故障结论

### 6. Redis 缓存
- 缓存设备详情
- 使用 Cache Aside 模式
- 查询时优先读取 Redis
- 未命中时查询 MySQL
- 查询后写入 Redis
- 设置缓存 TTL
- 设备更新后主动删除缓存
- 工单状态联动设备状态时同步失效缓存

### 7. Docker Compose 部署
当前 Compose 编排包含：
- MySQL
- Redis
- PgVector
- Spring Boot 后端

通过环境变量管理：
- MySQL 密码
- PgVector 密码
- JWT Secret
- 大模型 API Key

同时配置：
- MySQL 健康检查
- Redis 健康检查
- PgVector 健康检查
- 数据卷持久化
- 服务启动依赖

---

## 三、技术栈

### 后端
- Java 17
- Spring Boot
- MyBatis-Plus
- MySQL
- Redis
- Spring Data Redis
- JWT
- BCrypt
- Maven
- RestTemplate

### AI / RAG
- 大语言模型 API
- Embedding
- PgVector
- PostgreSQL
- RAG
- 向量检索
- Prompt 上下文增强

### 前端
- Vue 3
- TypeScript
- Element Plus
- Axios
- Vue Router
- Vite

### 部署与工具
- Docker
- Docker Compose
- Git
- Linux
- PowerShell

---

## 四、项目结构

    equipment-maintenance-system
    ├─ backend
    │  └─ equipment-server
    │     ├─ src
    │     │  ├─ main
    │     │  │  ├─ java
    │     │  │  └─ resources
    │     │  │     └─ knowledge
    │     ├─ Dockerfile
    │     ├─ docker-compose.yml
    │     ├─ equipment_db.sql
    │     └─ pom.xml
    │
    ├─ frontend
    │  └─ equipment-maintenance-web
    │     ├─ src
    │     │  ├─ api
    │     │  ├─ layout
    │     │  ├─ router
    │     │  ├─ utils
    │     │  └─ views
    │     └─ package.json
    │
    ├─ .gitignore
    └─ README.md

---

## 五、后端分层

后端主要采用：

    Controller
    ↓
    Service
    ↓
    Mapper
    ↓
    MyBatis-Plus
    ↓
    MySQL

各层职责：
- Controller：接收 HTTP 请求
- Service：处理业务逻辑
- Mapper：负责数据库访问
- MyBatis-Plus：简化 CRUD、分页、条件查询

---

## 六、前端请求流程

    Vue 页面
    ↓
    API 文件
    ↓
    Axios
    ↓
    request.ts 请求拦截器
    ↓
    自动携带 JWT Token
    ↓
    Spring Boot
    ↓
    Controller
    ↓
    Service
    ↓
    数据库 / Redis / AI 服务

---

## 七、RAG 故障分析流程

    用户填写故障描述
    ↓
    前端调用 /rag/analyze
    ↓
    EmbeddingService 生成问题向量
    ↓
    PgVector 执行向量相似度检索
    ↓
    RagRetrievalService 获取相关维修知识
    ↓
    组合用户问题 + 检索资料
    ↓
    调用大语言模型
    ↓
    生成故障分析结果
    ↓
    保存分析记录
    ↓
    返回 analysisId + analysisResult + sources
    ↓
    前端展示分析结果和参考维修资料
    ↓
    可继续一键创建维修工单

---

## 八、Redis 缓存流程

设备详情采用 Cache Aside 模式：

    查询设备详情
    ↓
    先查 Redis
    ↓
    命中 → 直接返回
    ↓
    未命中 → 查询 MySQL
    ↓
    写入 Redis
    ↓
    设置 TTL
    ↓
    返回数据

当设备信息发生更新，或工单状态变化联动设备状态时：

    更新 MySQL
    ↓
    删除对应 Redis 缓存
    ↓
    下一次查询重新从 MySQL 构建缓存

---

## 九、JWT 与 RBAC

登录成功后：

    用户名 + 密码
    ↓
    BCrypt 校验
    ↓
    生成 JWT
    ↓
    返回 Token + Role
    ↓
    前端保存 Token
    ↓
    Axios 后续请求自动携带 Authorization
    ↓
    后端拦截器校验 JWT
    ↓
    根据角色执行权限判断

当前项目主要区分：
- ADMIN
- WORKER

前端权限控制主要用于界面入口展示。

真正的权限安全校验仍由后端完成。

---

## 十、运行配置

敏感配置通过环境变量提供。

示例：
- `DB_PASSWORD`
- `DEEPSEEK_API_KEY`
- `JWT_SECRET`
- `PGVECTOR_PASSWORD`

项目仓库不会提交真实 `.env` 文件和真实密钥。

---

## 十一、Docker Compose

在后端目录准备好环境变量后，可使用：

    docker compose up -d --build

查看服务状态：

    docker compose ps

停止服务：

    docker compose down

---

## 十二、前端启动

进入前端目录：

    cd frontend/equipment-maintenance-web

安装依赖：

    npm install

启动：

    npm run dev

默认开发地址：

    http://localhost:5173

---

## 十三、项目特点

本项目不是单纯 CRUD 示例，而是围绕设备维修业务形成了完整流程：

    设备
    ↓
    故障
    ↓
    RAG 知识检索
    ↓
    AI 辅助分析
    ↓
    分析结果持久化
    ↓
    维修工单
    ↓
    状态流转
    ↓
    设备状态联动
    ↓
    维修结果
    ↓
    维修历史

同时结合：
- JWT
- RBAC
- Redis
- Docker Compose
- PgVector
- Embedding
- RAG
- 大语言模型 API

完成了从基础信息化系统到 AI 辅助运维场景的综合实践。

---

