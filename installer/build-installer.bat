@echo off
REM CL Booster - Installer Builder
REM This script builds the Maven project and prepares the Inno Setup installer

setlocal enabledelayedexpansion

echo.
echo ========================================
echo CL Booster - Installer Builder
echo ========================================
echo.

set "PROJECT_DIR=%~dp0.."
set "INSTALLER_DIR=%~dp0"
set "TARGET_DIR=%PROJECT_DIR%\target"

echo [*] Project Directory: %PROJECT_DIR%
echo [*] Installer Directory: %INSTALLER_DIR%
echo.

REM Check for Maven
echo [*] Checking Maven installation...
mvn -version >nul 2>&1
if errorlevel 1 (
    echo [-] ERROR: Maven is not installed or not in PATH
    echo [!] Please install Maven from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)
echo [+] Maven found
echo.

REM Check for Java
echo [*] Checking Java installation...
java -version >nul 2>&1
if errorlevel 1 (
    echo [-] ERROR: Java is not installed or not in PATH
    echo [!] Please install Java 21+ from https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)
echo [+] Java found
echo.

REM Clean and build the Maven project
echo [*] Building CLBooster project with Maven...
echo [*] This may take several minutes on first run...
echo.
cd /d "%PROJECT_DIR%"
call mvn clean package -DskipTests

if errorlevel 1 (
    echo [-] Build failed! Check Maven output for errors.
    pause
    exit /b 1
)
echo.
echo [+] Maven build successful!
echo.

REM Check if JAR was created
if not exist "%TARGET_DIR%\cl-booster-1.0-SNAPSHOT.jar" (
    echo [-] ERROR: JAR file was not created at expected location
    echo [!] Expected: %TARGET_DIR%\cl-booster-1.0-SNAPSHOT.jar
    pause
    exit /b 1
)
echo [+] JAR file created: cl-booster-1.0-SNAPSHOT.jar
echo.

REM Check for Inno Setup
echo [*] Checking for Inno Setup...
if exist "C:\Program Files (x86)\Inno Setup 6\iscc.exe" (
    set "ISCC_PATH=C:\Program Files (x86)\Inno Setup 6\iscc.exe"
) else if exist "C:\Program Files\Inno Setup 6\iscc.exe" (
    set "ISCC_PATH=C:\Program Files\Inno Setup 6\iscc.exe"
) else (
    echo [-] WARNING: Inno Setup not found in default locations
    echo [*] You can compile the ISS file manually:
    echo     1. Download Inno Setup from: https://jrsoftware.org/isdl.php
    echo     2. Open the .iss file in Inno Setup IDE
    echo     3. Click Build -^> Compile
    echo.
    echo [*] Alternatively, to find Inno Setup:
    echo     where iscc.exe
    echo.
    pause
    exit /b 1
)
echo [+] Inno Setup found at: !ISCC_PATH!
echo.

REM Verify required files exist for installer
echo [*] Verifying installer files...
if not exist "%INSTALLER_DIR%\setup.iss" (
    echo [-] ERROR: setup.iss not found
    exit /b 1
)
echo [+] setup.iss found
if not exist "%INSTALLER_DIR%\run-app.bat" (
    echo [-] ERROR: run-app.bat not found
    exit /b 1
)
echo [+] run-app.bat found
if not exist "%INSTALLER_DIR%\install-dependencies.bat" (
    echo [-] ERROR: install-dependencies.bat not found
    exit /b 1
)
echo [+] install-dependencies.bat found
if not exist "%INSTALLER_DIR%\config-env.bat" (
    echo [-] ERROR: config-env.bat not found
    exit /b 1
)
echo [+] config-env.bat found

echo.
echo [*] IMPORTANT: Before compiling the ISS file:
echo     1. Obtain a Java 21 JRE (runtime, not full JDK^)
echo     2. Extract it to: %INSTALLER_DIR%\java-runtime
echo        (Create this folder if it doesn't exist^)
echo     3. Visit: https://www.oracle.com/java/technologies/javase-jre21-downloads.html
echo     4. Download "Windows x64 Compressed Archive"
echo.
echo     To convert JRE to portable:
echo.
echo     cd /d %INSTALLER_DIR%\java-runtime
echo     Add the extracted JRE contents here
echo     Structure should be: java-runtime\bin\, java-runtime\lib\, etc.
echo.

REM Compile the ISS file
echo [*] Compiling Inno Setup installer...
echo [*] Source: %INSTALLER_DIR%\setup.iss
echo.

cd /d "%INSTALLER_DIR%"
"!ISCC_PATH!" setup.iss

if errorlevel 1 (
    echo [-] Inno Setup compilation failed!
    pause
    exit /b 1
)

echo.
echo [+] Installer compiled successfully!
echo [*] Look for: CLBooster-Setup-1.0.0.exe in %INSTALLER_DIR%
echo.
echo [*] Next steps:
echo     1. Test the installer on your system
echo     2. Distribute CLBooster-Setup-1.0.0.exe to users
echo     3. Users can run it to install the application
echo.
pause
exit /b 0
