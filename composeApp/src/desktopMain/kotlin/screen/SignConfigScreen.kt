package screen

import Constant
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bean.SignConfig
import bean.readSignConfig
import bean.writeSignConfig
import dialog.SelectListDialog
import theme.color333
import theme.colorf00
import theme.getTipTextColor

data class SignConfigState(
    val signFileName: String,
    val alias: String,
    val passWord: String,
    val keyPassWord: String,
    val signVersion: String
)

fun SignConfig.toState(): SignConfigState {
    return SignConfigState(
        signFileName = signFileName,
        alias = alias,
        passWord = passWord,
        keyPassWord = keyPassWord,
        signVersion = signVersion
    )
}

fun SignConfigState.toConfig(): SignConfig {
    return SignConfig().also {
        it.signFileName = signFileName
        it.alias = alias
        it.passWord = passWord
        it.keyPassWord = keyPassWord
        it.signVersion = signVersion


    }
}

@Composable
fun SignConfigScreen(path: String, onBack: () -> Unit) {
    var signConfigState by remember(path) {
        mutableStateOf(
            readSignConfig(path).toState()
        )
    }



    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectSginFile(signConfigState, onChange = {
            signConfigState = it
        })

        OutlinedTextField(
            value = signConfigState.alias,
            onValueChange = { signConfigState = signConfigState.copy(alias = it) },
            label = { Text("别名") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = signConfigState.passWord,
            onValueChange = { signConfigState = signConfigState.copy(passWord = it) },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = signConfigState.keyPassWord,
            onValueChange = { signConfigState = signConfigState.copy(keyPassWord = it) },
            label = { Text("keyPassWord(若和密码相同可不填):") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = signConfigState.signVersion,
            onValueChange = { signConfigState = signConfigState.copy(signVersion = it) },
            label = { Text("签名版本(v1,v2,v3)") },
            modifier = Modifier.fillMaxWidth()
        )
        Row {
            Button(
                onClick = {
                    onBack()
                },
                modifier = Modifier.padding(top = 16.dp, end = 10.dp)
            ) {
                Text("返回")
            }
            Button(
                onClick = {
                    // 这里可以处理生成新的配置数据
                    val newConfig = signConfigState.toConfig()
                    writeSignConfig(path = path, newConfig)
                    onBack()
                },
                modifier = Modifier.padding(top = 16.dp, start = 10.dp)
            ) {
                Text("保存配置")
            }
        }


    }
}

@Composable
private fun SelectSginFile(signConfigState: SignConfigState, onChange: (SignConfigState) -> Unit) {

    var showFileDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var content = ""
        var isTipTextColor = false

        if (signConfigState.signFileName.isNotEmpty()) {
            val signList = getFileDatas(Constant.signFileDir)?.map { "Sign/" + it.name } ?: emptyList()
            if (signList.contains(signConfigState.signFileName)) {
                isTipTextColor = false
                content = signConfigState.signFileName
            } else {
                content = signConfigState.signFileName + "(文件不存在)"
                isTipTextColor = true
            }
        } else {
            isTipTextColor = true
            content = "请选择签名文件(必选)"
        }
        Text(
            text = "签名文件:$content",
            modifier = Modifier.weight(1f),
            color = getTipTextColor(isTipTextColor)
        )
        Button(onClick = { showFileDialog = true }) {
            Text("选择文件")
        }
    }
    if (showFileDialog) {
        val signList = getFileDatas(Constant.signFileDir)?.map { it.name } ?: emptyList()
        SelectListDialog(
            showDialog = showFileDialog,
            title = "选择签名文件",
            items = signList,
            onDismiss = { showFileDialog = false },
            onConfirm = { index ->
                val selectedFile = "Sign/${signList[index]}"
                onChange(signConfigState.copy(signFileName = selectedFile))
                showFileDialog = false
            }
        )
    }

}