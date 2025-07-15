import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import compose.update
import dialog.TipDialog
import screen.MainScreen
import util.RuntimeUtil
import vm.ErrorTipState

fun main() = application(exitProcessOnExit = true) {

    val isShowDialog = remember {
        mutableStateOf(false)
    }
    Window(
        onCloseRequest = {
            isShowDialog.value = true
        },
        title = "" + System.getProperty("user.dir"),
        state= rememberWindowState( size = DpSize(1200.dp, 800.dp),)
    ) {
        if (isShowDialog.value) {
            TipDialog(title = "提示", content = "是否退出应用", confirmClick = {
                RuntimeUtil.destroyAll()
                exitApplication()
                System.exit(0)
            }, onDismissRequest = {
                isShowDialog.value = false
            })
        }
        if (ErrorTipState.value.isShow) {
            TipDialog(title = "错误", content = ErrorTipState.value.msg, confirmClick = {
                ErrorTipState.update { it.copy(isShow = false) }
            }, onDismissRequest = {
                ErrorTipState.update { it.copy(isShow = false) }
            })
        }
        MainScreen()
    }
}

