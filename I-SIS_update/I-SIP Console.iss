; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{D523E93D-755A-4591-B164-A0EC7C23D6DC}
AppName=I-SIP Console
AppVersion=2.0.6
;AppVerName=I-SIP Console 2.0.6
AppPublisher=BV Associates
AppPublisherURL=http://www.bvassociates.fr
AppSupportURL=http://www.bvassociates.fr
AppUpdatesURL=http://www.bvassociates.fr
DefaultDirName={pf}\BV Associates\I-SIP Console
DisableDirPage=no
DefaultGroupName=I-SIP
OutputBaseFilename=Setup I-SIP Console 2.0.6
SetupIconFile=Console\I-SIS Console.ico
Compression=lzma
SolidCompression=yes
UninstallDisplayIcon={app}\I-SIS Console.ico


[Languages]
Name: "french"; MessagesFile: "compiler:Languages\French.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: "Console\I-SIS Console.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "Console\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
Name: "{group}\I-SIP Console"; Filename: "{app}\I-SIS Console.exe" ; IconFilename: "{app}\I-SIS Console.ico" ;WorkingDir: "{app}"
Name: "{commondesktop}\I-SIP Console"; Filename: "{app}\I-SIS Console.exe"; Tasks: desktopicon  ; IconFilename: "{app}\I-SIS Console.ico" ;WorkingDir: "{app}"

[ini]
Filename: "{app}\prefs\{username}.prefs"; Section: "AUTO-EXPLORE"; Key: "Enabled"; String: "true"; Flags: createkeyifdoesntexist
Filename: "{app}\prefs\{username}.prefs"; Section: "AUTO-EXPLORE"; Key: "PreloadMenus"; String: "false"; Flags: createkeyifdoesntexist
Filename: "{app}\prefs\{username}.prefs"; Section: "AUTO-EXPLORE"; Key: "RemoveUnnecessaryNodes"; String: "true"; Flags: createkeyifdoesntexist
Filename: "{app}\prefs\{username}.prefs"; Section: "AUTO-EXPLORE"; Key: "Tables"; String: """PortalAccess,AgentAccess,ICleAccess,ICleServices,PortalApplicationAccess"""; Flags: createkeyifdoesntexist

Filename: "{app}\prefs\{username}.prefs"; Section: "OpenURL"; Key: "Browser.Command"; String: """cmd /c start /B """" ""%[url]"""""; Flags: createkeyifdoesntexist


[Run]
Filename: "{app}\I-SIS Console.exe"; Description: "{cm:LaunchProgram,I-SIP Console}"; Flags: nowait postinstall skipifsilent

