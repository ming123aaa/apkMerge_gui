android apk合并工具
可用于打渠道包，修改apk，合并apk的代码和资源
本项目基于[apkMerge](https://github.com/ming123aaa/apkMerge.git)开发的gui，项目运行环境依赖java17以上版本


打包:
运行 gradle->Tasks->compose Desktop->packageUberJarForCurrentOS
生成 composeApp/build/compose/jars/gameBuildTool-windows-x64-1.0.0.jar


运行环境配置：参考[Tools](Tools)
[metaDataKey.json](Tools/workSpace1/metaDataKey.json) 用于设置可配置<meta-data>数据[metaDataKey.json](Tools/workSpace1/metaDataKey.json)

需要在java17以上的版本运行
如果已经将jdk 17设置为环境变量,运行 Tools/workSpace0 里面的  [运行.vbs](Tools/workSpace0/运行.vbs)
若不想将jdk17设为环境变量,可将jdk17复制到[Tools\java](Tools/java)内,运行 Tools/workSpace1 里面的  [运行.vbs](Tools/workSpace1/运行.vbs)



若想修改批量打包的命令：
 主包为apk [build.bat](Tools/workSpace1/build.bat)  
 主包为zip [buildZip.bat](Tools/workSpace1/buildZip.bat)


可选打包模式:
merge(合并sdk)
simple_fast(渠道包快速打包,只支持修改<meta-data/>、包名等只需要修改AndroidManifest.xml的配置)
simple(渠道包完全版,支持修改ApkConfig.json的所有内容)
merge_reverse(sdk作为主包合并)
merge_list(多sdk合并)