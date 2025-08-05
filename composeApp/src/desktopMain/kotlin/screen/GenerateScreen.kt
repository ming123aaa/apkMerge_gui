package screen

import Constant
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import compose.update
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import util.FileSelect
import util.RuntimeUtil
import vm.ErrorTipState
import java.io.File


data class GenerateData(
    val apkPath: String = "",
    val isShowLastApk: Boolean = false,//显示 使用上次主包按钮
    val isUseLastApk: Boolean = false, //使用上次主包
    val isRunning: Boolean = false, //正在运行状态
    val isWaitStop: Boolean = false, //等待停止状态
    val msg: String = "",
    val logMsg: String = "",
    val isComplete: Boolean = false
)

@Composable
fun GenerateScreen(onBack: () -> Unit) {
    val generateData =
        remember { mutableStateOf(GenerateData(isShowLastApk = File(Constant.baseApk).exists() || File(Constant.baseZip).exists())) }
    val rememberCoroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 返回按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,

            ) {
            Button(
                onClick = {
                    onBack()
                },
                enabled = !generateData.value.isRunning
            ) {
                Text("返回")
            }
        }
        SelectApk(generateData, rememberCoroutineScope)

        StartButton(generateData, rememberCoroutineScope)
        if (generateData.value.isRunning) {
            LaunchedEffect(Unit) {
                val time = System.currentTimeMillis()
                while (generateData.value.isRunning) {
                    delay(1000)
                    generateData.update { it.copy(msg = "用时:" + (formatSeconds((System.currentTimeMillis() - time) / 1000))) }
                }
            }
        }

        // 消息显示
        if (generateData.value.msg.isNotEmpty()) {
            Text(generateData.value.msg)
        }
        Box {
            val rememberScrollState = rememberScrollState()
            LaunchedEffect(generateData.value.isRunning) {
                rememberScrollState.scrollTo(rememberScrollState.maxValue)
            }
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState)) {
                if (generateData.value.logMsg.isNotEmpty()) {
                    Text(text = "打包日志:")
                    SelectionContainer() {
                        Text(text = generateData.value.logMsg)
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd),
                adapter = rememberScrollbarAdapter(rememberScrollState)
            )
        }


    }

}

@Composable
private fun SelectApk(
    generateData: MutableState<GenerateData>,
    rememberCoroutineScope: CoroutineScope
) {
    if (!generateData.value.isRunning && !generateData.value.isComplete) {
        // APK 路径显示
        if (generateData.value.isShowLastApk) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChexBoxText(generateData.value.isUseLastApk, onCheckedChange = { b ->
                    generateData.update {
                        it.copy(isUseLastApk = b)
                    }
                }, text = "使用上次的主包")
            }
        }
        if (!generateData.value.isUseLastApk) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "主包:" + generateData.value.apkPath.ifEmpty { "请选择主包.apk/.zip文件  zip文件:解压后为apktool反编译后的文件" },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    rememberCoroutineScope.launch(Dispatchers.IO) {
                        val file = FileSelect.selectFile()
                        if (file != null) {
                            generateData.update { it.copy(apkPath = file.absolutePath) }
                        }
                    }
                }) {
                    Text("选择文件")
                }
            }
        }
    }
}

@Composable
private fun StartButton(
    generateData: MutableState<GenerateData>,
    rememberCoroutineScope: CoroutineScope
) {
    Row {
        Button(
            onClick = {
                if (generateData.value.isComplete) {
                    FileSelect.openFile(Constant.userDirPath + "\\channel\\buildOutApk")
                } else {
                    rememberCoroutineScope.launch(Dispatchers.IO) {
                        isRunning = true
                        generateData.update { it.copy(isRunning = true, msg = "", logMsg = "") }
                        try {
                            if (!generateData.value.isUseLastApk) {
                                File(Constant.baseApk).delete()
                                File(Constant.baseZip).delete()
                                if (generateData.value.apkPath.endsWith(".apk")) {
                                    File(generateData.value.apkPath).copyTo(File(Constant.baseApk), overwrite = true)
                                } else {
                                    File(generateData.value.apkPath).copyTo(File(Constant.baseZip), overwrite = true)
                                }
                            }
                            if (isRunning) {
                                val isApk = File(Constant.baseApk).exists()
                                if (isApk) {
                                    if (File(Constant.buildApkBat).exists()) {
                                        buildUseBat(generateData, Constant.buildApkBat)
                                    } else {
                                        buildUseJar(generateData)
                                    }
                                } else {
                                    if (File(Constant.buildZipBat).exists()) {
                                        buildUseBat(generateData, Constant.buildZipBat)
                                    } else {
                                        buildUseJar(generateData)
                                    }
                                }

                                delay(1000)
                            }


                            if (isRunning) {
                                FileSelect.openFile(Constant.userDirPath + "\\channel\\buildOutApk")
                            }
                        } catch (e: Throwable) {
                            ErrorTipState.update { it.copy(isShow = true, msg = e.message ?: "") }
                        }
                        generateData.update { it.copy(isRunning = false, isComplete = true, isWaitStop = false) }
                        isRunning = false

                    }
                }
            },
            enabled = !generateData.value.isRunning && (generateData.value.apkPath.isNotEmpty() || generateData.value.isUseLastApk)
        ) {
            Text(
                if (generateData.value.isRunning) {
                    "生成中..."
                } else {
                    if (generateData.value.isComplete) "生成完成,查看生成文件" else "开始生成"
                }
            )
        }

        Button(modifier = Modifier.padding(start = 10.dp), onClick = {

            if (process == null) {
                generateData.update { it.copy(isWaitStop = true) }
                isRunning = false
            } else {
                val mProcess = process!!
                generateData.update { it.copy(isWaitStop = true) }
                isRunning = false
                rememberCoroutineScope.launch(Dispatchers.IO) {
                    try {
                        RuntimeUtil.destroyForcibly(mProcess)
                    } catch (e: Throwable) {
                        ErrorTipState.update { it.copy(isShow = true, msg = e.message ?: "") }
                    }

                }

            }
        }, enabled = generateData.value.isRunning && !generateData.value.isWaitStop) {
            Text("停止")
        }
    }
}


@Volatile
private var process: Process? = null

@Volatile
private var isRunning: Boolean = false

private fun buildUseBat(generateData: MutableState<GenerateData>, batPath: String) {
    RuntimeUtil.execAndPrint(
        arrayOf(batPath),
        24 * 60 * 60,
        errorCall = { msg -> generateData.update { it.copy(logMsg = it.logMsg + "\n" + msg) } },
        onProcess = {
            process = it
        }) { msg ->
        generateData.update { it.copy(logMsg = it.logMsg + "\n" + msg) }
        return@execAndPrint false
    }
    process = null
}


private fun buildUseJar(generateData: MutableState<GenerateData>) {
    val parentPath = File(Constant.userDirPath).parentFile.absolutePath
    val java = if (File("${Constant.userDirPath}/java/bin/java.exe").exists()) {
        "${Constant.userDirPath}/java/bin/java.exe"
    } else if (File("${parentPath}/java/bin/java.exe").exists()) {
        "${parentPath}/java/bin/java.exe"
    } else {
        "java"
    }
    val gameSdkTool = if (File("${Constant.userDirPath}/jar/gameSdkTool.jar").exists()) {
        "${Constant.userDirPath}/jar/gameSdkTool.jar"
    } else {
        "${parentPath}/jar/gameSdkTool.jar"
    }

    val libs = if (File("${Constant.userDirPath}/libs").exists()) {
        "${Constant.userDirPath}/libs"
    } else {
        "${parentPath}/libs"
    }

    val apkPath = if (File(Constant.baseApk).exists()) {
        Constant.baseApk
    } else {
        Constant.baseZip
    }

    val cmd =
        "$java -Dfile.encoding=utf-8 -jar  ${gameSdkTool} -javaPath ${java} -libs ${libs} -baseApk  ${apkPath} -channelConfig ${Constant.userDirPath}/channel/ChannelConfig.json -generateMultipleChannelApk"
    RuntimeUtil.execAndPrint(
        arrayOf("cmd.exe", "/c", cmd),
        24 * 60 * 60,
        errorCall = { msg -> generateData.update { it.copy(logMsg = it.logMsg + "\n" + msg) } },
        onProcess = {
            process = it
        },
        call = { msg ->
            generateData.update { it.copy(logMsg = it.logMsg + "\n" + msg) }
            return@execAndPrint false
        })
    process = null
}

fun formatSeconds(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val secs = seconds % 60

    // 使用字符串格式化来确保小时、分钟和秒都是两位数
    return String.format("%02d:%02d:%02d", hours, minutes, secs)
}