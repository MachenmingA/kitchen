#!/bin/bash
# ============================================
# 我的厨房 - 一键启动脚本
# ============================================

echo "========================================"
echo "  我的厨房 - 企业级应用启动器"
echo "========================================"
echo ""

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
BACKEND_DIR="$SCRIPT_DIR/backend"
FRONTEND_DIR="$SCRIPT_DIR"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 检查 Java
check_java() {
    if ! command -v java &> /dev/null; then
        echo -e "${RED}✗ Java 未安装，请先安装 JDK 17+${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Java 已安装${NC}"
}

# 检查 Node.js
check_node() {
    if ! command -v node &> /dev/null; then
        echo -e "${RED}✗ Node.js 未安装，请先安装 Node.js 18+${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Node.js 已安装${NC}"
}

# 检查 Maven
check_maven() {
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}✗ Maven 未安装，请先安装 Maven${NC}"
        exit 1
    fi
    echo -e "${GREEN}✓ Maven 已安装${NC}"
}

# 检查 MySQL
check_mysql() {
    if ! command -v mysql &> /dev/null; then
        echo -e "${YELLOW}⚠ MySQL CLI 未安装，请确保 MySQL 服务正在运行${NC}"
    else
        echo -e "${GREEN}✓ MySQL CLI 已安装${NC}"
    fi
}

# 检查 Redis
check_redis() {
    if ! command -v redis-cli &> /dev/null; then
        echo -e "${YELLOW}⚠ Redis CLI 未安装，请确保 Redis 服务正在运行${NC}"
    else
        echo -e "${GREEN}✓ Redis CLI 已安装${NC}"
    fi
}

# 初始化数据库
init_database() {
    echo ""
    echo -e "${YELLOW}正在检查数据库...${NC}"

    SCHEMA_FILE="$BACKEND_DIR/src/main/resources/schema.sql"

    if [ -f "$SCHEMA_FILE" ]; then
        read -p "是否初始化数据库？(y/n): " -n 1 -r
        echo ""
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            echo -e "${YELLOW}请输入 MySQL root 密码:${NC}"
            read -s MYSQL_PASSWORD
            mysql -u root -p"$MYSQL_PASSWORD" < "$SCHEMA_FILE" 2>/dev/null
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✓ 数据库初始化成功${NC}"
            else
                echo -e "${YELLOW}⚠ 数据库初始化失败，请手动执行: mysql -u root -p < $SCHEMA_FILE${NC}"
            fi
        fi
    fi
}

# 启动后端
start_backend() {
    echo ""
    echo -e "${YELLOW}正在启动后端服务...${NC}"
    cd "$BACKEND_DIR"

    # 检查是否有 Maven Wrapper
    if [ -f "$BACKEND_DIR/mvnw" ]; then
        chmod +x "$BACKEND_DIR/mvnw"
        "$BACKEND_DIR/mvnw" spring-boot:run &
    else
        mvn spring-boot:run &
    fi

    BACKEND_PID=$!
    echo -e "${GREEN}✓ 后端服务启动中 (PID: $BACKEND_PID)${NC}"
    echo -e "${YELLOW}等待后端启动...${NC}"

    # 等待后端启动
    for i in {1..30}; do
        if curl -s http://localhost:8080/api/recipes > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 后端服务已就绪 (http://localhost:8080)${NC}"
            return 0
        fi
        sleep 2
    done

    echo -e "${YELLOW}⚠ 后端启动中，请访问 http://localhost:8080/swagger-ui.html 查看 API${NC}"
    return 1
}

# 启动前端
start_frontend() {
    echo ""
    echo -e "${YELLOW}正在启动前端服务...${NC}"
    cd "$FRONTEND_DIR"

    # 检查依赖
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}正在安装前端依赖...${NC}"
        npm install
    fi

    npm run dev &
    FRONTEND_PID=$!
    echo -e "${GREEN}✓ 前端服务启动中 (PID: $FRONTEND_PID)${NC}"
    echo -e "${YELLOW}等待前端启动...${NC}"

    # 等待前端启动
    for i in {1..30}; do
        if curl -s http://localhost:5173 > /dev/null 2>&1; then
            echo -e "${GREEN}✓ 前端服务已就绪 (http://localhost:5173)${NC}"
            return 0
        fi
        sleep 2
    done

    echo -e "${YELLOW}⚠ 前端启动中，请访问 http://localhost:5173${NC}"
    return 1
}

# 主函数
main() {
    echo ""
    echo "【环境检查】"
    check_java
    check_node
    check_maven
    check_mysql
    check_redis

    init_database

    echo ""
    echo "【启动服务】"
    start_backend
    start_frontend

    echo ""
    echo "========================================"
    echo -e "${GREEN}  我的厨房已启动！${NC}"
    echo "========================================"
    echo ""
    echo "访问地址:"
    echo "  前端: http://localhost:5173"
    echo "  后端: http://localhost:8080"
    echo "  API文档: http://localhost:8080/swagger-ui.html"
    echo ""
    echo "按 Ctrl+C 停止所有服务"
    echo "========================================"

    # 等待用户中断
    wait
}

# 捕获中断信号
trap 'echo ""; echo "正在停止服务..."; kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit 0' INT TERM

# 运行主函数
main
