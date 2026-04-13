# 部署文档

本项目使用 GitHub Actions 实现自动化部署，支持前端和后端独立部署。

## 目录结构

```
项目根目录/
├── .github/
│   ├── workflows/
│   │   ├── deploy-frontend.yml    # 前端部署 workflow
│   │   └── deploy-backend.yml     # 后端部署 workflow
│   └── scripts/
│       └── deploy-backend.sh      # 后端部署脚本
├── docker-compose.yml             # 后端 Docker 配置
├── .env.example                   # 环境变量示例
└── ruoyi-ui/                      # 前端源码
```

## 服务器目录结构

```
/www/wwwroot/zzj.nnnnzs.cn/        # 项目根目录
├── dist/                          # 前端静态文件
│   ├── index.html
│   ├── static/
│   └── ...
└── backend/                       # 后端目录
    ├── ruoyi-admin.jar            # 应用 JAR 包
    ├── docker-compose.yml         # Docker 配置
    ├── .env                       # 环境变量配置
    ├── logs/                      # 日志目录
    ├── uploadPath/                # 上传文件目录
    └── deploy-backend.sh          # 部署脚本
```

## GitHub Secrets 配置

在 GitHub 仓库 `Settings → Secrets and variables → Actions` 中配置以下 Secrets：

### 服务器连接

| Secret 名称 | 说明 | 示例 |
|------------|------|------|
| `SERVER_HOST` | 服务器 IP 或域名 | `192.168.1.100` |
| `SERVER_PORT` | SSH 端口 | `22` |
| `SERVER_USER` | SSH 用户名 | `root` |
| `SERVER_SSH_KEY` | SSH 私钥 | 完整私钥内容 |

### 部署路径

| Secret 名称 | 值 |
|------------|-----|
| `FRONTEND_DEPLOY_DIR` | `/www/wwwroot/zzj.nnnnzs.cn/dist` |
| `BACKEND_DEPLOY_DIR` | `/www/wwwroot/zzj.nnnnzs.cn/backend` |

### SSH 密钥生成

```bash
# 生成 SSH 密钥对
ssh-keygen -t rsa -b 4096 -C "github-actions" -f ~/.ssh/github_actions

# 复制公钥到服务器
ssh-copy-id -i ~/.ssh/github_actions.pub user@your-server.com

# 复制私钥内容到 GitHub Secrets
cat ~/.ssh/github_actions
```

## 前端部署

### 自动部署

推送到 `main` 分支，修改 `ruoyi-ui` 目录下的文件时自动触发：

```bash
git add ruoyi-ui/
git commit -m "feat: update frontend"
git push origin main
```

### 手动部署

```bash
# 本地构建
cd ruoyi-ui
pnpm install
pnpm run build:prod

# 上传 dist 目录到服务器
scp -r dist/* user@server:/www/wwwroot/zzj.nnnnzs.cn/dist/
```

## 后端部署

### 自动部署

推送到 `main` 分支，修改后端相关文件时自动触发：

```bash
git add ruoyi-*/ pom.xml
git commit -m "feat: update backend"
git push origin main
```

### 环境变量配置

复制 `.env.example` 到服务器并配置：

```bash
# 在服务器上
cd /www/wwwroot/zzj.nnnnzs.cn/backend
cp .env.example .env
vi .env
```

**必填配置：**

```env
# 数据库
DB_URL=jdbc:mysql://your-host:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
DB_USERNAME=your-username
DB_PASSWORD=your-password

# Redis
REDIS_HOST=your-redis-host
REDIS_PORT=6379
REDIS_DATABASE=0
REDIS_PASSWORD=your-redis-password

# 其他
TOKEN_SECRET=your-secret-key
ruoyi.profile=/www/wwwroot/zzj.nnnnzs.cn/backend/uploadPath
```

### Docker 部署

```bash
# 确保服务器已安装 Docker 和 docker-compose
docker --version
docker-compose --version

# 启动服务
cd /www/wwwroot/zzj.nnnnzs.cn/backend
docker-compose up -d

# 查看日志
docker-compose logs -f

# 重启服务
docker-compose restart

# 停止服务
docker-compose down
```

## Nginx 配置

```nginx
server {
    listen 80;
    server_name zzj.nnnnzs.cn;
    root /www/wwwroot/zzj.nnnnzs.cn/dist;
    index index.html;

    # 前端静态文件
    location / {
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://127.0.0.1:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }

    # Druid 监控
    location /druid/ {
        proxy_pass http://127.0.0.1:8080/druid/;
    }

    # Swagger 文档
    location /swagger-ui/ {
        proxy_pass http://127.0.0.1:8080/swagger-ui/;
    }
}
```

## 常用命令

### 查看部署状态

在 GitHub 仓库 `Actions` 标签页查看 workflow 执行状态。

### 服务器日志

```bash
# 后端应用日志
docker logs -f ruoyi-backend

# 实时日志
docker-compose logs -f ruoyi-backend

# nginx 日志
tail -f /var/log/nginx/access.log
tail -f /var/log/nginx/error.log
```

### 备份恢复

部署脚本会自动保留最近 3 个备份：

```bash
# 前端备份
ls -la /www/wwwroot/zzj.nnnnzs.cn/dist_backup_*/

# 后端备份
ls -la /www/wwwroot/zzj.nnnnzs.cn/backend/ruoyi-admin_backup_*.jar
```

## 故障排查

### 前端无法访问

1. 检查 nginx 配置：`nginx -t`
2. 检查目录权限：`ls -la /www/wwwroot/zzj.nnnnzs.cn/dist`
3. 查看 nginx 错误日志

### 后端无法启动

1. 检查 Docker 是否运行：`docker ps`
2. 检查环境变量：`cat /www/wwwroot/zzj.nnnnzs.cn/backend/.env`
3. 查看容器日志：`docker logs ruoyi-backend`
4. 检查端口占用：`netstat -tlnp | grep 8080`

### 数据库连接失败

1. 检查数据库服务状态
2. 确认 .env 中的连接配置
3. 检查防火墙规则

### Redis 连接失败

1. 检查 Redis 服务状态
2. 确认密码配置
3. 测试连接：`redis-cli -h your-host -a your-password ping`
