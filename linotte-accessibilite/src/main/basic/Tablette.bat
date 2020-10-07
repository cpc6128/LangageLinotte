@echo off

setlocal
for /f "tokens=2 delims=[]" %%i in ('ver') do set VERSION=%%i
for /f "tokens=2-3 delims=. " %%i in ("%VERSION%") do set VERSION=%%i.%%j
if "%VERSION%" == "5.00" echo chcp 1252 >NUL
if "%VERSION%" == "5.0" echo chcp 1252 >NUL
if "%VERSION%" == "5.1" echo chcp 1252 >NUL
if "%VERSION%" == "5.2" echo chcp 1252 >NUL
if "%VERSION%" == "6.0" echo chcp 1252 >NUL
if "%VERSION%" == "6.1" echo chcp 1252 >NUL
endlocal

title lecture
java -jar Atelier.jar -a %1
