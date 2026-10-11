@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "CATALINA_HOME=%~dp0apache-tomcat-10.1.34"

if exist "%~dp0target\student-result-analysis.war" (
    copy /Y "%~dp0target\student-result-analysis.war" "%CATALINA_HOME%\webapps\ROOT.war" >nul 2>&1
    copy /Y "%~dp0target\student-result-analysis.war" "%CATALINA_HOME%\webapps\student-result-analysis.war" >nul 2>&1
)

echo ========================================================
echo Starting Student Result Analysis System on Apache Tomcat...
echo Access at: http://localhost:8085/
echo Login: admin / admin123
echo ========================================================
call "%CATALINA_HOME%\bin\catalina.bat" run
