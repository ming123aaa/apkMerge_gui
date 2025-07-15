chcp 65001
set a=%cd%
cd..
set b=%cd%
cd %a%

java -Dfile.encoding=utf-8 -jar  "%b%\jar\gameSdkTool.jar"  -libs "%b%\libs" -baseApk "%a%/base.apk"  -channelConfig "%a%\channel\ChannelConfig.json" -generateMultipleChannelApk

