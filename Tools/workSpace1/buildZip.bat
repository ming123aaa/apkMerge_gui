chcp 65001
set a=%cd%
cd..
set b=%cd%
cd %a%

"%b%/java/bin/java.exe" -Dfile.encoding=utf-8 -jar  "%b%\jar\gameSdkTool.jar"  -javaPath "%b%/java/bin/java.exe" -libs "%b%\libs" -baseApk "%a%/base.zip"  -channelConfig "%a%\channel\ChannelConfig.json" -generateMultipleChannelApk

