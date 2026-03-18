; CL Booster Installer
; Inno Setup Script for creating a single-file installer EXE
; Compiles with: iscc.exe setup.iss

#define MyAppName "CL Booster"
#define MyAppVersion "1.0.0"
#define MyAppPublisher "CL Booster Project"
#define MyAppExeName "cl-booster.exe"
#define MyAppIconName "cl-booster.ico"

[Setup]
; Installer metadata
AppName= CLBooster
AppVersion= 1.0.0
AppPublisher= SWEP group 1
AppCopyright=Copyright (C) 2026 CL Booster Project
DefaultDirName={localappdata}\CLBooster
DefaultGroupName= Cl Booster
OutputDir=.\
OutputBaseFilename=CLBooster-Setup-{#MyAppVersion}
Compression=lzma2
SolidCompression=yes
PrivilegesRequired=lowest
ShowLanguageDialog=auto

; Installer appearance
WizardStyle=modern
WizardResizable=yes

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"
Name: "finnish"; MessagesFile: "compiler:Languages\Finnish.isl"
Name: "swedish"; MessagesFile: "compiler:Languages\Swedish.isl"

[Types]
Name: "full"; Description: "Full Installation"
Name: "compact"; Description: "Compact Installation"
Name: "custom"; Description: "Custom Installation"; Flags: iscustom

[Components]
Name: "application"; Description: "CL Booster Application (Required)"; Types: full compact custom; Flags: fixed
Name: "jre"; Description: "Java Runtime Environment (Required - 175 MB)"; Types: full compact custom; Flags: fixed
Name: "database"; Description: "MariaDB Database (Optional - required for CLI mode - 150 MB)"; Types: full; Flags: disablenouninstallwarning
Name: "docker"; Description: "Docker Desktop (Optional - for advanced users - downloads separately)"; Types: full; Flags: disablenouninstallwarning

[Dirs]
Name: "{app}"
Name: "{app}\java"
Name: "{app}\database"
Name: "{app}\lib"
Name: "{userappdata}\CLBooster\uploads"
Name: "{userappdata}\CLBooster\uploads\resumes"
Name: "{userappdata}\CLBooster\uploads\coverletters"

[Files]
; Application JAR files - copy from target/ after Maven build
Source: "..\target\cl-booster-1.0-SNAPSHOT.jar"; DestDir: "{app}"; Components: application; Flags: ignoreversion
Source: "..\README.md"; DestDir: "{app}"; Components: application; Flags: ignoreversion

; Environment template
Source: "..\.env.example"; DestDir: "{app}"; DestName: ".env.template"; Components: application; Flags: ignoreversion

; Java Runtime Environment (bundled - adjust path as needed)
; NOTE: Download Java 21 JRE and extract to installer\java-runtime\ before compilation
; UNCOMMENT the line below once you've added the java-runtime folder
; Source: "java-runtime\*"; DestDir: "{app}\java"; Flags: ignoreversion recursesubdirs createallsubdirs; Components: jre

; Database SQL scripts
Source: "..\src\main\java\com\clbooster\app\backend\service\database\coverletter_generator_script.sql"; DestDir: "{app}\database"; Components: database; Flags: ignoreversion

; Batch utility scripts
Source: "run-app.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "install-dependencies.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "config-env.bat"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
; Start menu shortcuts
Name: "{group}\CL Booster"; Filename: "{app}\run-app.bat"; IconFileName: "{app}\{#MyAppIconName}"; Comment: "Start CL Booster application"; Components: application
Name: "{group}\Configuration"; Filename: "{app}\config-env.bat"; Comment: "Configure API keys and settings"; Components: application
Name: "{group}\Database Setup"; Filename: "cmd.exe"; Parameters: "/k ""{app}\database\setup-db.bat"""; Comment: "Setup MariaDB database"; Components: database
Name: "{group}\Java Version Check"; Filename: "cmd.exe"; Parameters: "/k java -version"; Comment: "Verify Java installation"

; Desktop shortcuts
Name: "{userdesktop}\CL Booster"; Filename: "{app}\run-app.bat"; IconFileName: "{app}\{#MyAppIconName}"; Components: application

; Quick launch - if available
Name: "{userappdata}\Microsoft\Internet Explorer\Quick Launch\CL Booster"; Filename: "{app}\run-app.bat"; IconFileName: "{app}\{#MyAppIconName}"; Components: application; Flags: createonlyiffileexists

[Run]
; Create .env file from template if it doesn't exist
Filename: "cmd.exe"; Parameters: "/c if not exist ""{userappdata}\CLBooster\.env"" copy ""{app}\.env.template"" ""{userappdata}\CLBooster\.env"""; Flags: runhidden
; Run dependency check
Filename: "{app}\install-dependencies.bat"; Flags: runhidden; StatusMsg: "Checking system requirements..."
; Offer to open README
Filename: "notepad.exe"; Parameters: "{app}\README.md"; Flags: postinstall skipifsilent; Description: "View README&Next Steps"
; Offer to launch application
Filename: "{app}\run-app.bat"; Flags: postinstall skipifsilent; Description: "Launch CL Booster Now"

[UninstallDelete]
; Clean up user data on uninstall (optional - users choose)
Type: filesandordirs; Name: "{userappdata}\CLBooster"
Type: filesandordirs; Name: "{app}"

[Code]
{ Custom functions for installer logic }

function GetJavaVersion: String;
var
  ResultCode: Integer;
  JavaVersionOutput: String;
begin
  Result := '';
  if Exec(ExpandConstant('cmd.exe'), '/c java -version 2>&1 | find "version"', '', SW_HIDE, ewWaitUntilTerminated, ResultCode) then
  begin
    Result := 'Java is installed';
  end
  else
  begin
    Result := 'Java not found. Please install Java 21+';
  end;
end;

function CheckJavaInstalled: Boolean;
var
  ResultCode: Integer;
begin
  Result := Exec(ExpandConstant('cmd.exe'), '/c java -version >nul 2>&1', '', SW_HIDE, ewWaitUntilTerminated, ResultCode) and (ResultCode = 0);
end;

function CheckMavenInstalled: Boolean;
var
  ResultCode: Integer;
begin
  Result := Exec(ExpandConstant('cmd.exe'), '/c mvn -v >nul 2>&1', '', SW_HIDE, ewWaitUntilTerminated, ResultCode) and (ResultCode = 0);
end;

procedure InitializeWizard;
begin
  { Add post-install initialization if needed }
  if not CheckJavaInstalled then
  begin
    MsgBox('Java 21+ is required but not found on your system.' + Chr(13) + 'Please install Java from https://www.oracle.com/java/technologies/downloads/', mbError, MB_OK);
  end;
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssPostInstall then
  begin
    { Create .env file if it doesn't exist }
    if not FileExists(ExpandConstant('{userappdata}\CLBooster\.env')) then
    begin
      FileCopy(ExpandConstant('{app}\.env.template'), ExpandConstant('{userappdata}\CLBooster\.env'), False);
    end;
  end;
end;

