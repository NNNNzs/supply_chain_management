# GitHub Actions 部署配置说明

## 部署流程

本项目使用 GitHub Actions 实现自动化部署，分为前端和后端两个独立的 workflow。

## 前端部署

- **触发条件**: `main` 分支上 `ruoyi-ui` 目录下的文件变更
- **构建工具**: pnpm + Vite
- **部署方式**: SCP 上传 dist 目录到服务器指定路径
- **特点**: 由服务器统一 nginx 管理，无需重启服务

## 后端部署

- **触发条件**: `main` 分支上后端相关文件变更（排除 `ruoyi-ui`）
- **构建工具**: Maven + JDK 17
- **输出文件**: `ruoyi-admin.jar`
- **部署方式**: SCP 上传 JAR 包 -> SSH 执行部署脚本 -> Docker 重启
- **特点**: 自动备份旧版本、Docker 容器化部署、健康检查

## GitHub Secrets 配置

在 GitHub 仓库中配置以下 Secrets（Settings → Secrets and variables → Actions → New repository secret）：

### 服务器连接配置

| Secret 名称 | 说明 | 示例 |
|------------|------|------|
| `SERVER_HOST` | 服务器 IP 地址或域名 | `192.168.1.100` 或 `example.com` |
| `SERVER_PORT` | SSH 端口 | `22` |
| `SERVER_USER` | SSH 用户名 | `root` 或 `ubuntu` |
| `SERVER_SSH_KEY` | SSH 私钥 | 完整的私钥内容 |

### 部署路径配置

| Secret 名称 | 说明 | 示例 |
|------------|------|------|
| `FRONTEND_DEPLOY_DIR` | 前端部署目录 | `/var/www/supply-chain` |
| `BACKEND_DEPLOY_DIR` | 后端部署目录 | `/opt/ruoyi-backend` |
| `DOCKER_CONTAINER_NAME` | Docker 容器名称 | `ruoyi-backend` |

## SSH 密钥生成（如果还没有）

```bash
# 在本地机器上生成 SSH 密钥对
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions

# 将公钥添加到服务器
ssh-copy-id -i ~/.ssh/github_actions.pub user@your-server.com

# 将私钥内容复制到 GitHub Secrets
cat ~/.ssh/github_actions
```

## 服务器准备

### 前端服务器

确保 nginx 配置正确指向 `FRONTEND_DEPLOY_DIR` 目录。

### 后端服务器

确保服务器已安装 Docker：

```bash
# 安装 Docker
curl -fsSL https://get.docker.com | sh

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
```

## 首次部署

1. 配置好 GitHub Secrets
2. 确保 `BACKEND_DEPLOY_DIR` 目录存在且可写
3. 推送代码到 `main` 分支触发部署

## 部署脚本

后端部署脚本位于 `.github/scripts/deploy-backend.sh`，会自动：

1. 备份现有的 JAR 包（保留最近 3 个）
2. 停止并删除旧容器
3. 构建新的 Docker 镜像
4. 启动新容器
5. 执行健康检查

## 查看部署状态

在 GitHub 仓库的 "Actions" 标签页可以查看所有 workflow 的执行状态和日志。

## 故障排查

### 前端部署失败

1. 检查 `FRONTEND_DEPLOY_DIR` 是否存在且有写权限
2. 检查 SSH 连接是否正常
3. 查看 workflow 日志

### 后端部署失败

1. 检查 Docker 是否已安装
2. 检查端口 8080 是否被占用
3. 查看容器日志：`docker logs ruoyi-backend`
4. 检查防火墙规则

## 手动执行部署脚本

如果需要手动在服务器上执行部署：

```bash
cd /opt/ruoyi-backend
chmod +x deploy-backend.sh
./deploy-backend.sh
```
