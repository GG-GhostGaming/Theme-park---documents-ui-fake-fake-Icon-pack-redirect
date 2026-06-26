@rem Generated Gradle wrapper batch file for Windows
@echo off
set DIR=%~dp0
if exist "%DIR%\gradlew.bat" if not "%~0" == "%DIR%\gradlew.bat" (
  "%DIR%\gradlew.bat" %*
  goto :EOF
)
if exist "%DIR%\gradle\wrapper\gradle-wrapper.jar" (
  java -jar "%DIR%\gradle\wrapper\gradle-wrapper.jar" %*
) else (
  gradle %*
)
