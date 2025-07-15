package screen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import bean.ChannelConfigItem
import dialog.EditDialog
import dialog.LoadingDialog
import dialog.TipDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import theme.color333
import theme.colorSelectText
import theme.coloreee
import theme.colorf00
import util.StringUtil
import vm.ChannelViewModel
import java.io.File


@Composable
fun ChannelScreen(channelViewModel: ChannelViewModel, generateAction: () -> Unit) {

    val channelName = channelViewModel.editChannelNameState.value
    val rememberLazyListState = channelViewModel.lazyListState

    if (channelName.isEmpty()) {
        ChannelListScreen(
            channelViewModel = channelViewModel,
            generateAction = generateAction,
            listState = rememberLazyListState
        )
    } else {
        ChannelEditScreen(channelViewModel = channelViewModel, channel = channelName)
    }
}

@Composable
private fun ChannelEditScreen(channelViewModel: ChannelViewModel, channel: String) {


    LaunchedEffect(channel) {
        channelViewModel.initEditChannelConfigState(channel)
    }
    val rememberCoroutineScope = rememberCoroutineScope()
    if (channel.isNotEmpty()) {
        ChannelEditScreenImpl(channelConfigState = channelViewModel.editChannelConfigState, onBack = {
            channelViewModel.editChannelNameState.value = ""
            channelViewModel.lastEditChannel=""
        }, onSave = { channelConfigState ->
            rememberCoroutineScope.launch {
                channelViewModel.setChannelConfigState(channelConfigState)
                channelViewModel.editChannelNameState.value = ""
                channelViewModel.lastEditChannel=""
            }

        })
    }
}

@Composable
private fun ChannelListScreen(
    channelViewModel: ChannelViewModel,
    generateAction: () -> Unit,
    listState: LazyListState
) {
    LaunchedEffect(Unit) {
        channelViewModel.initChannelConfig()
    }
    Column() {
        ChannelTopView(channelViewModel = channelViewModel, generateAction = generateAction)
        ChannelListContent(channelViewModel = channelViewModel, listState = listState)
    }
}

@Composable
private fun ChannelListContent(channelViewModel: ChannelViewModel, listState: LazyListState) {
    Box(modifier = Modifier.fillMaxSize()) {

        val rememberCoroutineScope = rememberCoroutineScope()



        LazyColumn(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), state = listState) {
            items(count =  channelViewModel.channelListState.size,
                itemContent = {
                    val mutableState =  channelViewModel.channelListState[it]
                    ChannelItemView(mutableState.value, enableChange = { isEnable ->
                        rememberCoroutineScope.launch {
                            channelViewModel.setChannelEnable(mutableState, isEnable)
                        }
                    }, editClick = {
                        channelViewModel.editChannelNameState.value = mutableState.value.name
                    }, deleteClick = {
                        rememberCoroutineScope.launch {
                            channelViewModel.deleteChannel(mutableState)
                        }
                    })
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp).background(color = coloreee))
                })


        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(listState)
        )

        Button(onClick = {
            rememberCoroutineScope.launch (){
                listState.animateScrollToItem(0)
            }
        }, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 50.dp, bottom = 50.dp)
        ){
            Text("返回顶部")
        }

    }
}

@Composable
private fun ChannelItemView(
    item: ChannelListItemState,
    enableChange: (Boolean) -> Unit,
    editClick: () -> Unit,
    deleteClick: () -> Unit
) {
    Column(modifier = Modifier.padding(10.dp)) {
        val isShowDialog = remember { mutableStateOf(false) }
        if (isShowDialog.value) {
            TipDialog("删除渠道", "是否删除渠道${item.name}", confirmClick = {
                deleteClick()
                isShowDialog.value = false
            }, onDismissRequest = {
                isShowDialog.value = false
            })
        }
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "渠道名:" + item.name,
            color = color333,
            textAlign = TextAlign.Left
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "说明:" + item.describe,
            color = color333,
            textAlign = TextAlign.Left
        )


        Text(
            modifier = Modifier.fillMaxWidth(),
            text = "生成apk的名称:" + item.apkName,
            color = color333,
            textAlign = TextAlign.Left
        )


        Row() {
            Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = "是否生成:",
                    color = color333,
                    textAlign = TextAlign.Center
                )
                Switch(item.isEnable, onCheckedChange = enableChange)
            }

            Box(modifier = Modifier.align(Alignment.CenterVertically).width(150.dp).height(50.dp).clickable {
                editClick()
            }) {
                Text(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    text = "编辑",
                    color = colorSelectText,
                    textAlign = TextAlign.Center
                )
            }
            Box(modifier = Modifier.align(Alignment.CenterVertically).width(150.dp).height(50.dp).clickable {
                isShowDialog.value = true
            }) {
                Text(
                    modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                    text = "删除",
                    color = colorf00,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

}


@Composable
private fun ChannelTopView(channelViewModel: ChannelViewModel, generateAction: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {

        val isShowDialog = remember { mutableStateOf(false) }
        val isShowLoading = remember { mutableStateOf(false) }
        val rememberCoroutineScope = rememberCoroutineScope()
        if (isShowDialog.value) {
            EditDialog(title = "添加渠道", hint = "请输入渠道名(不包含中文和特殊符号)", confirmClick = {
                rememberCoroutineScope.launch {
                    isShowDialog.value = false
                    channelViewModel.addChannel(it)
                }

            }, onDismissRequest = {
                isShowDialog.value = false
            }, onFitter = {
                StringUtil.filterInput(it)
            })
        }
        if (isShowLoading.value) {
            LoadingDialog()
        }
        TextField(
            modifier = Modifier.padding(10.dp).fillMaxWidth(), onValueChange = {
                channelViewModel.search(it)
            }, value = channelViewModel.searchKeywords.value,
            placeholder = {
                Text("输入内容搜索")
            }
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                if (channelViewModel.checkChannel()) {
                    generateAction()
                }
            }, content = {
                Text("生成渠道包")
            })
            Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                isShowDialog.value = true
            }, content = {
                Text("添加渠道")
            })

            Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                rememberCoroutineScope.launch {
                    isShowLoading.value = true
                    channelViewModel.outputAllConfig()
                    isShowLoading.value = false
                }
            }, content = {
                Text("导出配置")
            })

            Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                rememberCoroutineScope.launch {
                    isShowLoading.value = true
                    channelViewModel.inputAllConfig()
                    isShowLoading.value = false
                }
            }, content = {
                Text("导入配置")
            })
            ClearAllConfig(rememberCoroutineScope, isShowLoading, channelViewModel)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                rememberCoroutineScope.launch {
                    channelViewModel.setAllChannelEnable(true)
                }

            }, content = {
                Text("全选")
            })
            Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
                rememberCoroutineScope.launch {
                    channelViewModel.setAllChannelEnable(false)
                }
            }, content = {
                Text("全不选")
            })
            Row(modifier = Modifier.padding(horizontal = 10.dp)) {
                Checkbox(channelViewModel.isTopChannel.value, onCheckedChange = {
                    channelViewModel.isTopChannel.value = it
                    channelViewModel.refreshChannelConfig()
                })
                Text(text = "置顶选中", modifier = Modifier.align(Alignment.CenterVertically))
            }

        }
        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp).background(color = coloreee))
    }

}

@Composable
private fun ClearAllConfig(
    rememberCoroutineScope: CoroutineScope,
    isShowLoading: MutableState<Boolean>,
    channelViewModel: ChannelViewModel
) {
    val isDialog = remember { mutableStateOf(false) }
    if (isDialog.value) {
        TipDialog("提示", "是否清空所有的配置", confirmClick = {
            isDialog.value = false
            rememberCoroutineScope.launch(Dispatchers.IO) {
                isShowLoading.value = true
                FileUtils.deleteFile(File(Constant.channelDir))
                channelViewModel.initChannelConfig()
                isShowLoading.value = false
            }
        }, onDismissRequest = {
            isDialog.value = false
        })
    }
    Button(modifier = Modifier.padding(horizontal = 10.dp), onClick = {
        rememberCoroutineScope.launch {
            isDialog.value = true
        }
    }, content = {
        Text("清空所有配置")
    })
}

data class ChannelListItemState(val isEnable: Boolean, val name: String, val describe: String, val apkName: String)

fun ChannelConfigItem.toChannelListItemState(): ChannelListItemState {
    return ChannelListItemState(
        isEnable = this.channelEnable,
        name = channelName,
        describe = describe,
        apkName = apkName
    )
}