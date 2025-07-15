chcp 65001
set a=%cd%
cd..
set b=%cd%
cd %a%

"%b%/java/bin/java.exe" -Dfile.encoding=utf-8 -jar  "%b%\jar\gameBuildTool-windows-x64-1.0.0.jar"