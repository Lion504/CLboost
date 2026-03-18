@echo off
REM CL Booster - Quick Start Guide
REM Run this to get started with building the installer

setlocal

echo.
echo ========================================
echo CL Booster - Installer Quick Start
echo ========================================
echo.
echo This guide will help you build an installer EXE for CL Booster
echo.
echo STEP 1: Prerequisites Required
echo   [ ] Java 21+ (download: oracle.com/java/technologies/downloads)
echo   [ ] Maven 3.6+ (download: maven.apache.org)
echo   [ ] Inno Setup 6 (download: jrsoftware.org/isdl.php)
echo.
echo STEP 2: Prepare Java Runtime
echo   [ ] Download Java 21 JRE (Windows x64 Compressed Archive)
echo   [ ] Create folder: installer\java-runtime
echo   [ ] Extract JRE contents to: installer\java-runtime
echo.
echo STEP 3: Optional - Add Icon
echo   [ ] Create or find cl-booster.ico (256x256 pixels)
echo   [ ] Place in installer\ folder
echo.
echo STEP 4: Build the Installer
echo   [ ] Open Command Prompt
echo   [ ] cd installer
echo   [ ] build-installer.bat
echo.
echo STEP 5: Test and Distribute
echo   [ ] Test CLBooster-Setup-1.0.0.exe on clean computer
echo   [ ] Distribute to users
echo.
echo ========================================
echo Quick Reference
echo ========================================
echo.
echo Run these commands:
echo.
echo   1. Check system requirements:
echo      installer\install-dependencies.bat
echo.
echo   2. Build the installer:
echo      installer\build-installer.bat
echo.
echo   3. After installation, configure settings:
echo      installer\config-env.bat
echo.
echo   4. Run application:
echo      installer\run-app.bat
echo.
echo ========================================
echo Documentation
echo ========================================
echo.
echo For detailed information, see:
echo   - INSTALLER_GUIDE.md (complete guide)
echo   - AUDIT_REPORT.md (system audit)
echo.
pause
