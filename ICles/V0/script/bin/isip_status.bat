@echo OFF

echo suppression cache PAR
del /F /S /Q %TEMP%\par-%USERNAME%

echo construction du profil
perl -pe "s/^/set $1/ if !/^(#|REM|$)/; s/^#/REM /" "%~dp0\..\..\..\..\..\Portal\product\conf\IsisPortal_WIN32.ini" > "%~dp0\..\..\..\..\..\Portal\product\conf\IsisPortal_WIN32.bat"

echo chargement du profil
call "%~dp0\..\..\..\..\..\Portal\product\conf\IsisPortal_WIN32.bat"


echo verification de l'etat
PC_ISIP_STATUS -t IsisPortalV202_ISIP