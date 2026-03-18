@echo off
REM CL Booster - Environment Configuration Utility
REM This script helps configure API keys and settings

setlocal enabledelayedexpansion

set "APP_DATA=%APPDATA%\CLBooster"
set "ENV_FILE=%APP_DATA%\.env"
set "EDITOR=notepad.exe"

echo.
echo ========================================
echo CL Booster - Configuration Manager
echo ========================================
echo.

REM Ensure directory exists
if not exist "%APP_DATA%" mkdir "%APP_DATA%"

REM If .env doesn't exist, create from template
if not exist "%ENV_FILE%" (
    echo [*] First run - creating configuration file...
    if exist "%~dp0.env.template" (
        copy "%~dp0.env.template" "%ENV_FILE%" >nul
        echo [+] Configuration file created at:
        echo     %ENV_FILE%
    ) else (
        echo [-] Template file not found. Creating default configuration...
        (
            echo # CL Booster Configuration
            echo # Add your API keys and settings here
            echo.
            echo # Google Gemini API Configuration
            echo GEMINI_API_KEY=your_api_key_here
            echo GOOGLE_PROJECT_ID=your_project_id
            echo GOOGLE_LOCATION=us-central1
            echo.
            echo # Application Settings
            echo PORT=8080
            echo SERVER_SERVLET_CONTEXT_PATH=/
            echo.
            echo # Database Configuration (Optional - for CLI mode^)
            echo DB_HOST=localhost
            echo DB_PORT=3306
            echo DB_NAME=CL_generator
            echo DB_USERNAME=root
            echo DB_PASSWORD=password
        ) > "%ENV_FILE%"
        echo [+] Default configuration created
    )
    echo.
)

echo [*] Choose configuration option:
echo     1. Open configuration file in text editor
echo     2. Show current settings
echo     3. Set GEMINI_API_KEY
echo     4. Open folder in Explorer
echo     5. Exit
echo.

set /p "CHOICE=Enter your choice (1-5): "

if "%CHOICE%"=="1" (
    echo [*] Opening configuration file in text editor...
    start "" "%EDITOR%" "%ENV_FILE%"
    echo [+] File opened. Changes will take effect after restarting the application.
) else if "%CHOICE%"=="2" (
    echo.
    echo [*] Current Configuration:
    echo ========================================
    if exist "%ENV_FILE%" (
        type "%ENV_FILE%"
    ) else (
        echo [-] Configuration file not found
    )
    echo ========================================
    echo.
) else if "%CHOICE%"=="3" (
    echo.
    set /p "API_KEY=Enter your Google Gemini API Key (press Ctrl+C to cancel): "
    if not "!API_KEY!"=="" (
        REM Create a temporary file with updated content
        setlocal enabledelayedexpansion
        (
            for /f "usebackq delims== tokens=1,*" %%A in ("%ENV_FILE%") do (
                if /i "%%A"=="GEMINI_API_KEY" (
                    echo GEMINI_API_KEY=!API_KEY!
                ) else (
                    echo %%A=%%B
                )
            )
        ) > "%ENV_FILE%.tmp"
        move /y "%ENV_FILE%.tmp" "%ENV_FILE%" >nul
        echo [+] API Key updated successfully
        echo [!] Restart the application for changes to take effect
    )
    echo.
) else if "%CHOICE%"=="4" (
    echo [*] Opening configuration folder...
    start "" explorer.exe "%APP_DATA%"
) else (
    echo [*] Exiting...
)

echo.
pause
exit /b 0
