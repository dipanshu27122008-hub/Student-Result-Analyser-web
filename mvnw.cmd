@echo off
setlocal
if not defined JAVA_HOME (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.12"
)
set "MAVEN_CMD=%~dp0apache-maven-3.9.9\bin\mvn.cmd"
if not exist "%MAVEN_CMD%" (
    mvn %*
) else (
    "%MAVEN_CMD%" %*
)
