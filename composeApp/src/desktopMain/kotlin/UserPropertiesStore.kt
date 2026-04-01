import Constant.userConfig
import kotlinx.coroutines.runBlocking
import util.PropertiesStore

object UserPropertiesStore {

    private val userPropertiesStore = PropertiesStore(userConfig)

    var isTopChannel: Boolean
        get() {
            return runCatching {
                userPropertiesStore.get("isTopChannel").toBoolean()
            }.getOrNull() ?: false
        }
        set(value) {
            runCatching{
                userPropertiesStore.put("isTopChannel", value.toString())
            }
        }

    var isShowApkConfig: Boolean
        get() {
            return runCatching {
                userPropertiesStore.get("isShowApkConfig").toBoolean()
            }.getOrNull() ?: false
        }
        set(value) {
            runCatching{
                userPropertiesStore.put("isShowApkConfig", value.toString())
            }
        }
    var selectBaseApkPath: String
        get() {
            return runCatching {
                userPropertiesStore.get("selectBaseApkPath")
            }.getOrNull() ?: ""
        }
        set(value) {
            runCatching{
                userPropertiesStore.put("selectBaseApkPath", value.toString())
            }
        }
    var outputApkPath: String
        get() {
            return runCatching {
                userPropertiesStore.get("outputApkPath")
            }.getOrNull() ?: ""
        }
        set(value) {
            runCatching{
                userPropertiesStore.put("outputApkPath", value.toString())
            }
        }

    var isWriteCopyApkLog: Boolean
        get() {
            return runCatching {
                userPropertiesStore.get("isWriteCopyApkLog").toBoolean()
            }.getOrNull() ?: false
        }
        set(value) {
            runCatching{
                userPropertiesStore.put("isWriteCopyApkLog", value.toString())
            }
        }



}