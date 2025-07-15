package vm

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import screen.Page

@Stable
class MainViewModel {

    val pageState=mutableStateOf<Page>(Page.Home)


}