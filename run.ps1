# 我的厨房 - 自动化部署脚本

param(
    [switch]$SkipFrontend,
    [switch]$SkipBackend,
    [switch]$SkipDatabase,
    [switch]$Dev,
    [switch]$Prod
)

$ErrorActionPreference = "Stop"
$ProjectRoot = $PSScriptRoot

function Write-Step($message) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "  $message" -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
}

function Test-Service($url, $timeout = 60) {
    $start = Get-Date
    while ((New-TimeSpan -Start $start -End (Get-Date)).TotalSeconds -lt $timeout) {
        try {
            $response = Invoke-WebRequest -Uri $url -TimeoutSec 2 -UseBasicParsing -ErrorAction SilentlyContinue
            if ($response.StatusCode -eq 200) { return $true }
        } catch {}
        Start-Sleep -Seconds 2
    }
    return $false
}

function Test-Command($cmd) {
    $null = Get-Command $cmd -ErrorAction SilentlyContinue
    return $?
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  我的厨房 - 自动化部署" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""

# 获取 MySQL 配置
function Get-MySqlConfig {
    $config = @{
        Host = "localhost"
        Port = 3306
        User = "root"
        Password = ""
        Database = "my_kitchen"
    }
    
    # 从 application.yml 读取
    $ymlPath = Join-Path $ProjectRoot "backend\src\main\resources\application.yml"
    if (Test-Path $ymlPath) {
        $content = Get-Content $ymlPath -Raw
        if ($content -match "url:\s*jdbc:mysql://([^:]+):(\d+)/(\w+)") {
            $config.Host = $matches[1]
            $config.Port = [int]$matches[2]
            $config.Database = $matches[3]
        }
        if ($content -match "username:\s*(\w+)") { $config.User = $matches[1] }
        if ($content -match "password:\s*(\w+)") { $config.Password = $matches[1] }
    }
    
    return $config
}

# 初始化数据库
function Init-Database {
    $mysql = Get-Command mysql -ErrorAction SilentlyContinue
    
    if (-not $mysql) {
        Write-Host "  警告: 未找到 mysql 命令行工具" -ForegroundColor Yellow
        Write-Host "  请手动执行: mysql -u root -p < backend\src\main\resources\schema.sql" -ForegroundColor Yellow
        return $false
    }
    
    $config = Get-MySqlConfig
    $schemaFile = Join-Path $ProjectRoot "backend\src\main\resources\schema.sql"
    
    if (-not (Test-Path $schemaFile)) {
        Write-Host "  错误: 未找到 schema.sql" -ForegroundColor Red
        return $false
    }
    
    Write-Host "  正在初始化数据库..." -ForegroundColor Gray
    
    # 尝试执行 SQL 脚本
    $env:MYSQL_PWD = $config.Password
    
    $result = & mysql -h $config.Host -P $config.Port -u $config.User < $schemaFile 2>&1
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "  数据库初始化成功" -ForegroundColor Green
        return $true
    } else {
        Write-Host "  数据库初始化失败: $result" -ForegroundColor Yellow
        Write-Host "  请确保 MySQL 服务正在运行，或手动执行 schema.sql" -ForegroundColor Yellow
        return $false
    }
}

# 创建日志目录
$logsDir = Join-Path $ProjectRoot "logs"
if (-not (Test-Path $logsDir)) {
    New-Item -ItemType Directory -Path $logsDir | Out-Null
}

# 环境检查
Write-Step "环境检查"

$checks = @{
    "Java" = (Test-Command "java")
    "Maven" = (Test-Command "mvn")
    "Node.js" = (Test-Command "node")
    "MySQL" = (Test-Command "mysql")
}

foreach ($check in $checks.GetEnumerator()) {
    $color = if ($check.Value) { "Green" } else { "Yellow" }
    $status = if ($check.Value) { "OK" } else { "未找到" }
    Write-Host "  $($check.Key): $status" -ForegroundColor $color
}

if (-not $checks["Java"] -or -not $checks["Maven"]) {
    Write-Host "  错误: 需要 Java 和 Maven" -ForegroundColor Red
    exit 1
}

if (-not $checks["Node.js"]) {
    Write-Host "  错误: 需要 Node.js" -ForegroundColor Red
    exit 1
}

# 清理旧进程
Write-Step "清理旧进程"
$ports = @(8080, 5174, 5173, 5172)
foreach ($port in $ports) {
    $conn = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    if ($conn) {
        $pid = $conn.OwningProcess
        Write-Host "  停止端口 $port (PID: $pid)" -ForegroundColor Yellow
        Stop-Process -Id $pid -Force -ErrorAction SilentlyContinue
    }
}
Start-Sleep -Seconds 2

# 初始化数据库
if (-not $SkipDatabase) {
    Write-Step "初始化数据库"
    Init-Database
}

# 部署后端
if (-not $SkipBackend) {
    Write-Step "部署后端 (Spring Boot)"
    
    $backendDir = Join-Path $ProjectRoot "backend"
    
    if (-not (Test-Path $backendDir)) {
        Write-Host "  错误: 后端目录不存在" -ForegroundColor Red
        exit 1
    }
    
    # Maven 构建
    Write-Host "  Maven 构建中..." -ForegroundColor Gray
    Set-Location $backendDir
    $buildOutput = mvn clean package -DskipTests 2>&1
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "  Maven 构建失败!" -ForegroundColor Red
        Write-Host $buildOutput -ForegroundColor Red
        exit 1
    }
    Write-Host "  Maven 构建成功" -ForegroundColor Green
    
    # 启动后端
    Write-Host "  启动后端服务..." -ForegroundColor Gray
    $jarFile = Get-ChildItem -Path (Join-Path $backendDir "target") -Filter "*.jar" | 
        Where-Object { $_.Name -like "*-0.0.1-SNAPSHOT.jar" -or $_.Name -like "my-kitchen*.jar" } | 
        Select-Object -First 1
    
    if ($jarFile) {
        $backendProcess = Start-Process -FilePath "java" -ArgumentList "-jar", $jarFile.FullName -WorkingDirectory $backendDir -PassThru -WindowStyle Minimized
        Write-Host "  后端进程 PID: $($backendProcess.Id)" -ForegroundColor Green
        
        # 等待后端就绪
        Write-Host "  等待后端服务就绪..." -ForegroundColor Gray
        if (Test-Service "http://localhost:8080/api/recipes" 90) {
            Write-Host "  后端服务已就绪 (http://localhost:8080)" -ForegroundColor Green
        } else {
            Write-Host "  警告: 后端服务启动超时，请检查 MySQL 是否运行" -ForegroundColor Yellow
        }
    } else {
        Write-Host "  警告: 未找到 JAR 文件" -ForegroundColor Yellow
    }
}

# 部署前端
if (-not $SkipFrontend) {
    Write-Step "部署前端 (React + Vite)"
    
    $frontendDir = $ProjectRoot
    
    # 安装依赖
    if (-not (Test-Path (Join-Path $frontendDir "node_modules"))) {
        Write-Host "  安装前端依赖..." -ForegroundColor Gray
        Set-Location $frontendDir
        npm install --silent
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  依赖安装失败!" -ForegroundColor Red
            exit 1
        }
    }
    Write-Host "  依赖检查完成" -ForegroundColor Green
    
    if ($Prod) {
        # 生产构建
        Write-Host "  执行生产构建..." -ForegroundColor Gray
        Set-Location $frontendDir
        npm run build --silent
        if ($LASTEXITCODE -ne 0) {
            Write-Host "  前端构建失败!" -ForegroundColor Red
            exit 1
        }
        Write-Host "  前端构建成功" -ForegroundColor Green
        Write-Host "  构建产物: $frontendDir\dist" -ForegroundColor Gray
    } else {
        # 开发服务器
        Write-Host "  启动开发服务器..." -ForegroundColor Gray
        $viteProcess = Start-Process -FilePath "npm" -ArgumentList "run dev" -WorkingDirectory $frontendDir -PassThru -WindowStyle Minimized
        Write-Host "  前端进程 PID: $($viteProcess.Id)" -ForegroundColor Green
        
        # 等待前端就绪
        Write-Host "  等待前端服务就绪..." -ForegroundColor Gray
        if (Test-Service "http://localhost:5174" 60) {
            Write-Host "  前端服务已就绪 (http://localhost:5174)" -ForegroundColor Green
        } else {
            Write-Host "  警告: 前端服务启动超时" -ForegroundColor Yellow
        }
    }
}

# 完成
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  部署完成!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "  访问地址:" -ForegroundColor White
if (-not $SkipBackend) { Write-Host "    后端 API: http://localhost:8080" -ForegroundColor Cyan }
if (-not $SkipFrontend) {
    if ($Prod) {
        Write-Host "    前端构建: $frontendDir\dist" -ForegroundColor Cyan
    } else {
        Write-Host "    前端网站: http://localhost:5174" -ForegroundColor Cyan
    }
}
Write-Host ""
Write-Host "  快捷操作:" -ForegroundColor Gray
Write-Host "    重新部署: .\run.ps1" -ForegroundColor Gray
Write-Host "    停止服务: .\scripts\stop.ps1" -ForegroundColor Gray
Write-Host ""

# 自动打开浏览器
if (-not $SkipFrontend -and -not $Prod) {
    Start-Process -FilePath "http://localhost:5174"
}
