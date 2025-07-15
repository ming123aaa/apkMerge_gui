package screen

import Constant
import FileUtils
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import theme.colorSelectText
import theme.coloreee
import theme.colorf00
import compose.TextCenter
import dialog.EditDialog
import dialog.TipDialog
import util.FileSelect
import util.StringUtil
import java.io.File
import java.util.regex.Pattern

@Composable
fun SignScreen() {
    val signConfigPath = remember { mutableStateOf("") }
    if (signConfigPath.value.isEmpty()) {
        SignFileScreen(onEdit = {
            signConfigPath.value = it
        })
    } else {
        SignConfigScreen(signConfigPath.value, onBack = {
            signConfigPath.value = ""
        })
    }
}



@Composable
private fun SignFileScreen(onEdit: (String) -> Unit) {
    val filePath = Constant.signConfigDir
    val fileDatas = remember { mutableStateOf<List<FileData>>(emptyList()) }
    val isAddDialog = remember { mutableStateOf(false) }
    if (isAddDialog.value) {
        EditDialog("添加签名配置", "签名配置名称(不支持中文和符号)", confirmClick = {
            val signConfigPath = Constant.getSignConfigPath(it)
            onEdit(signConfigPath)
            isAddDialog.value = false
        }, onDismissRequest = {
            isAddDialog.value = false
        }, onFitter = {
            StringUtil.filterInput(it)
        })
    }
    LaunchedEffect(Unit) {
        getFileDatas(filePath)?.let {
            fileDatas.value = it
        }
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            Button(onClick = {
                isAddDialog.value = true
            },
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                content = {
                    Text("添加")
                })
            Button(onClick = {
                FileSelect.openFile(Constant.signConfigDir.replace("/","\\"))
            },
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                content = {
                    Text("查看文件")
                })
        }

        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp).background(color = coloreee))
        SignFileList(data = fileDatas.value, deleteCall = {
            FileUtils.deleteFile(File(it))
            getFileDatas(filePath)?.let {
                fileDatas.value = it
            }
        }, editCall = onEdit)
    }
}


@Composable
private fun SignFileList(data: List<FileData>, deleteCall: (String) -> Unit, editCall: (String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val rememberLazyListState = rememberLazyListState()
        val deleteDialogMsg = remember { mutableStateOf("") }

        if (deleteDialogMsg.value.isNotEmpty()) {
            TipDialog(title = "刪除", content = "是否刪除${File(deleteDialogMsg.value).name}?", confirmClick = {
                deleteCall(deleteDialogMsg.value)
                deleteDialogMsg.value = ""
            }, onDismissRequest = {
                deleteDialogMsg.value = ""
            })

        }

        LazyColumn(modifier = Modifier.fillMaxWidth().align(Alignment.TopStart), state = rememberLazyListState) {
            items(count = data.size,

                itemContent = {
                    val fileData = data[it]
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(modifier = Modifier.weight(weight = 1f), text = "文件名:${fileData.name}")
                        TextCenter(
                            modifier = Modifier.width(120.dp).height(40.dp).clickable {
                                editCall(fileData.absolutePath)
                            },
                            text = "编辑",
                            color = colorSelectText
                        )
                        TextCenter(
                            modifier = Modifier.width(120.dp).height(40.dp).clickable {
                                deleteDialogMsg.value = fileData.absolutePath
                            },
                            text = "刪除",
                            color = colorf00
                        )
                    }
                    Spacer(modifier = Modifier.fillMaxWidth().height(10.dp).background(color = coloreee))
                })


        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd),
            adapter = rememberScrollbarAdapter(rememberLazyListState)
        )
    }
}