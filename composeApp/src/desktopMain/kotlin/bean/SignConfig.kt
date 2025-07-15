package bean

import FileUtils
import com.google.gson.Gson
import dialog.TipDialog
import java.io.File

/**
 * 签名配置  只用于json保存
 */
class SignConfig {

    var signFileName = ""
    var alias = ""
    var passWord = ""
    var keyPassWord = ""
    var signVersion = ""
}


fun readSignConfig(path: String): SignConfig {
    if (!File(path).exists()) {
        writeSignConfig(path, SignConfig())
    }
    try {
        return Gson().fromJson(FileUtils.readText(path), SignConfig::class.java)
    } catch (e: Throwable) {

    }
    return SignConfig()
}


fun writeSignConfig(path: String, signConfig: SignConfig) {
    FileUtils.writeText(path, getJsonFormatter(Gson().toJson(signConfig)))
}