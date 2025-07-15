package screen

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
import theme.coloreee
import theme.colorf00
import compose.TextCenter
import dialog.LoadingDialog
import dialog.TipDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import util.FileSelect
import java.io.File

@Composable
fun FileScreen(filePath: String) {
    val fileDatas = remember(filePath) { mutableStateOf<List<FileData>>(emptyList()) }
    val loadingDialog = remember {
        mutableStateOf(false)
    }
    LaunchedEffect(filePath) {
        getFileDatas(filePath)?.let {
            fileDatas.value = it
        }
    }
    val rememberCoroutineScope = rememberCoroutineScope()
    if (loadingDialog.value) {
        LoadingDialog { }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row {
            Button(onClick = {
                rememberCoroutineScope.launch(Dispatchers.IO) {
                    val file = FileSelect.selectFile()
                    if (file != null) {
                        loadingDialog.value = true
                        file.copyTo(File("$filePath/${file.name}"), overwrite = true)
                        getFileDatas(filePath)?.let {
                            fileDatas.value = it
                        }
                        loadingDialog.value = false
                    }
                }

            },
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                content = {
                    Text("添加")
                })
            Button(onClick = {
                FileSelect.openFile(filePath.replace("/","\\"))
            },
                modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp),
                content = {
                    Text("查看文件")
                })
            Text("文件路径:${filePath}", modifier = Modifier.align(Alignment.CenterVertically))
        }

        Spacer(modifier = Modifier.fillMaxWidth().height(10.dp).background(color = coloreee))

        FileList(fileDatas.value) {
            loadingDialog.value = true
            FileUtils.deleteFile(File(it))
            getFileDatas(filePath)?.let {
                fileDatas.value = it
            }
            loadingDialog.value = false
        }
    }
}

 fun getFileDatas(filePath: String) =
    File(filePath).listFiles()?.filter { it.isFile }?.map { it.toFileData() }

@Composable
private fun FileList(data: List<FileData>, deleteCall: (String) -> Unit) {
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


class FileData(val name: String, val absolutePath: String)

fun File.toFileData(): FileData {
    return FileData(name = name, absolutePath = absolutePath)
}