@echo off
REM Execution script for Windows
if not exist "bin" mkdir bin
if not exist "data" mkdir data

echo Compiling application sources...
javac -d bin -sourcepath src src\Main.java src\TestRunner.java src\model\*.java src\controller\*.java src\view\*.java
if errorlevel 1 exit /b 1

echo Launching Sports Club Membership Management System...
java -cp bin Main
pause
