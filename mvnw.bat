@ECHO OFF
SETLOCAL

set MAVEN_PROJECTBASEDIR=%~dp0

if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

if not "%JAVA_HOME%"=="" goto javaHomeSet

for %%i in (java.exe) do set JAVA_EXE=%%~$PATH:i

if not "%JAVA_EXE%"=="" goto init

echo.
echo Error: JAVA_HOME not found in your environment.
echo.
goto error

:javaHomeSet
set JAVA_EXE=%JAVA_HOME%\bin\java.exe

:init

echo "%JAVA_EXE%" ^
       -classpath ".mvn\wrapper\maven-wrapper.jar" ^
       "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
       org.apache.maven.wrapper.MavenWrapperMain %*

"%JAVA_EXE%" ^
  -classpath ".mvn\wrapper\maven-wrapper.jar" ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  org.apache.maven.wrapper.MavenWrapperMain %*

goto end

:error
exit /B 1

:end
ENDLOCAL