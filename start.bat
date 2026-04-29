@echo off
chcp 65001 > nul
REM ============================================
REM 我的厨房 - Windows 一键启动脚本
REM ============================================

echo.
echo ========================================
echo   我的厨房 - 企业级应用启动器
echo ========================================
echo.

set SCRIPT_DIR=%~dp0
set BACKEND_DIR=%SCRIPT_DIR%backend

REM 检查 Java
java -version > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java 未安装，请先安装 JDK 17+
    pause
    exit /b 1
)
echo [OK] Java 已安装

REM 检查 Node.js
node --version > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js 未安装，请先安装 Node.js 18+
    pause
    exit /b 1
)
echo [OK] Node.js 已安装

REM 检查 Maven
mvn --version > nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven 未安装，请先安装 Maven
    pause
    exit /b 1
)
echo [OK] Maven 已安装

REM 检查 MySQL
net start | findstr "MySQL" > nul
if errorlevel 1 (
    echo [WARN] MySQL 服务可能未运行
) else (
    echo [OK] MySQL 服务运行中
)

REM 检查 Redis
net start | findstr "Redis" > nul
if errorlevel 1 (
    echo [WARN] Redis 服务可能未运行
) else (
    echo [OK] Redis 服务运行中
)

echo.
echo ========================================
echo [1] 启动后端服务
echo [2] 启动前端服务
echo [3] 同时启动前后端
echo [4] 初始化数据库
echo [5] 查看帮助
echo ========================================
echo.

set /p choice=请选择 (1-5):

if "%choice%"=="1" goto start_backend
if "%choice%"=="2" goto start_frontend
if "%choice%"=="3" goto start_all
if "%choice%"=="4" goto init_db
if "%choice%"=="5" goto show_help

:start_backend
echo.
echo [INFO] 正在启动后端服务...
cd /d "%BACKEND_DIR%"
start "MyKitchen-Backend" cmd /k "mvn spring-boot:run"
echo [OK] 后端服务启动中，请访问 http://localhost:8080
echo [OK] API文档: http://localhost:8080/swagger-ui.html
goto end

:start_frontend
echo.
echo [INFO] 正在启动前端服务...
cd /d "%SCRIPT_DIR%"
if not exist "node_modules" (
    echo [INFO] 正在安装依赖...
    call npm install
)
start "MyKitchen-Frontend" cmd /k "npm run dev"
echo [OK] 前端服务启动中，请访问 http://localhost:5173
goto end

:start_all
echo.
echo [INFO] 正在启动所有服务...
cd /d "%BACKEND_DIR%"
start "MyKitchen-Backend" cmd /k "mvn spring-boot:run"
echo [OK] 后端服务启动中...

timeout /t 10 /nobreak > nul

cd /d "%SCRIPT_DIR%"
if not exist "node_modules" (
    echo [INFO] 正在安装依赖...
    call npm install
)
start "MyKitchen-Frontend" cmd /k "npm run dev"
echo [OK] 前端服务启动中...
goto show_urls

:init_db
echo.
echo [INFO] 请在 MySQL 中执行以下命令初始化数据库:
echo.
echo   mysql -u root -p ^< "%BACKEND_DIR%\src\main\resources\schema.sql"
echo.
set /p confirm=按 Enter 键返回菜单...
goto start_menu

:show_help
echo.
echo ========================================
echo   使用说明
echo ========================================
echo.
echo   1. 首次使用请先确保已安装:
echo      - JDK 17+
echo      - Node.js 18+
echo      - Maven 3.8+
echo      - MySQL 8.0+
echo      - Redis (可选，用于缓存)
echo.
echo   2. 初始化数据库:
echo      mysql -u root -p ^< schema.sql
echo      (在 backend 目录下执行)
echo.
echo   3. 启动服务后访问:
echo      - 前端: http://localhost:5173
echo      - 后端: http://localhost:8080
echo      - API文档: http://localhost:8080/swagger-ui.html
echo.
echo   4. 默认测试账号:
echo      - 用户名: chef_wang
echo      - 密码: 123456
echo.
echo ========================================
echo.
set /p confirm=按 Enter 键返回菜单...

:show_urls
echo.
echo ========================================
echo   我的厨房已启动！
echo ========================================
echo.
echo   访问地址:
echo   - 前端: http://localhost:5173
echo   - 后端: http://localhost:8080
echo   - API文档: http://localhost:8080/swagger-ui.html
echo.
echo   默认测试账号:
echo   - 用户名: chef_wang
echo   - 密码: 123456
echo.
echo ========================================
echo   关闭窗口可停止服务
echo ========================================
echo.

:end
pause
