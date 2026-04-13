# CLAUDE.md

这是 Claude (AI 编程助手) 的项目索引文档，帮助快速了解项目结构和相关文档。

## 项目概述

供应链管理系统，基于若依框架开发。

- **后端**: Spring Boot + MySQL + Redis
- **前端**: Vue 3 + Element Plus + Vite
- **部署**: GitHub Actions + Docker Compose

## 项目结构

```
supply_chain_management/
├── ruoyi-admin/          # 后端主模块
├── ruoyi-framework/      # 框架核心
├── ruoyi-system/         # 系统模块
├── ruoyi-generator/      # 代码生成
├── ruoyi-ui/            # 前端项目
├── docs/                # 项目文档
├── .github/             # GitHub Actions 配置
└── docker-compose.yml   # Docker 部署配置
```

## 相关文档

| 文档 | 说明 |
|------|------|
| [部署文档](docs/deployment.md) | GitHub Actions 自动化部署、Docker 配置、Nginx 配置 |
| [README.md](README.md) | 项目介绍和本地开发指南 |

## 开发规范

### 后端开发
- 基于 Spring Boot 3.x
- 使用 MyBatis Plus
- 遵循 RESTful API 设计

### 前端开发
- Vue 3 Composition API
- Element Plus 组件库
- Pinia 状态管理

### Git 工作流
- `main` 分支用于生产环境
- 推送到 main 分支触发自动部署
- 前后端独立部署

## 部署相关

### 自动部署触发条件
- **前端**: 修改 `ruoyi-ui/` 目录下的文件
- **后端**: 修改 `ruoyi-*/` 或 `pom.xml` 文件

### 服务器环境
- 前端: `/www/wwwroot/zzj.nnnnzs.cn/dist`
- 后端: `/www/wwwroot/zzj.nnnnzs.cn/backend`
- Nginx: 反向代理后端 API 到 `/api/` 路径

详细配置请参考 [部署文档](docs/deployment.md)。
