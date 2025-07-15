package vm

import androidx.compose.runtime.mutableStateOf

data class ErrorTip(val isShow:Boolean=false,val msg:String)

val ErrorTipState= mutableStateOf(ErrorTip(msg = ""))