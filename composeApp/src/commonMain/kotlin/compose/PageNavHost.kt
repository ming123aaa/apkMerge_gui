package com.ohuang.test_compose.composeView


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


/**
val controller= rememberSaveable() {
PageNavHostController()
}
PageNavHost(controller =controller, defaultKey ="CanvasPage"){
add("CanvasPage"){ CanvasPage()}
add("ImagePage"){ ImagePage()}
add("TextPage"){ TextPage() }
}
controller.navigation("CanvasPage") //use the fun to  replace   page
 *
 */
class PageNavHostController() {
    private var state = mutableStateOf<String>("")
    private var key: String = ""

    private var map = HashMap<String, (@Composable() () -> Unit)>()


    private var pageNavHostManager = PageNavHostManager()


    @Composable
    fun CreatePageNavHost(defaultKey: String, scope: (PageNavHostManager) -> Unit = {}) {
        key = defaultKey
        scope(pageNavHostManager)
        PageNavHostImp()
    }

    @Composable
    private fun PageNavHostImp() {
        if (state.value.isEmpty()) {
            state.value = key
        } else {
            map[state.value]?.invoke()
        }
    }

    private fun remove(key: String) {
        map.remove(key)
    }

    private fun removeAll() {
        map.clear()
    }

    inner class PageNavHostManager {
        /**
         * removeCallBack() 启动结束回调  true为启动 false为关闭
         */
        fun add(
            key: String,
            content: (@Composable() () -> Unit)
        ) {
            map[key] = content
        }

    }

    fun navigation(key: String) {
        state.value = key
    }
}

@Composable
fun rememberPageNavHostController(): PageNavHostController {
    return remember { PageNavHostController() }
}

@Composable
fun PageNavHost(
    controller: PageNavHostController = rememberPageNavHostController(),
    defaultKey: String,
    builder: PageNavHostController.PageNavHostManager.() -> Unit = {}
) {
    controller.CreatePageNavHost(defaultKey, builder)
}