android apk合并工具
可用于打渠道包，修改apk，合并apk的代码和资源
基于[apkMerge](https://github.com/ming123aaa/apkMerge)实现。

## 使用
项目运行环境:
系统:windows
java版本:java17以上版本

需要在java17以上的版本运行
如果已经将jdk 17设置为环境变量,运行 Tools/workSpace0 里面的  [运行.vbs](Tools/workSpace0/运行.vbs)
若不想将jdk17设为环境变量,可将jdk17复制到[Tools\java](Tools/java)内,运行 Tools/workSpace1 里面的  [运行.vbs](Tools/workSpace1/运行.vbs)


## 编译打包
运行 gradle->Tasks->compose Desktop->packageUberJarForCurrentOS
生成目录 composeApp/build/compose/jars/gameBuildTool-windows-x64-1.0.0.jar



## 说明
运行环境配置：参考[Tools](Tools)
[metaDataKey.json](Tools/workSpace1/metaDataKey.json) 用于设置可配置<meta-data>数据[metaDataKey.json](Tools/workSpace1/metaDataKey.json)


若想修改批量打包的命令：
 主包为apk [build.bat](Tools/workSpace1/build.bat)  
 主包为zip [buildZip.bat](Tools/workSpace1/buildZip.bat)

### 打包模式
可选打包模式:
merge(合并sdk)
simple_fast(渠道包快速打包,只支持修改<meta-data/>、包名等只需要修改AndroidManifest.xml的配置)
simple(渠道包完全版,支持修改ApkConfig.json的所有内容)
merge_reverse(sdk作为主包合并)
merge_list(多sdk合并)


### ApkConfig配置说明

```kotlin
class ApkConfig {
var packageName: String = "" // android包名
var iconImgPath = "" //图片相对于配置文件的路径
var iconSize = "-xxhdpi" //图标大小
var appName = "" //app名称
var versionCode="" //版本号
var versionName="" //版本名称
var minSdkVersion="" //最小sdk版本
var targetSdkVersion="" //目标sdk版本
var abiNames:List<String> = ArrayList<String>() //需要保留的abi架构,为空就是保留所有的架构
var metaDataMap: MutableMap<String,String> = TreeMap<String,String>() // meta-data修改
var replaceStringManifest:List<ReplaceStringData> = emptyList() // AndroidManifest.xml 字符串替换   用于复杂的数据替换
var deleteFileList:List<String> = emptyList() //需要删除的文件， 示例 /res/mipmap-anydpi
var changeClassPackage:Map<OldName,NewName> = emptyMap() // 修改class所在的包名  com.xxx.yyy 中间用.隔开
var renameResMap:Map<ResType,Map<OldName,NewName>> = emptyMap() // Map<type,Map<oldName,newName>>
var smaliClassSizeMB:Long=0   //限制smaliClass文件的大小,避免方法数量超出限制无法打包,推荐值50M  若smaliClassSizeMB<=0或smaliClassSizeMB>=1000将不限制文件大小
var deleteSmaliPaths: List<String> =emptyList() //需要删除的smail的文件   aa/bb   aa/cc.smali
var isDeleteSameNameSmali: Boolean=true  //是否删除相同名称的smali文件
var deleteManifestNodeNames: Set<String> =emptySet() //根据name删除的AndroidManifest.xml对应的节点
}
```