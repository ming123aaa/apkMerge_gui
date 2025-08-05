@file:OptIn(ExperimentalLayoutApi::class)

package screen

import Constant
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bean.*
import com.google.gson.Gson
import compose.TextCenter
import compose.update
import dialog.SelectListDialog
import theme.color333
import theme.colorf00
import theme.getTipTextColor
import vm.ErrorTipState

data class ChannelConfigState(
    val channel: String = "",
    val describe: String = "",//描述
    val apkName: String = "", //apk名称
    val signConfigFile: String = "", //signConfig.json的相对路径
    val apkConfigState: ApkConfigState = ApkConfigState(),
    val mode: String = "",  //打包模式
    val channelApkFile: String = "",//sdk文件
    val extraCmd: SnapshotStateList<String> = mutableStateListOf<String>(),
    val listMergeConfigs: SnapshotStateList<MergeConfigState> = mutableStateListOf()
)


data class MergeConfigState(
    val isEnable: MutableState<Boolean> = mutableStateOf(true),
    val channelApkFile: MutableState<String> = mutableStateOf(""),//sdk文件
    val extraCmd: SnapshotStateList<String> = mutableStateListOf<String>()
)

fun MergeConfig.toState(): MergeConfigState {
    return MergeConfigState(
        isEnable = mutableStateOf(enable),
        channelApkFile = mutableStateOf(channelApkFile),
        extraCmd = extraCmd.toMutableStateList()
    )
}

fun MergeConfigState.toConfig(): MergeConfig {
    return MergeConfig().apply {
        this.enable = this@toConfig.isEnable.value
        this.extraCmd = this@toConfig.extraCmd
        this.channelApkFile = this@toConfig.channelApkFile.value
    }
}

data class ApkConfigState(
    val isUseContent: Boolean = UserPropertiesStore.isShowApkConfig, // ture 只对content内容编辑 , false 对除了content的其他参数编辑
    val content: String = "",
    val dataMap: SnapshotStateMap<String, String> = SnapshotStateMap(),
    val packageName: String = "", //包名
    val appName: String = "",
    val iconFile: String = "",
    val versionCode: String = "",
    val versionName: String = "",
    val minSdkVersion: String = "",
    val targetSdkVersion: String = ""
)


fun ApkConfigState.write(channelName: String) {
    val readApkConfig = readApkConfig(channelName)
    if (isUseContent) {
        var check = runCatching { Gson().fromJson(content, ApkConfig::class.java) }
            .onFailure {
                ErrorTipState.update { it.copy(isShow = true, msg = "ApkConfig.json不是一个json数据") }
            }.getOrNull() ?: ErrorTipState.update { it.copy(isShow = true, msg = "ApkConfig.json 格式错误") }
        writeApkConfigContent(channelName = channelName, content = content)
    } else {
        fun setMetaData(map: MutableMap<String, String>, key: String, value: String) {
            if (value.isEmpty()) {
                map.remove(key)
            } else {
                map[key] = value
            }
        }
        readApkConfig.packageName = packageName
        readApkConfig.iconImgPath = iconFile
        readApkConfig.appName = appName
        readApkConfig.versionCode = versionCode
        readApkConfig.versionName = versionName
        readApkConfig.minSdkVersion = minSdkVersion
        readApkConfig.targetSdkVersion = targetSdkVersion
        val metaDataMap = readApkConfig.metaDataMap
        dataMap.forEach { (key, value) ->
            val s = Constant.getMetaDataKeyMap()[key]
            if (s != null) {
                setMetaData(metaDataMap, s, value)
            }
        }

        writeMergeApkConfig(channelName = channelName, apkConfig = readApkConfig)
    }
}

fun readApkConfigState(channel: String): ApkConfigState {
    val readApkConfigContent = readApkConfigContent(channel)
    val readApkConfig = readApkConfig(channel)
    val metaDataMap = readApkConfig.metaDataMap
    val newMap = Constant.getMetaDataKeyMap().mapValues { metaDataMap[it.value] ?: "" }
    return ApkConfigState(
        content = readApkConfigContent,
        dataMap = SnapshotStateMap<String, String>().apply {
            putAll(newMap)
        },
        packageName = readApkConfig.packageName,
        iconFile = readApkConfig.iconImgPath,
        appName = readApkConfig.appName,
        versionCode = readApkConfig.versionCode,
        versionName = readApkConfig.versionName,
        minSdkVersion = readApkConfig.minSdkVersion,
        targetSdkVersion = readApkConfig.targetSdkVersion
    )
}


fun ChannelConfigItem.setState(state: ChannelConfigState) {
    channelName = state.channel
    describe = state.describe
    apkName = state.apkName
    signConfigFile = state.signConfigFile
    apkConfigFile = Constant.getApkConfigName(state.channel)
    mode = state.mode
    channelApkFile = state.channelApkFile
    extraCmd = state.extraCmd.toList()
    listMergeConfigs = state.listMergeConfigs.map { it.toConfig() }
}

fun ChannelConfigItem.toState(): ChannelConfigState {
    return ChannelConfigState(
        channel = channelName,
        describe = describe,
        apkName = apkName,
        signConfigFile = signConfigFile,
        apkConfigState = readApkConfigState(channelName),
        mode = mode,
        channelApkFile = channelApkFile,
        extraCmd = extraCmd.toMutableStateList(),
        listMergeConfigs = listMergeConfigs.map { it.toState() }.toMutableStateList()
    )
}

@Composable
fun ChannelEditScreenImpl(
    channelConfigState: MutableState<ChannelConfigState>,
    onBack: () -> Unit,
    onSave: (ChannelConfigState) -> Unit
) {

    var rememberScrollState = rememberScrollState()
    Box {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            EditBasis(channelConfigState)

            Spacer(modifier = Modifier.height(16.dp))

            EditApkConfig(channelConfigState)

            Spacer(modifier = Modifier.height(30.dp))
        }
        Row(modifier = Modifier.align(Alignment.BottomStart)) {
            Button(
                onClick = {
                    onBack()
                },
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("返回")
            }
            Button(
                onClick = {

                    onSave(channelConfigState.value)
                },
                modifier = Modifier.padding(start = 10.dp)
            ) {
                Text("保存配置")
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(rememberScrollState)
        )
    }

}

@Composable
private fun EditBasis(channelConfigState: MutableState<ChannelConfigState>) {
    // 基础配置部分
    var channelState by channelConfigState
    Text("${channelState.channel}配置", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    OutlinedTextField(
        value = channelState.describe,
        onValueChange = { channelState = channelState.copy(describe = it) },
        label = { Text("描述") },
        modifier = Modifier.fillMaxWidth()
    )
    OutlinedTextField(
        value = channelState.apkName,
        onValueChange = { channelState = channelState.copy(apkName = it) },
        label = { Text("APK名称") },
        modifier = Modifier.fillMaxWidth()
    )

    SelectSign(channelState.signConfigFile) {
        channelState = channelState.copy(signConfigFile = it)
    }
    SelectMode(channelState.mode) {
        channelState = channelState.copy(mode = it)
    }

    if (channelState.mode == MODE_MERGE || channelState.mode == MODE_MERGE_Reverse) {
        SelectSdk(channelState.channelApkFile) {
            channelState = channelState.copy(channelApkFile = it)
        }
        SelectSdkCmd(channelState.extraCmd)
    }
    if (channelState.mode == MODE_LIST) {
        SelectSDkList(channelState.listMergeConfigs)
    }
}

@Composable
private fun SelectSDkList(listMergeConfigs: SnapshotStateList<MergeConfigState>) {

    listMergeConfigs.forEach {
        EditMergeConfigItem(it, onDelete = {
            listMergeConfigs.remove(it)
        })
    }
    SelectSdk("") {
        listMergeConfigs.add(MergeConfigState(channelApkFile = mutableStateOf(it)))
    }

}

@Composable
private fun EditMergeConfigItem(item: MergeConfigState, onDelete: () -> Unit) {
    Column(
        modifier = Modifier.border(width = 1.dp, color = color333, shape = RoundedCornerShape(5.dp))
            .padding(5.dp)
    ) {

        SelectSdk(item.channelApkFile.value) {
            item.channelApkFile.value = it
        }
        SelectSdkCmd(item.extraCmd)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("是否启用:")
            Switch(item.isEnable.value, onCheckedChange = {
                item.isEnable.value = it
            })

            TextCenter(text = "删除", modifier = Modifier.padding(start = 20.dp).width(150.dp).height(30.dp).clickable {
                onDelete()
            }, color = colorf00)
        }
    }
}

@Composable
fun ChexBoxText(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )

        Text(text)
    }

}

@Composable
private fun SelectSdkCmd(extraCmd: SnapshotStateList<String>) {

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("使用常用配置:", modifier = Modifier.align(Alignment.CenterVertically))
        Button(onClick = {
            extraCmd.clear()
        }){
            Text("默认配置")
        }

        Button(onClick = {
            extraCmd.clear()
            extraCmd.add("-useChannelCode")
            extraCmd.add("-useChannelRes")
        }){
            Text("替换代码和资源")
        }
        Button(onClick = {
            extraCmd.clear()
            extraCmd.add("-keepActivityTheme")
            extraCmd.add("-changeNotRSmali")
            extraCmd.add("-reNameAttr")
            extraCmd.add("-isReNameStyle")
            extraCmd.add("-isRenameRes")
            extraCmd.add("-isRenameClassPackage")
        }){
            Text("合并apk通用配置")
        }


    }
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.Center
    ) {

        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-useChannelCode"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-useChannelCode")
                } else {
                    extraCmd.remove("-useChannelCode")
                }
            }, text = "sdk代码优先"
        )


        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-useChannelRes"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-useChannelRes")
                } else {
                    extraCmd.remove("-useChannelRes")
                }
            }, text = "sdk资源优先"
        )
        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-keepActivityTheme"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-keepActivityTheme")
                } else {
                    extraCmd.remove("-keepActivityTheme")
                }
            }, text = "合并时activity的theme保持不变"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-changeNotRSmali"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-changeNotRSmali")
                } else {
                    extraCmd.remove("-changeNotRSmali")
                }
            }, text = "修改不引用R.class的id值(0x7f开头)"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-reNameAttr"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-reNameAttr")
                } else {
                    extraCmd.remove("-reNameAttr")
                }
            }, text = "attr重命名"
        )
        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-isReNameStyle"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isReNameStyle")
                } else {
                    extraCmd.remove("-isReNameStyle")
                }
            }, text = "style冲突时重命名"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-isRenameRes"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isRenameRes")
                } else {
                    extraCmd.remove("-isRenameRes")
                }
            }, text = "res冲突时重命名(style,attr除外)"
        )


        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-isRenameClassPackage"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isRenameClassPackage")
                } else {
                    extraCmd.remove("-isRenameClassPackage")
                }
            }, text = "class冲突时重命名"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-notUseDefaultKeepClassPackage"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-notUseDefaultKeepClassPackage")
                } else {
                    extraCmd.remove("-notUseDefaultKeepClassPackage")
                }
            }, text = "禁用默认的keepClass规则"
        )
        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-useChannelApktoolYml"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-useChannelApktoolYml")
                } else {
                    extraCmd.remove("-useChannelApktoolYml")
                }
            }, text = "优先使用sdk的ApktoolYml"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 15.dp),
            checked = extraCmd.contains("-replaceApplication"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-replaceApplication")
                } else {
                    extraCmd.remove("-replaceApplication")
                }
            }, text = "替换Application类"
        )

    }
    if (!extraCmd.contains("-useChannelRes")) {
        SelectChannelResCmd(extraCmd)
    }

}

@Composable
private fun SelectChannelResCmd(extraCmd: SnapshotStateList<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        TextCenter(modifier = Modifier.align(Alignment.CenterVertically), "sdk资源优先:")
        ChexBoxText(
            modifier = Modifier.padding(start = 5.dp),
            checked = extraCmd.contains("-isUseChannelFileAssets"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isUseChannelFileAssets")
                } else {
                    extraCmd.remove("-isUseChannelFileAssets")
                }
            }, text = "Assets文件"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 5.dp),
            checked = extraCmd.contains("-isUseChannelFileLib"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isUseChannelFileLib")
                } else {
                    extraCmd.remove("-isUseChannelFileLib")
                }
            }, text = "lib文件"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 5.dp),
            checked = extraCmd.contains("-isUseChannelFileManifest"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isUseChannelFileManifest")
                } else {
                    extraCmd.remove("-isUseChannelFileManifest")
                }
            }, text = "AndroidManifest"
        )

        ChexBoxText(
            modifier = Modifier.padding(start = 5.dp),
            checked = extraCmd.contains("-isUseChannelFileRes"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isUseChannelFileRes")
                } else {
                    extraCmd.remove("-isUseChannelFileRes")
                }
            }, text = "res文件"
        )
        ChexBoxText(
            modifier = Modifier.padding(start = 5.dp),
            checked = extraCmd.contains("-isUseChannelFileOther"),
            onCheckedChange = {
                if (it) {
                    extraCmd.add("-isUseChannelFileOther")
                } else {
                    extraCmd.remove("-isUseChannelFileOther")
                }
            }, text = "其他文件"
        )


    }
}


@Composable
private fun SelectSign(signConfigFile: String, onChange: (String) -> Unit) {
    var showFileDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "签名配置:" + signConfigFile.ifEmpty { "请选择签名配置(必选)" },
            modifier = Modifier.weight(1f), color = getTipTextColor(signConfigFile.isEmpty())
        )
        Button(onClick = { showFileDialog = true }) {
            Text("选择文件")
        }
    }
    if (showFileDialog) {
        val signList = getFileDatas(Constant.signConfigDir)?.map { it.name } ?: emptyList()
        SelectListDialog(
            showDialog = showFileDialog,
            title = "选择签名文件",
            items = signList,
            onDismiss = { showFileDialog = false },
            onConfirm = { index ->
                val selectedFile = "SignConfig/${signList[index]}"
                onChange(selectedFile)
                showFileDialog = false
            }
        )
    }
}

@Composable
private fun SelectSdk(sdk: String, onChange: (String) -> Unit) {
    var showFileDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "sdk文件:" + sdk.ifEmpty { "请选择sdk文件(必选)" },
            modifier = Modifier.weight(1f),
            color = getTipTextColor(sdk.isEmpty())
        )
        Button(onClick = { showFileDialog = true }) {
            Text("选择文件")
        }
    }
    if (showFileDialog) {
        val signList = getFileDatas(Constant.sdkFileDir)?.map { it.name } ?: emptyList()
        SelectListDialog(
            showDialog = showFileDialog,
            title = "选择sdk文件",
            items = signList,
            onDismiss = { showFileDialog = false },
            onConfirm = { index ->
                val selectedFile = "sdk/${signList[index]}"
                onChange(selectedFile)
                showFileDialog = false
            }
        )
    }
}

@Composable
private fun SelectIcon(value: String, onChange: (String) -> Unit) {
    var showFileDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "应用图标:" + value.ifEmpty { "请选择图标文件(可不选)" },
            modifier = Modifier.weight(1f),
            color = color333
        )
        if (value.isNotEmpty()) {
            TextCenter(modifier = Modifier.clickable {
                onChange("")
            }, text = "取消选择", colorf00)
        }
        Button(onClick = { showFileDialog = true }) {
            Text("选择文件")
        }
    }
    if (showFileDialog) {
        val signList = getFileDatas(Constant.apkConfigResDir)?.map { it.name } ?: emptyList()
        SelectListDialog(
            showDialog = showFileDialog,
            title = "请选择图标文件",
            items = signList,
            onDismiss = { showFileDialog = false },
            onConfirm = { index ->
                val selectedFile = "res/${signList[index]}"
                onChange(selectedFile)
                showFileDialog = false
            }
        )
    }
}

@Composable
private fun SelectMode(mode: String, onChange: (String) -> Unit) {
    var showFileDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "打包模式:" + mode.ifEmpty { "请选择打包模式(必选)" },
            modifier = Modifier.weight(1f),
            color = getTipTextColor(mode.isEmpty())
        )
        Button(onClick = { showFileDialog = true }) {
            Text("选择")
        }
    }
    if (showFileDialog) {
        val list = listOf(
            MODE_MERGE to "merge(合并sdk)",
            MODE_MERGE_Reverse to "merge_reverse(sdk作为主包合并)",
            MODE_LIST to "merge_list(多sdk合并)",
            MODE_SIMPLE_Fast to "simple_fast(渠道包快速打包,只支持修改<meta-data/>,包名,版本号等只需要修改AndroidManifest.xml的配置)",
            MODE_SIMPLE to "simple(渠道包完全版,相比simple_fast新增修改icon,appName)",
            MODE_CHANGE to "change(支持修改ApkConfig.json的所有内容)",
            MODE_DECOMPILE to "decompile(反编译后生成zip(不生成apk),支持修改ApkConfig.json的所有内容)"
        )
        SelectListDialog(
            showDialog = showFileDialog,
            title = "选择打包模式",
            items = list.map { it.second },
            onDismiss = { showFileDialog = false },
            onConfirm = { index ->
                onChange(list[index].first)
                showFileDialog = false
            }
        )
    }
}

@Composable
private fun EditApkConfig(channelConfigState: MutableState<ChannelConfigState>) {
    // APK配置部分
    var channelState by channelConfigState
    Text("APK配置", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = channelState.apkConfigState.isUseContent,
            onCheckedChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(isUseContent = it)
                )
                UserPropertiesStore.isShowApkConfig = it
            }
        )
        Text("编辑ApkConfig.json")
    }

    if (channelState.apkConfigState.isUseContent) {
        OutlinedTextField(
            value = channelState.apkConfigState.content,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(content = it)
                )
            },
            label = { Text("Content") },
            modifier = Modifier.fillMaxWidth()
        )
    } else {

        OutlinedTextField(
            value = channelState.apkConfigState.packageName,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(packageName = it)
                )
            },
            label = { Text("包名") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = channelState.apkConfigState.appName,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(appName = it)
                )
            },
            label = { Text("app名称") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = channelState.apkConfigState.versionName,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(versionName = it)
                )
            },
            label = { Text("versionName") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = channelState.apkConfigState.versionCode,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(versionCode = it)
                )
            },
            label = { Text("versionCode") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = channelState.apkConfigState.minSdkVersion,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(minSdkVersion = it)
                )
            },
            label = { Text("minSdkVersion") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = channelState.apkConfigState.targetSdkVersion,
            onValueChange = {
                channelState = channelState.copy(
                    apkConfigState = channelState.apkConfigState.copy(targetSdkVersion = it)
                )
            },
            label = { Text("targetSdkVersion") },
            modifier = Modifier.fillMaxWidth()
        )


        SelectIcon(value = channelState.apkConfigState.iconFile) {
            channelState = channelState.copy(
                apkConfigState = channelState.apkConfigState.copy(iconFile = it)
            )
        }
        channelState.apkConfigState.dataMap.forEach { (name, value) ->
            OutlinedTextField(
                value = value,
                onValueChange = {
                    channelState.apkConfigState.dataMap[name] = it
                },
                label = { Text(name) },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}