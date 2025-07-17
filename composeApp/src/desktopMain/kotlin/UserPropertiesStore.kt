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


}