package compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun <reified T> MutableState<T>.update(block: (T) -> T) {
    this.value = block(this.value)
}

fun Modifier.onClick(enabled: Boolean = true, onClick: () -> Unit) = composed {
    this.clickable(
        enabled = enabled,
        onClick = onClick,
        indication = null,
        interactionSource = remember { MutableInteractionSource() })
}

