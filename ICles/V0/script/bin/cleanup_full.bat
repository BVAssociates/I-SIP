@echo OFF

echo suppression cache PAR
del /F /S /Q %TEMP%\par-%USERNAME%

echo construction du profil
perl -pe "s/^/set $1/ if !/^(#|REM|$)/; s/^#/REM /" "%~dp0\..\..\..\..\..\Portal\product\conf\IsisPortal_WIN32.ini" > "%~dp0\..\..\..\..\..\Portal\product\conf\IsisPortal_WIN32.bat"

echo chargement du profil
call "%~dp0\..\..\..\..\..\Portal\product\conf\IsisPortal_WIN32.bat"

del "%ISIP_LOG%\isip_cleanup.log"
FOR /F %%i IN ('select -s Environnement from environ') DO (
	echo lancement du defrag sur %%i
	PC_CLEAN_BASELINE -c %%i 2>> "%ISIP_LOG%\isip_cleanup.log"
)