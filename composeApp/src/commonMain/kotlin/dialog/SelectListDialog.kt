package dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SelectListDialog(
    showDialog: Boolean,
    title: String,
    items: List<String>,
    selectedIndex: Int = 0,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    if (showDialog) {
        var currentSelectedIndex by remember { mutableStateOf(selectedIndex) }
        
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = {
                LazyColumn {
                    items(items.size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { currentSelectedIndex = index }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentSelectedIndex == index,
                                onClick = { currentSelectedIndex = index }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(items[index])
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (items.isNotEmpty()) {
                        onConfirm(currentSelectedIndex)
                    }else{
                        onDismiss()
                    }
                }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("取消")
                }
            }
        )
    }
}
