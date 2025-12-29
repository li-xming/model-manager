@echo off
chcp 65001 >nul
echo ========================================
echo 数据模型管理平台 - 启动脚本
echo ========================================
echo.

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到Java环境，请先安装JDK 1.8并配置JAVA_HOME
    pause
    exit /b 1
)

echo [信息] 检测到Java环境
echo.

REM 检查Maven
where mvn >nul 2>&1
if errorlevel 1 (
    echo [警告] 未找到Maven命令
    echo [提示] 请确保Maven已安装并配置到PATH环境变量
    echo [提示] 或者使用IDE（如IntelliJ IDEA）打开项目并运行DataModelApplication类
    echo.
    pause
    exit /b 1
)

echo [信息] 检测到Maven环境
echo.

REM 编译项目
echo [步骤1] 编译项目...
call mvn clean compile
if errorlevel 1 (
    echo [错误] 编译失败，请检查代码
    pause
    exit /b 1
)

echo [成功] 编译完成
echo.

REM 启动项目
echo [步骤2] 启动项目...
echo [提示] 项目将在 http://localhost:8080/api/doc.html 启动
echo [提示] 按 Ctrl+C 停止服务
echo.
call mvn spring-boot:run

pause

