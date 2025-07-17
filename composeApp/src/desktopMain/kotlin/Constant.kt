import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import util.PropertiesStore

object Constant {

    val userDirPath: String = System.getProperty("user.dir")

    val channelConfigPath = "$userDirPath/channel/ChannelConfig.json"
    val oldChannelConfigPath = "$userDirPath/channel_old/ChannelConfig.json"
    val channelDir = "$userDirPath/channel"
    val oldChannelDir = "$userDirPath/channel_old"

    val apkConfigDir = "$userDirPath/channel/ApkConfig"
    val apkConfigResDir = "$userDirPath/channel/ApkConfig/res"

    val signConfigDir= "$userDirPath/channel/SignConfig"
    val signFileDir= "$userDirPath/channel/SignConfig/Sign"
    val sdkFileDir= "$userDirPath/channel/sdk"

    val outputChannelZip="$userDirPath/output/channel.zip"
    val outputDir="$userDirPath/output"

    val baseApk="${userDirPath}/base.apk"
    val baseZip="${userDirPath}/base.zip"



    val buildApkBat="${userDirPath}/build.bat"
    val buildZipBat="${userDirPath}/buildZip.bat"
    val metadataKey="${userDirPath}/metaDataKey.json"
    val userConfig="${userDirPath}/user.txt"





    fun getApkConfigPath(channelName: String): String {
        return apkConfigDir + "/ApkConfig_${channelName}.json"
    }

    fun getApkConfigName(channelName: String): String {
        return "ApkConfig/ApkConfig_${channelName}.json"
    }

    fun getSignConfigPath(signName:String):String{
        return signConfigDir+"/SignConfig_${signName}.json"
    }

    fun getMetaDataKeyMap():Map<String,String>{
        try {
            return Gson().fromJson<HashMap<String,String>>(FileUtils.readText(metadataKey),object :TypeToken<HashMap<String,String>>(){}.type)
        }catch (_:Throwable){}
        return  mapOf()
    }

}