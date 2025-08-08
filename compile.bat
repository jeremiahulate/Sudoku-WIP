@echo off
echo Compiling Sudoku game...

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all java source files with Gson library
javac -cp "lib/*" -d bin src\*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful.
    echo Running Sudoku game...
    cd bin
    java -cp "../lib/*;." App
    cd ..
) else (
    echo Compilation failed.
)
pause