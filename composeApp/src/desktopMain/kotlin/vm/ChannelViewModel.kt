package vm

import Constant
import FileUtils
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import bean.*
import compose.update
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import screen.*
import util.FileSelect
import util.ZipUtil
import java.io.File
import java.lang.Exception

@Stable
class ChannelViewModel {
    private var mChannelConfig: ChannelConfig? = null

    val editChannelNameState = mutableStateOf("")
    val searchKeywords = mutableStateOf("")

    val isTopChannel = mutableStateOf(UserPropertiesStore.isTopChannel)

    val lazyListState = LazyListState(0, 0)


    val channelListState: SnapshotStateList<MutableState<ChannelListItemState>> = SnapshotStateList()


    suspend fun initChannelConfig() {
        withContext(Dispatchers.IO) {
            try {
                mChannelConfig = readChannelConfig()

            } catch (e: Throwable) {
                ErrorTipState.update { it.copy(isShow = true, msg = e.stackTraceToString()) }
            }
        }
        refreshChannelConfig()
    }

    fun refreshChannelConfig() {
        search(searchKeywords.value)
    }


    private suspend fun saveChannelConfig() {
        withContext(Dispatchers.IO) {
            mChannelConfig?.let {
                try {
                    writeChannelConfig(it)
                } catch (e: Throwable) {
                    ErrorTipState.update { it.copy(isShow = true, msg = e.stackTraceToString()) }
                }
            }
        }
    }


    fun search(txt: String) {
        searchKeywords.value = txt
        val newDatas = mChannelConfig?.items?.filter {
            it.channelName.contains(txt, ignoreCase = true) || it.describe.contains(
                txt,
                ignoreCase = true
            ) || it.apkName.contains(txt, ignoreCase = true)
        }
        if (channelListState.isNotEmpty()) {
            channelListState.clear()
        }
        if (newDatas != null) {
            if (isTopChannel.value) {
                val data = newDatas.sortedBy { if (it.channelEnable) 0 else 1 }
                    .map { mutableStateOf(it.toChannelListItemState()) }
                channelListState.addAll(data)
            } else {
                channelListState.addAll(newDatas.map { mutableStateOf(it.toChannelListItemState()) })
            }
        } else {
            ErrorTipState.update { it.copy(isShow = true, msg = "读取配置文件失败") }
        }
    }

    val editChannelConfigState = mutableStateOf(ChannelConfigState())
    var lastEditChannel = ""

    fun initEditChannelConfigState(channel: String) {
        if (channel != lastEditChannel) {
            lastEditChannel = channel
            val findChannelConfigState = findChannelConfigState(channel)
            if (findChannelConfigState != null) {
                editChannelConfigState.value = findChannelConfigState
            } else {
                editChannelNameState.value = ""
                lastEditChannel = ""
            }
        }
    }

    private fun findChannelConfigState(channel: String): ChannelConfigState? {
        val item = mChannelConfig!!.items.find { it.channelName == channel }
        if (item != null) {
            return item.toState()
        }
        return null
    }

    suspend fun addChannel(channel: String): Boolean {
        try {
            if (mChannelConfig!!.items.find { it.channelName == channel } != null) {
                return false
            }
            mChannelConfig!!.items.add(0, createChannelConfigItem(channelName = channel))
            saveChannelConfig()
            initChannelConfig()
            editChannelNameState.value = channel
        } catch (e: Exception) {
            ErrorTipState.update { it.copy(isShow = true, msg = e.stackTraceToString()) }
        }
        return true
    }

    suspend fun setChannelEnable(item: MutableState<ChannelListItemState>, isEnable: Boolean) {
        mChannelConfig?.items?.find { it.channelName == item.value.name }?.channelEnable = isEnable
        saveChannelConfig()
        item.update { it.copy(isEnable = isEnable) }
        refreshChannelConfig()
    }

    suspend fun deleteChannel(item: MutableState<ChannelListItemState>) {
        mChannelConfig?.items?.removeIf { it.channelName == item.value.name }
        FileUtils.deleteFile(Constant.getApkConfigPath(item.value.name))
        saveChannelConfig()
        initChannelConfig()
    }

    suspend fun setAllChannelEnable(isEnable: Boolean) {
        val names = channelListState.map { it.value.name }
        mChannelConfig?.items?.forEach {
            if (names.contains(it.channelName)) {
                it.channelEnable = isEnable
            }
        }
        saveChannelConfig()
        initChannelConfig()
    }


    suspend fun setChannelConfigState(channelConfigState: ChannelConfigState) {
        val channel = channelConfigState.channel
        mChannelConfig?.items?.find { it.channelName == channel }?.let {
            it.setState(channelConfigState)
            channelConfigState.apkConfigState.write(channel)
        }
        saveChannelConfig()
        initChannelConfig()

    }

    suspend fun outputAllConfig() {
        withContext(Dispatchers.IO) {
            FileUtils.deleteFile(Constant.channelDir + "/build")
            FileUtils.deleteFile(Constant.channelDir + "/buildOutApk")
            ZipUtil.zip(Constant.channelDir, Constant.outputChannelZip)
            FileSelect.openFile(Constant.userDirPath + "\\output")
        }
    }

    suspend fun inputAllConfig() {
        withContext(Dispatchers.IO) {
            val file = FileSelect.selectFile()
            File(Constant.channelConfigPath).copyTo(File(Constant.oldChannelConfigPath), overwrite = true)
            if (file != null) {
                ZipUtil.unzip(file.absolutePath, Constant.userDirPath)
                initChannelConfig()
                megeOldConfig()
            }
        }
    }

    private suspend fun megeOldConfig() {

        val readChannelConfig = readChannelConfig(Constant.oldChannelConfigPath)
        if (mChannelConfig == null) {
            return
        }
        val channels = mChannelConfig!!.items.map { it.channelName }
        readChannelConfig.items.forEach {
            if (!channels.contains(it.channelName)) {
                mChannelConfig!!.items.add(it)
            }
        }
        saveChannelConfig()
        initChannelConfig()
    }

    fun checkChannel(): Boolean {
        val stringBuilder = StringBuilder()

        var isSuccess = true
        mChannelConfig?.items?.forEach {
            if (it.signConfigFile.isEmpty()) {
                stringBuilder.append("渠道名:${it.channelName} 未设置签名配置").append("\n")
                isSuccess = false
            }
            if (it.apkConfigFile.isEmpty()) {
                stringBuilder.append("渠道名:${it.channelName} 未设置ApkConfig").append("\n")
                isSuccess = false
            }
        }
        if (!isSuccess) {
            ErrorTipState.update { it.copy(isShow = true, msg = stringBuilder.toString()) }
        }
        return isSuccess
    }


}