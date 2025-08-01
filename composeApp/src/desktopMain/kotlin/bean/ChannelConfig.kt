package bean

import Constant
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser

import java.io.File

/**
 *  ChannelConfig.json 数据结构
 *
 */
class ChannelConfig {
    var buildDirFile = "build"
    var outDirFile = "buildOutApk"
    var enableChannelNames: List<String> = ArrayList<String>()  //设置可用渠道(channelName 或者 channelGroupName)都行  为空时所有渠道都可用
    var disableChannelNames: List<String> = ArrayList<String>()  //设置可用渠道(channelName 或者 channelGroupName)都行  为空时所有渠道都可用
    var items: MutableList<ChannelConfigItem> = ArrayList<ChannelConfigItem>()
}

class ChannelConfigItem {
    var channelName = ""  //渠道的名称  必须设置
    var apkConfigFile = ""  //apkConfig.json的相对路径 必须设置
    var signConfigFile = "" //signConfig.json的相对路径 必须设置
    var mode = ""  //修改渠道包的模式 默认skip模式 必须设置
    var extraCmd: List<String> = ArrayList<String>() //用于设置合并代码时额外的cmd参数
    var channelApkFile = ""  //合并渠道包框架的apk
    var listMergeConfigs: List<MergeConfig> = ArrayList<MergeConfig>()
    var channelEnable = true //是否可用
    var channelGroupName = "" //组名
    var describe = "" //描述
    var apkName = "" //不设置自动使用渠道名
}

class MergeConfig {
    var enable = true
    var extraCmd: List<String> = ArrayList<String>() //用于设置合并代码时额外的cmd参数
    var channelApkFile = ""  //合并渠道包框架的apk
}

fun createChannelConfigItem(channelName: String): ChannelConfigItem {
    return ChannelConfigItem().apply {
        this.channelName = channelName
        this.apkConfigFile = Constant.getApkConfigName(channelName)
        this.channelEnable = false
        this.mode = MODE_SIMPLE_Fast
        this.extraCmd = listOf("-useChannelRes", "-useChannelCode")
    }
}

const val MODE_SKIP = "skip"// skip:跳过处理
const val MODE_SIMPLE = "simple" //simple模式:不合并只进行修改包名图标等操作
const val MODE_SIMPLE_Fast = "simple_fast" //simple_fast模式:比simple模式更快(若修改了图标和应用名称,请用simple模式)
const val MODE_MERGE = "merge" //merge模式:合并代码资源
const val MODE_LIST = "merge_list" //merge_list模式:合并代码资源,支持多个包合并
const val MODE_MERGE_Reverse = "merge_reverse" //merge_reverse:反向合并代码资源,渠道包作为主包
const val MODE_CHANGE="change"
const val MODE_DECOMPILE="decompile"

fun readChannelConfig(): ChannelConfig {
    if (!File(Constant.channelConfigPath).exists()) {
        writeChannelConfig(channelConfig = ChannelConfig())
    }
    try {
        return Gson().fromJson<ChannelConfig>(FileUtils.readText(Constant.channelConfigPath), ChannelConfig::class.java)
    } catch (e: Throwable) {
    }
    return ChannelConfig()
}

fun readChannelConfig(path:String): ChannelConfig {
    if (!File(path).exists()) {
        writeChannelConfig(channelConfig = ChannelConfig())
    }
    try {
        return Gson().fromJson<ChannelConfig>(FileUtils.readText(path), ChannelConfig::class.java)
    } catch (e: Throwable) {
    }
    return ChannelConfig()
}

fun writeChannelConfig(channelConfig: ChannelConfig) {
    FileUtils.writeText(Constant.channelConfigPath, getJsonFormatter(Gson().toJson(channelConfig)))
}
fun getJsonFormatter(json: String): String {
    try {
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(JsonParser.parseString(json))
    } catch (e: Throwable) {

    }
    return json
}