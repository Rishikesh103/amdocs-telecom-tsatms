@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.502.7-hotspot"
set "PATH=%JAVA_HOME%\bin;C:\maven\bin;%PATH%"

cd /d "%~dp0"

if not exist "target\telecom-tsatms-1.0.0-jar-with-dependencies.jar" (
    echo Building TSATMS Application Package...
    call mvn clean package -DskipTests
)

:loop
cls
echo ================================================================================
echo   Amdocs TSATMS - Telecom Service Assurance System
echo   Launching Application...
echo ================================================================================
echo.

java -jar target\telecom-tsatms-1.0.0-jar-with-dependencies.jar

echo.
echo ================================================================================
echo   Application Stopped.
echo   [1] Restart Application
echo   [2] Exit to Terminal
echo ================================================================================
set /p postChoice="Enter choice (1 or 2, default 1): "
if "%postChoice%"=="2" goto end
goto loop

:end
