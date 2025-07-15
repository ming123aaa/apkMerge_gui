package dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.graphics.Color
import theme.color333
import theme.colorfff

@Composable
fun TipDialog(
    title: String,
    content: String,
    btnString: String = "确定",
    cancelString: String = "取消",
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    ),
    confirmClick: () -> Unit,
    onDismissRequest: () -> Unit
) {

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        content = {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .background(colorfff, shape = RoundedCornerShape(8.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title, fontSize = 16.sp, color = color333
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp, max = 300.dp)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text(
                            text = content,
                            fontSize = 14.sp,
                            color = color333,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            textAlign = TextAlign.Center
                        )
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentHeight()
                ) {
                    Button(modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp), onClick = {
                        confirmClick()
                    }) {
                        Text(text = btnString, fontSize = 16.sp, color = colorfff)
                    }
                    Button(modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp), onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = cancelString, fontSize = 16.sp, color = colorfff)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    )
}

@Composable
fun EditDialog(
    title: String,
    hint: String,
    btnString: String = "确定",
    cancelString: String = "取消",
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    ),
    confirmClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onFitter:(String)->String={ it}
) {

    val string = remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        content = {
            Column(
                modifier = Modifier
                    .width(400.dp)
                    .background(colorfff, shape = RoundedCornerShape(8.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title, fontSize = 16.sp, color = color333
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 50.dp, max = 300.dp)
                        .padding(horizontal = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        TextField(
                            value = string.value,
                            onValueChange = {
                                string.value = onFitter(it)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            placeholder = {
                                Text(hint)
                            }
                        )
                    }

                }
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .width(250.dp)
                        .wrapContentHeight()
                ) {
                    Button(modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 10.dp), onClick = {
                        confirmClick(string.value)
                    }) {
                        Text(text = btnString, fontSize = 16.sp, color = colorfff)
                    }
                    Button(modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 10.dp), onClick = {
                        onDismissRequest()
                    }) {
                        Text(text = cancelString, fontSize = 16.sp, color = colorfff)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    )
}

@Composable
fun LoadingDialog(onDismissRequest: () -> Unit={}){
    Dialog(onDismissRequest = onDismissRequest) {
            Column(
                modifier = Modifier
                    .background(colorfff, shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .size(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Color.Blue)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "加载中...")
            }
        }

}