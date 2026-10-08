@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "CATALINA_HOME=%~dp0apache-tomcat-10.1.34"
echo ========================================================
echo Starting Student Result Analysis System on Apache Tomcat...
echo Access at: http://localhost:8085/ or http://localhost:8085/student-result-analysis/
echo Login: admin / admin123
echo ========================================================
call "%CATALINA_HOME%\bin\catalina.bat" run
