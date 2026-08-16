@echo off
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-8.0.502.7-hotspot"
set "PATH=%JAVA_HOME%\bin;C:\maven\bin;%PATH%"

echo ========================================================
echo Starting Telecom Service Assurance System (TSATMS)...
echo ========================================================

java -jar target\telecom-tsatms-1.0.0-jar-with-dependencies.jar
pause
