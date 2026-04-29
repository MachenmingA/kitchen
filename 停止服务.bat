@echo off
chcp 65001 > nul
echo.
echo  ########################################
echo  #                                      #
echo  #       我的厨房 - 停止所有服务        #
echo  #                                      #
echo  ########################################
echo.

echo 正在停止服务...

:: 停止后端 (Java)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do (
    echo   停止后端 (PID: %%a)...
    taskkill /F /PID %%a > nul 2>&1
)

:: 停止前端 (Node)
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :5174') do (
    echo   停止前端 (PID: %%a)...
    taskkill /F /PID %%a > nul 2>&1
)

:: 停止所有 Node 进程 (谨慎)
:: taskkill /F /IM node.exe > nul 2>&1

echo.
echo  所有服务已停止
echo.
pause
