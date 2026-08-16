@echo off
REM TSATMS Build and Verification Script
REM Windows batch file to verify and build the complete system

setlocal enabledelayedexpansion

echo.
echo ========================================================
echo TSATMS - BUILD & VERIFICATION SCRIPT
echo ========================================================
echo.

REM Check Java
echo [1/6] Checking Java installation...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java not found. Please install Java 8+
    exit /b 1
)
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr "version"') do set JAVA_VERSION=%%i
echo OK: Java %JAVA_VERSION% installed

REM Check Maven
echo.
echo [2/6] Checking Maven installation...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven not found. Please install Maven 3.6+
    exit /b 1
)
for /f "tokens=3" %%i in ('mvn -version 2^>^&1 ^| findstr "Apache Maven"') do set MAVEN_VERSION=%%i
echo OK: Maven %MAVEN_VERSION% installed

REM Check MySQL
echo.
echo [3/6] Checking MySQL installation...
mysql -version >nul 2>&1
if %errorlevel% neq 0 (
    echo WARNING: MySQL not found or not in PATH
    echo Please ensure MySQL is installed and in PATH
) else (
    for /f "tokens=5" %%i in ('mysql --version') do set MYSQL_VERSION=%%i
    echo OK: MySQL %MYSQL_VERSION% installed
)

REM Check project structure
echo.
echo [4/6] Verifying project structure...
if not exist "pom.xml" (
    echo ERROR: pom.xml not found in current directory
    exit /b 1
)
if not exist "database\schema.sql" (
    echo ERROR: database\schema.sql not found
    exit /b 1
)
if not exist "src\main\java\com\amdocs\telecom\main\Application.java" (
    echo ERROR: Application.java not found
    exit /b 1
)
echo OK: Project structure verified

REM Clean build
echo.
echo [5/6] Running Maven build...
echo This may take 1-2 minutes...
mvn clean compile
if %errorlevel% neq 0 (
    echo ERROR: Maven build failed
    exit /b 1
)
echo OK: Build successful

REM Summary
echo.
echo [6/6] Build Summary
echo ========================================================
echo Project Status: READY TO RUN
echo.
echo Next steps:
echo.
echo 1. Create Database:
echo    mysql -u root -p ^< database\schema.sql
echo    mysql -u root -p ^< database\seed_data.sql
echo.
echo 2. Run Application:
echo    mvn exec:java -Dexec.mainClass="com.amdocs.telecom.main.Application"
echo.
echo 3. Test Login (use one of these):
echo    Username: cust100245    / Password: password123
echo    Username: admin_sd1     / Password: password123
echo    Username: eng1008       / Password: password123
echo    Username: manager_nm1   / Password: password123
echo.
echo For CAPTCHA: Enter the displayed code
echo For OTP:     Enter the displayed number
echo.
echo ========================================================
echo.
echo Build completed successfully!
echo.
pause
