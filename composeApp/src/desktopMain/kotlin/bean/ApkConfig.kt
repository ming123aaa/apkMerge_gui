package bean

import Constant
import FileUtils
import com.google.gson.Gson

import java.io.File
import java.util.TreeMap
typealias ResType =String
typealias OldName =String
typealias NewName =String
/**
 * apk修改配置  只用于json保存
 */
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

class ReplaceStringData {

    var isReplaceFirst = false
    var isRegex = false
    var matchString: String = ""
    var replaceString: String = ""

}

fun readApkConfig(channelName: String): ApkConfig {
    try {
        return Gson().fromJson(readApkConfigContent(channelName) ,ApkConfig::class.java)
    }catch (_:Throwable){}
    return ApkConfig()
}

fun readApkConfigContent(channelName: String):String {
    val apkConfigPath = Constant.getApkConfigPath(channelName)
    if (!File(apkConfigPath).exists()) {
        writeApkConfig(channelName, ApkConfig())
    }
    return getJsonFormatter(FileUtils.readText(apkConfigPath))
}

fun writeApkConfig(channelName: String, apkConfig: ApkConfig) {
    writeApkConfigContent(channelName,Gson().toJson(apkConfig))
}

fun writeApkConfigContent(channelName: String, content: String){
    val apkConfigPath = Constant.getApkConfigPath(channelName)
    FileUtils.writeText(apkConfigPath, getJsonFormatter(content))
}


