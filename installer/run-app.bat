@echo off
REM CL Booster - Main Application Launcher
REM This batch file launches the CL Booster application with proper configuration

setlocal enabledelayedexpansion

echo.
echo ========================================
echo CL Booster - Application Launcher
echo ========================================
echo.

REM Get installation directory
set "APP_DIR=%~dp0"
set "APP_DATA=%APPDATA%\CLBooster"
set "ENV_FILE=%APP_DATA%\.env"
set "JAR_FILE=%APP_DIR%cl-booster-1.0-SNAPSHOT.jar"
set "JAVA_HOME=%APP_DIR%java"

REM Create necessary directories
if not exist "%APP_DATA%" mkdir "%APP_DATA%"
if not exist "%APP_DATA%\uploads" mkdir "%APP_DATA%\uploads"
if not exist "%APP_DATA%\uploads\resumes" mkdir "%APP_DATA%\uploads\resumes"
if not exist "%APP_DATA%\uploads\coverletters" mkdir "%APP_DATA%\uploads\coverletters"

REM Check if .env file exists, if not create from template
if not exist "%ENV_FILE%" (
    echo.
    echo [*] Creating .env file from template...
    if exist "%APP_DIR%.env.template" (
        copy "%APP_DIR%.env.template" "%ENV_FILE%" >nul
        echo [+] .env file created at %ENV_FILE%
        echo [!] Please edit the .env file to add your Google Gemini API key
        echo [!] Open the file in your favorite editor to configure
        echo.
        timeout /t 3
    ) else (
        echo [-] .env.template not found!
    )
)

REM Check if JAR exists
if not exist "%JAR_FILE%" (
    echo [-] ERROR: JAR file not found at %JAR_FILE%
    echo [*] Please ensure the installation is complete
    pause
    exit /b 1
)

REM Verify Java is available
echo [*] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [-] ERROR: Java is not installed or not in PATH
    echo [*] Please install Java 21+ from https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
) else (
    for /f "tokens=*" %%i in ('java -version 2^>^&1') do (
        echo [+] %%i
        goto :java_found
    )
    :java_found
)

REM Load environment variables from .env file
echo.
echo [*] Loading environment configuration...
for /f "usebackq delims== tokens=1,2" %%A in ("%ENV_FILE%") do (
    if not "%%A"=="" (
        if not "%%A:~0,1%"=="#" (
            set "%%A=%%B"
        )
    )
)

REM Verify required environment variables
if not defined GEMINI_API_KEY (
    echo [-] WARNING: GEMINI_API_KEY not set in .env file
    echo [*] AI features will not work without this configuration
    echo [!] Edit %ENV_FILE% to continue, or press any key to try anyway...
    pause
)

REM Verify database is running if needed
REM You can uncomment this if your app is configured for database mode
REM echo [*] Checking database connection...
REM netstat -ano | find ":3306" >nul 2>&1
REM if errorlevel 1 (
REM     echo [-] WARNING: MariaDB database not found on port 3306
REM     echo [*] Some features may not work without database
REM )

echo.
echo [+] Starting CL Booster application...
echo [*] Application will open at http://localhost:8080
echo [*] Press Ctrl+C in this window to stop the application
echo [*] Check your browser if it doesn't open automatically
echo.

REM Run the application
cd /d "%APP_DATA%"
java -jar "%JAR_FILE%"

exit /b %errorlevel%
