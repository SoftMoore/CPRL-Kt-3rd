@echo off

rem
rem Configuration settings for the CPRL compiler project.
rem
rem These settings assume an IntelliJ IDEA project named "CompilerProject" with a module
rem named "compiler".  Class files are placed in the IDEA default "out\production" directory.

rem set EXAMPLES_HOME to the directory for the test examples
set EXAMPLES_HOME=C:\Compilers\examples

rem set PROJECT_HOME to the directory for your compiler project
set PROJECT_HOME=C:\Compilers\CompilerProject

rem set CLASSES_HOME to the directory name used for compiled Java classes
set CLASSES_HOME=%PROJECT_HOME%\out\production\compiler

rem set KT_LIB_HOME to the directory for the Kotlin jar files
set KT_LIB_HOME=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition\plugins\Kotlin\kotlinc\lib

rem Add project-related class directories to COMPILER_PROJECT_PATH.
set COMPILER_PROJECT_PATH=%CLASSES_HOME%;%KT_LIB_HOME%\kotlin-stdlib.jar
