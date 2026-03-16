@echo off
REM CL Booster - Database Setup Script
REM This script sets up MariaDB database for CLI mode and data persistence

setlocal enabledelayedexpansion

echo.
echo ========================================
echo CL Booster - Database Setup
echo ========================================
echo.

set "APP_DIR=%~dp0"
set "SQL_SCRIPT=%APP_DIR%database\coverletter_generator_script.sql"
set "DB_HOST=localhost"
set "DB_PORT=3306"
set "DB_USER=root"
set "DB_NAME=CL_generator"

REM Check if MySQL/MariaDB is installed
echo [*] Checking for MySQL/MariaDB client...
mysql --version >nul 2>&1
if errorlevel 1 (
    echo [-] ERROR: MySQL/MariaDB client is not installed or not in PATH
    echo [!] Please install MariaDB from: https://mariadb.org/download/
    echo.
    echo [*] Or use Docker instead:
    echo     docker-compose up -d db
    echo.
    pause
    exit /b 1
)
echo [+] MySQL/MariaDB client found
echo.

REM Check if SQL script exists
if not exist "%SQL_SCRIPT%" (
    echo [-] ERROR: SQL script not found at %SQL_SCRIPT%
    echo [*] This script should be included with the installer
    pause
    exit /b 1
)
echo [+] SQL script found
echo.

REM Verify database connectivity
echo [*] Checking database connectivity...
echo [!] Default username: root
echo [!] This script will prompt for password
echo.

REM Execute SQL script
echo [*] Setting up database tables...
mysql -h %DB_HOST% -P %DB_PORT% -u %DB_USER% -p < "%SQL_SCRIPT%"

if errorlevel 1 (
    echo [-] Database setup failed!
    echo [!] Make sure MariaDB is running and you provided the correct password
    echo [!] To start MariaDB as a service:
    echo     net start MySQL80
    echo     or
    echo     net start MariaDB
    echo.
    pause
    exit /b 1
)

echo.
echo [+] Database setup completed successfully!
echo [+] Database '%DB_NAME%' is now ready for use
echo.
echo [*] You can now:
echo     1. Use CLI mode with the database
echo     2. Connect the web application to the database (configuration in setup^)
echo.
pause
exit /b 0
