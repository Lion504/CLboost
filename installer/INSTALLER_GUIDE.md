# CL Booster - Complete Installer Guide

## Overview

This guide explains how to build a complete, single-file installer (`.exe`) for CL Booster that includes:
- Java Runtime Environment (bundled)
- Application JAR
- Configuration utilities
- Database setup tools
- Multi-language support (English, German, French, Finnish, Swedish)

## Prerequisites

### Required Tools (for building the installer)

1. **Java 21+** - For building the application
   - Download: https://www.oracle.com/java/technologies/downloads/
   - Verify: `java -version`

2. **Maven 3.6+** - For building the JAR
   - Download: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **Inno Setup 6** - For creating the Windows installer
   - Download: https://jrsoftware.org/isdl.php
   - Installation required for `iscc.exe`

4. **Git** (optional, for version control)
   - Download: https://git-scm.com/download/win

### System Requirements (for end-users)

- **Windows** - XP or later (tested on Windows 10/11)
- **Java 21+** - Included in installer (175 MB) or pre-installed
- **Internet connection** - For Google Gemini API

## Build Process

### Step 1: Prepare Java Runtime Environment

The installer includes a portable Java runtime. Follow these steps:

1. Download Java 21 JRE (not full JDK):
   ```
   https://www.oracle.com/java/technologies/javase-jre21-downloads.html
   ```
   - Choose: **"Windows x64 Compressed Archive"** (~175 MB)

2. Create the runtime directory:
   ```cmd
   cd installer
   mkdir java-runtime
   ```

3. Extract the downloaded JRE:
   - Right-click the ZIP, select Extract
   - Copy the extracted contents to: `installer/java-runtime`
   - Result: `installer/java-runtime/bin/`, `installer/java-runtime/lib/`, etc.

### Step 2: Prepare Application Icon (Optional)

Add a custom icon for your installer:

1. Create or obtain a 256x256 PNG image
2. Convert to `.ico` format (use online converter: convertio.co, icoconvert.com)
3. Place in `installer/` directory as: `cl-booster.ico`
4. Inno Setup will automatically use it for shortcuts

### Step 3: Build the Installer

Use the automated build script:

```cmd
cd installer
build-installer.bat
```

**What this script does:**
1. ✓ Verifies Java and Maven are installed
2. ✓ Runs `mvn clean package -DskipTests`
3. ✓ Locates Inno Setup compiler
4. ✓ Compiles `setup.iss` into `CLBooster-Setup-1.0.0.exe`

**Build time:** ~5-10 minutes (first run longer due to Maven dependency downloads)

### Step 4: Output

Upon successful build, you'll find:
```
installer/CLBooster-Setup-1.0.0.exe
```

This is your complete installer! Ready to distribute.

## Installer Features

### Automatic Installation

The `setup.iss` file configures:

1. **Installation Wizard**
   - Modern UI with multi-language support
   - Optional components (database, Docker)
   - Custom installation paths

2. **Automatic Configuration**
   - Creates user data directory: `%APPDATA%\CLBooster`
   -Creates upload directories for resumes and cover letters
   - Generates `.env` file from template

3. **Start Menu Shortcuts**
   - "CL Booster" - Launches application
   - "Configuration" - Opens `.env` editor
   - "Database Setup" - Installs MariaDB schema
   - "Java Version Check" - Verifies Java installation

4. **Desktop Shortcut**
   - Quick access to launch application

### Post-Installation

After installation, users get:

1. **Quick Start Guide**
   - README.md opens automatically
   - Links to documentation

2. **Configuration Wizard**
   - Prompts for API keys
   - Validates settings

3. **Application Launch**
   - Automatic start option
   - Accessible at `http://localhost:8080`

## File Structure

```
installer/
├── setup.iss                          # Main Inno Setup configuration (DO NOT EDIT manually)
├── build-installer.bat                # Automated build script (RUN THIS)
├── run-app.bat                        # User-facing launcher
├── config-env.bat                     # Configuration utility
├── install-dependencies.bat           # Dependency checker
├── setup-db.bat                       # Database setup tool
├── java-runtime/                      # Java 21 JRE (to be added)
│   ├── bin/
│   ├── lib/
│   └── ...
├── .env.template                      # Configuration template (copy from root)
└── CLBooster-Setup-1.0.0.exe         # OUTPUT: Your installer
```

## Customization

### Change Application Name, Version, Icon

Edit `setup.iss`:

```pascal
#define MyAppName "CL Booster"
#define MyAppVersion "1.0.0"
#define MyAppIconName "cl-booster.ico"
```

### Add/Remove Languages

Modify the Inno Setup `[Languages]` section:

```pascal
[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "german"; MessagesFile: "compiler:Languages\German.isl"
; Add more as needed
```

### Customize Installation Path

Edit the default installation directory:

```pascal
DefaultDirName={localappdata}\CLBooster
```

Other options:
- `{programfiles}\CLBooster` - Program Files (requires admin)
- `{commonprogramfiles}\CLBooster`
- `{userdocs}\CLBooster`

### Modify Start Menu Shortcuts

In the `[Icons]` section of `setup.iss`:

```pascal
Name: "{group}\My Custom Shortcut"; Filename: "{app}\my-script.bat"
```

## Distribution

### For End Users

1. **Download**
   - Upload `CLBooster-Setup-1.0.0.exe` to your website
   - Or distribute via email/USB

2. **Installation**
   - User double-clicks `.exe`
   - Follows installation wizard
   - Application ready to launch

3. **First Run**
   - Configuration dialog appears
   - User enters Google Gemini API key
   - Application launches at `http://localhost:8080`

### For Developers

Maintain a GitHub Release:

1. Tag the version:
   ```cmd
   git tag -a v1.0.0 -m "Release 1.0.0"
   git push origin v1.0.0
   ```

2. Upload installer to GitHub Releases:
   - Navigate to: github.com/yourrepo/releases/new
   - Upload `CLBooster-Setup-1.0.0.exe`
   - Add release notes

## Troubleshooting

### Build Fails: "Maven not found"

```cmd
# Install Maven via Chocolatey (requires admin)
choco install maven

# Or add Maven to PATH manually
set PATH=%PATH%;C:\apache-maven-3.9.0\bin
```

### Build Fails: "Inno Setup not found"

```cmd
# Check alternate installation paths
where iscc.exe

# If still not found, install Inno Setup:
https://jrsoftware.org/isdl.php
```

### Build Fails: "Java not found"

```cmd
# Install Java 21+
https://www.oracle.com/java/technologies/downloads/

# Verify installation
java -version
```

### Build Fails: "JAR not created"

- Check Maven build output for compilation errors
- Ensure all dependencies are available
- Verify `pom.xml` is valid

### Installer Runs But App Won't Start

1. On first run, check if Java was correctly bundled:
   ```cmd
   cd "C:\Users\YourUsername\AppData\Local\CLBooster"
   .\java-runtime\bin\java -version
   ```

2. Manually launch with verbose output:
   ```cmd
   cd "C:\Users\YourUsername\AppData\Local\CLBooster"
   java -jar cl-booster-1.0-SNAPSHOT.jar
   ```

3. Check `.env` file configuration:
   ```cmd
   %APPDATA%\CLBooster\.env
   ```

### Application Can't Find Gemini API

- Verify `.env` file exists at `%APPDATA%\CLBooster\.env`
- Check `GEMINI_API_KEY` is set correctly
- Restart the application

## Advanced Options

### Silent Installation

For deployment in enterprise environments:

```cmd
CLBooster-Setup-1.0.0.exe /SILENT /NORESTART
```

### Uninstall

Users can uninstall via:
1. Windows Settings → Apps → CL Booster
2. Or directly: `%APPDATA%\CLBooster\uninstall.exe`

### Automatic Updates

For future updates, create new versions:

1. Update `#define MyAppVersion` in `setup.iss`
2. Rebuild: `build-installer.bat`
3. Distribute new `.exe`

## Performance Optimization

The installer includes:
- **LZMA2 compression** for smallest file size
- **Solid compression** for faster extraction
- **JRE bundling** for offline installation
- **Lazy component loading** for minimal overhead

Approximate installer sizes:
- Bare JAR: ~15-20 MB
- With JRE: ~180-190 MB
- With database: +150 MB

## Security Considerations

1. **Code Signing** (optional but recommended)
   - Sign the `.exe` with a certificate to prevent Windows warnings
   - Use: `signtool.exe` (included with Visual Studio)

2. **API Key Security**
   - The `.env` file is not included in installer
   - Users must provide their own API key
   - Stored locally in `%APPDATA%\CLBooster\.env`

3. **Database Credentials**
   - Default credentials in `setup-db.bat` should be changed
   - Use strong passwords for production

## Support and Maintenance

To update the installer:

1. Make changes to application code
2. Run `build-installer.bat` to rebuild
3. Test on clean Windows VM
4. Increment version in `setup.iss`
5. Rebuild and distribute

## References

- **Inno Setup Documentation**: http://www.jrsoftware.org/ishelp/
- **Java JRE Download**: https://www.oracle.com/java/technologies/downloads/
- **Maven Guide**: https://maven.apache.org/guides/
- **NSIS Alternative**: https://nsis.sourceforge.io/ (if you prefer NSIS over Inno Setup)

---

**Questions?** Check `dev_instructions.md` for development setup details.
