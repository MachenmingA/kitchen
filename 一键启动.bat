@echo off
chcp 65001 > nul
setlocal EnableDelayedExpansion

echo.
echo  #########################################################################
echo  #                                                                       #
echo  #                     我的厨房 - 一键部署启动                           #
echo  #                                                                       #
echo  #########################################################################
echo.

:: 获取脚本所在目录
set "PROJECT_DIR=%~dp0"
set "PROJECT_DIR=%PROJECT_DIR:~0,-1%"

:: 颜色定义
set "GREEN=0A"
set "YELLOW=0E"
set "RED=0C"
set "CYAN=0B"

:: ========================================
:: 1. 检测 Java
:: ========================================
call :print "检查 Java..." "CYAN"
where java > nul 2>&1
if %errorlevel% neq 0 (
    call :error "未安装 Java"
    echo   请先安装 JDK 17+: https://adoptium.net/
    pause
    exit /b 1
)
call :success "Java 已就绪"

:: ========================================
:: 2. 检测 Node.js
:: ========================================
call :print "检查 Node.js..." "CYAN"
where node > nul 2>&1
if %errorlevel% neq 0 (
    call :error "未安装 Node.js"
    echo   请先安装: https://nodejs.org/
    pause
    exit /b 1
)
for /f "delims=" %%v in ('node -v') do set "NODE_VERSION=%%v"
call :success "Node.js !NODE_VERSION! 已就绪"

:: ========================================
:: 3. 检测 MySQL
:: ========================================
call :print "检查 MySQL..." "CYAN"

:: 检查 MySQL 是否安装
set "MYSQL_FOUND=0"
sc query MySQL > nul 2>&1 && set "MYSQL_FOUND=1"
sc query MySQL80 > nul 2>&1 && set "MYSQL_FOUND=1"
sc query MySQL57 > nul 2>&1 && set "MYSQL_FOUND=1"

:: 检查 MySQL 是否运行
net start | findstr /i "mysql" > nul 2>&1
if %errorlevel% neq 0 (
    call :print "启动 MySQL 服务..." "YELLOW"
    net start MySQL > nul 2>&1
    net start MySQL80 > nul 2>&1
    net start MySQL57 > nul 2>&1
    timeout /t 3 /nobreak > nul
)

:: 测试连接
set "MYSQL_OK=0"
mysql -u root -e "SELECT 1" > nul 2>&1 && set "MYSQL_OK=1"
if !MYSQL_OK! equ 0 (
    mysql -u root -p"" -e "SELECT 1" > nul 2>&1 && set "MYSQL_OK=1"
)
if !MYSQL_OK! equ 0 (
    mysql -u root -proot -e "SELECT 1" > nul 2>&1 && set "MYSQL_OK=1"
)

if !MYSQL_OK! equ 1 (
    call :success "MySQL 已连接"
) else (
    call :error "MySQL 未运行或无法连接"
    echo   请手动启动 MySQL 服务，然后重新运行此脚本
    pause
    exit /b 1
)

:: ========================================
:: 4. 安装前端依赖
:: ========================================
call :print "检查前端依赖..." "CYAN"
if not exist "%PROJECT_DIR%\node_modules" (
    call :print "安装前端依赖 (首次运行需要几分钟)..." "YELLOW"
    cd /d "%PROJECT_DIR%"
    call npm install --silent
    if !errorlevel! neq 0 (
        call :error "依赖安装失败"
        pause
        exit /b 1
    )
)
call :success "前端依赖已就绪"

:: ========================================
:: 5. 初始化数据库
:: ========================================
call :print "初始化数据库..." "CYAN"

:: 尝试多种密码
set "DB_INIT=0"

mysql -u root -e "CREATE DATABASE IF NOT EXISTS my_kitchen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
if !errorlevel! equ 0 set "DB_INIT=1"

if !DB_INIT! equ 0 (
    mysql -u root -p"" -e "CREATE DATABASE IF NOT EXISTS my_kitchen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
    if !errorlevel! equ 0 set "DB_INIT=1"
)

if !DB_INIT! equ 0 (
    mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS my_kitchen CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>nul
    if !errorlevel! equ 0 set "DB_INIT=1"
)

if !DB_INIT! equ 1 (
    call :success "数据库创建成功"
) else (
    call :error "数据库创建失败，请检查 MySQL 配置"
)

:: 执行建表脚本
call :print "创建数据表..." "CYAN"
for %%p in ("" "-p" "-proot") do (
    if !DB_INIT! equ 0 (
        mysql -u root %%p my_kitchen < "%PROJECT_DIR%\backend\src\main\resources\schema.sql" 2>nul
        if !errorlevel! equ 0 set "DB_INIT=1"
    )
)

if !DB_INIT! equ 1 (
    call :success "数据表创建成功"
) else (
    call :print "建表可能失败，请手动执行: mysql -u root -p < backend\src\main\resources\schema.sql" "YELLOW"
)

:: ========================================
:: 6. 停止旧服务
:: ========================================
call :print "停止旧服务..." "CYAN"
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /F /PID %%a > nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5174') do taskkill /F /PID %%a > nul 2>&1
timeout /t 2 /nobreak > nul
call :success "旧服务已停止"

:: ========================================
:: 7. 启动后端
:: ========================================
call :print "启动后端 (端口 8080)..." "CYAN"
cd /d "%PROJECT_DIR%\backend"
start "MyKitchen-后端" /min cmd /c "title MyKitchen-后端 && mvn spring-boot:run"

:: 等待后端启动
call :print "等待后端启动..." "CYAN"
set "BACKEND_OK=0"
for /L %%i in (1,1,45) do (
    if !BACKEND_OK! equ 0 (
        timeout /t 2 /nobreak > nul
        curl -s http://localhost:8080/api/recipes > nul 2>&1
        if !errorlevel! equ 0 (
            set "BACKEND_OK=1"
        )
    )
)

if !BACKEND_OK! equ 1 (
    call :success "后端已就绪 (http://localhost:8080)"
) else (
    call :print "后端启动中，请稍候..." "YELLOW"
)

:: ========================================
:: 8. 启动前端
:: ========================================
call :print "启动前端 (端口 5174)..." "CYAN"
cd /d "%PROJECT_DIR%"
start "MyKitchen-前端" /min cmd /c "title MyKitchen-前端 && npm run dev"

:: 等待前端启动
call :print "等待前端启动..." "CYAN"
set "FRONTEND_OK=0"
for /L %%i in (1,1,30) do (
    if !FRONTEND_OK! equ 0 (
        timeout /t 2 /nobreak > nul
        curl -s http://localhost:5174 > nul 2>&1
        if !errorlevel! equ 0 (
            set "FRONTEND_OK=1"
        )
    )
)

if !FRONTEND_OK! equ 1 (
    call :success "前端已就绪"
) else (
    call :print "前端启动中，请稍候..." "YELLOW"
)

:: ========================================
:: 9. 打开浏览器
:: ========================================
start http://localhost:5174

:: ========================================
:: 完成
:: ========================================
echo.
echo  #########################################################################
echo  #                                                                       #
echo  #                      所有服务已成功启动!                              #
echo  #                                                                       #
echo  #########################################################################
echo.
echo   前端网站:  http://localhost:5174
echo   后端 API:   http://localhost:8080
echo.
echo   提示: 此窗口可以最小化，不要关闭
echo   停止服务: 双击运行 停止服务.bat
echo.
echo  #########################################################################
echo.

pause
exit /b 0

:: ========================================
:: 子函数
:: ========================================
:print
echo   [%~1]
exit /b 0

:success
<nul set /p "=   ["
<nul set /p "=%~1"
<nul set /p "=] "
echo OK
exit /b 0

:error
<nul set /p "=   ["
<nul set /p "=%~1"
<nul set /p "=] "
echo 失败
exit /b 0
