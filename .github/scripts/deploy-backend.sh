#!/bin/bash

set -e

# 配置变量
DEPLOY_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="ruoyi-admin"
JAR_FILE="${DEPLOY_DIR}/${APP_NAME}.jar"
COMPOSE_FILE="${DEPLOY_DIR}/docker-compose.yml"
CONTAINER_NAME="ruoyi-backend"

# 颜色输出
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查必要文件
if [ ! -f "$JAR_FILE" ]; then
    log_error "JAR file not found: $JAR_FILE"
    exit 1
fi

if [ ! -f "$COMPOSE_FILE" ]; then
    log_error "docker-compose.yml not found: $COMPOSE_FILE"
    exit 1
fi

if [ ! -f "${DEPLOY_DIR}/.env" ]; then
    log_warn ".env file not found. Please create it from .env.example"
fi

log_info "Starting deployment for ${APP_NAME}..."

# 使用 docker-compose 重启服务
log_info "Stopping existing containers..."
docker-compose -f "$COMPOSE_FILE" down

log_info "Starting containers..."
docker-compose -f "$COMPOSE_FILE" up -d

# 等待容器启动
log_info "Waiting for container to start..."
sleep 10

# 检查容器状态
if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
    log_info "Container ${CONTAINER_NAME} is running!"
    log_info "Container logs (last 20 lines):"
    docker logs --tail 20 "${CONTAINER_NAME}"
else
    log_error "Container failed to start!"
    log_error "Container logs:"
    docker logs "${CONTAINER_NAME}"
    exit 1
fi

# 健康检查
log_info "Performing health check..."
MAX_ATTEMPTS=30
ATTEMPT=0

while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if docker exec "${CONTAINER_NAME}" wget --no-verbose --tries=1 --spider "http://localhost:8080/" 2>/dev/null; then
        log_info "Health check passed! Application is ready."
        log_info "Deployment completed successfully!"
        exit 0
    fi

    ATTEMPT=$((ATTEMPT + 1))
    log_warn "Health check attempt $ATTEMPT/$MAX_ATTEMPTS failed. Retrying in 5 seconds..."
    sleep 5
done

log_error "Health check failed after $MAX_ATTEMPTS attempts!"
log_error "Container logs:"
docker logs --tail 50 "${CONTAINER_NAME}"
exit 1
