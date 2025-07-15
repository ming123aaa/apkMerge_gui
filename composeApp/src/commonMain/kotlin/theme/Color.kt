package theme

import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val color305ae4 = Color(0xff305ae4)
val colorMain = Color(0xFF03A9F4)
val colorfff= Color(0xFFFFFFFF)
val colorf00= Color(0xFFFF0000)
val coloreee= Color(0xFFEEEEEE)
val coloraaa= Color(0xFFaaaaaa)
val colorccc= Color(0xFFcccccc)
val color000= Color(0xFF000000)
val color888= Color(0xFF888888)
val color333= Color(0xFF333333)
val colorSelectText=Color(0XFF1E9FFD)

fun getTipTextColor(boolean: Boolean)=if (boolean) colorf00 else color333
