@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
set "CATALINA_HOME=%~dp0apache-tomcat-10.1.34"
call "%CATALINA_HOME%\bin\catalina.bat" stop
