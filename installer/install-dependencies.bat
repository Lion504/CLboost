@echo off
REM CL Booster - Dependency Checker and Installer
REM This script checks for required dependencies and provides installation instructions

setlocal enabledelayedexpansion

echo.
echo ========================================
echo CL Booster - System Requirements Check
echo ========================================
echo.

set "JAVA_OK=0"
set "MAVEN_OK=0"
set "DOCKER_OK=0"
set "ISSUES=0"

REM Check Java 21+
echo [*] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [-] Java is NOT installed
    echo [!] Required: Java 21 or higher
    echo [!] Download from: https://www.oracle.com/java/technologies/downloads/
    set "ISSUES=1"
) else (
    for /f "tokens=*" %%i in ('java -version 2^>^&1 ^| find /v find') do (
        set "JAVA_VERSION=%%i"
        echo [+] !JAVA_VERSION!
    )
    set "JAVA_OK=1"
)

echo.

REM Check Maven (optional but recommended)
echo [*] Checking Maven installation...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [-] Maven is NOT installed
    echo [!] Recommended: Maven 3.6+ for development
    echo [!] Download from: https://maven.apache.org/download.cgi
    echo [!] Or use: choco install maven  (if using Chocolatey)
) else (
    for /f "tokens=*" %%i in ('mvn -version 2^>^&1 ^| find "Apache Maven"') do (
        echo [+] %%i
    )
    set "MAVEN_OK=1"
)

echo.

REM Check Docker (optional)
echo [*] Checking Docker installation...
docker --version >nul 2>&1
if errorlevel 1 (
    echo [-] Docker is NOT installed
    echo [!] Optional: Docker for advanced deployment
    echo [!] Download from: https://www.docker.com/products/docker-desktop
) else (
    for /f "tokens=*" %%i in ('docker --version') do (
        echo [+] %%i
    )
    set "DOCKER_OK=1"
)

echo.

REM Check MariaDB (optional)
echo [*] Checking MariaDB database...
netstat -ano 2>nul | find ":3306" >nul 2>&1
if errorlevel 1 (
    echo [-] MariaDB database is NOT running
    echo [!] Optional: Required only for CLI mode and data persistence
    echo [!] Download from: https://mariadb.org/download/
) else (
    echo [+] MariaDB database is running on port 3306
)

echo.
echo ========================================
echo Summary
echo ========================================
echo.

if %JAVA_OK% equ 1 (
    echo [+] Java: OK
) else (
    echo [-] Java: MISSING (REQUIRED)
)

if %MAVEN_OK% equ 1 (
    echo [+] Maven: OK
) else (
    echo [*] Maven: NOT INSTALLED (optional for end-users)
)

if %DOCKER_OK% equ 1 (
    echo [+] Docker: OK
) else (
    echo [*] Docker: NOT INSTALLED (optional)
)

echo.

if %ISSUES% equ 1 (
    echo [-] MISSING REQUIRED COMPONENTS
    echo [!] Please install Java 21+ before running the application
    echo.
    pause
    exit /b 1
) else (
    echo [+] All required dependencies are installed!
    echo [*] You can now launch CL Booster
    echo.
    pause
    exit /b 0
)
