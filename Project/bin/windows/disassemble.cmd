@echo off

rem
rem Run CVM disassembler on one or more ".obj" files.
rem

rem set config environment variables locally
setlocal
call cprl_config.cmd

set CLASSPATH=%COMPILER_PROJECT_PATH%
java -ea -cp "%CLASSPATH%" edu.citadel.cvm.DisassemblerKt %*

rem restore settings
endlocal
