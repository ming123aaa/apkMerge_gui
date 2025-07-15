package screen

import Constant
import FileUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import theme.color333
import theme.colorSelectText
import theme.coloreee
import theme.colorfff
import compose.update
import dialog.LoadingDialog
import dialog.TipDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import util.RuntimeUtil
import vm.ChannelViewModel
import vm.ErrorTipState
import vm.MainViewModel
import java.io.File

@Composable
fun HomeScreen(mainViewModel: MainViewModel) {
    Row(modifier = Modifier.fillMaxSize().background(coloreee).padding(vertical = 20.dp, horizontal = 10.dp)) {

        val routerState = remember { mutableStateOf<HomeRouterIntent>(HomeRouterIntent.ChannelList) }
        NavigationHome(modifier = Modifier,
            select = routerState.value,
            action = {
                routerState.value = it
            }
        )

        HomeContent(
            modifier = Modifier.padding(start = 10.dp),
            select = routerState.value, mainViewModel = mainViewModel
        )


    }
}

@Composable
private fun HomeContent(
    modifier: Modifier = Modifier,
    select: HomeRouterIntent, mainViewModel: MainViewModel
) {
    val channelViewModel = remember { ChannelViewModel() }

    Box(modifier = modifier.fillMaxSize().background(colorfff)) {

        when (select) {
            is HomeRouterIntent.ChannelList -> {
                ChannelScreen(channelViewModel=channelViewModel,generateAction = {//跳转生成apk界面
                    mainViewModel.pageState.value = Page.Generate
                })
            }


            is HomeRouterIntent.SignFileList -> {
                FileScreen(Constant.signFileDir)
            }

            is HomeRouterIntent.SignConfig -> {
                SignScreen()
            }

            is HomeRouterIntent.SdkList -> {
                FileScreen(Constant.sdkFileDir)
            }
            is HomeRouterIntent.ApkConfigRes->{
                FileScreen(Constant.apkConfigResDir)
            }
        }

    }


}

@Composable
private fun NavigationHome(
    modifier: Modifier = Modifier,
    select: HomeRouterIntent,
    action: (HomeRouterIntent) -> Unit
) {

    val isShowLoading = remember { mutableStateOf(false) }
    val rememberCoroutineScope = rememberCoroutineScope()
    if (isShowLoading.value) {
        LoadingDialog()
    }
    Column(
        modifier = modifier.width(150.dp)
            .background(colorfff, RoundedCornerShape(5.dp))
            .padding(10.dp)
    ) {
        NavigationHomeItem(isSelect = select is HomeRouterIntent.ChannelList, "渠道列表") {
            action(HomeRouterIntent.ChannelList)
        }

        NavigationHomeItem(isSelect = select is HomeRouterIntent.SignFileList, "签名文件") {
            action(HomeRouterIntent.SignFileList)
        }
        NavigationHomeItem(isSelect = select is HomeRouterIntent.SignConfig, "签名配置") {
            action(HomeRouterIntent.SignConfig)
        }
        NavigationHomeItem(isSelect = select is HomeRouterIntent.SdkList, "sdk列表") {
            action(HomeRouterIntent.SdkList)
        }
        NavigationHomeItem(isSelect = select is HomeRouterIntent.ApkConfigRes, "资源文件") {
            action(HomeRouterIntent.ApkConfigRes)
        }



        NavigationHomeItem(isSelect = false, "清理缓存") {
            rememberCoroutineScope.launch (Dispatchers.IO){
                isShowLoading.value=true
                FileUtils.deleteFile(Constant.baseApk)
                FileUtils.deleteFile(Constant.outputDir)
                FileUtils.deleteFile(Constant.oldChannelDir)
                FileUtils.deleteFile(Constant.channelDir+"/build")
                FileUtils.deleteFile(Constant.channelDir+"/buildOutApk")
                isShowLoading.value=false

            }
        }

    }
}


@Composable
private fun NavigationHomeItem(isSelect: Boolean, text: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(40.dp).clickable {
        onClick()
    }) {
        Text(
            modifier = Modifier.fillMaxWidth().align(Alignment.Center),
            text = text,
            color = if (isSelect) colorSelectText else color333,
            textAlign = TextAlign.Center
        )
    }

}


sealed class HomeRouterIntent() {

    object ChannelList : HomeRouterIntent() //渠道列表


    object SignFileList : HomeRouterIntent() //签名文件列表

    object SignConfig : HomeRouterIntent() //签名配置

    object SdkList : HomeRouterIntent() //sdk列表
    object ApkConfigRes : HomeRouterIntent() //sdk列表


}
