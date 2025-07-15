package screen

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import vm.MainViewModel
import java.awt.Scrollbar

@Composable
fun MainScreen() {
    val mainViewModel=remember { MainViewModel() }
    MaterialTheme {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            when(mainViewModel.pageState.value){
                is Page.Home->{
                    HomeScreen(mainViewModel = mainViewModel)
                }

                is Page.Generate ->{
                    GenerateScreen {
                        mainViewModel.pageState.value = Page.Home
                    }
                }
            }
        }
    }
}

sealed class Page{

    object Home:Page() //主界面

    object Generate:Page() //生成apk界面
}



