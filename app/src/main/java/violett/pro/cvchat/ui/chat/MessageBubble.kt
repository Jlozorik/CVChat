package violett.pro.cvchat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import violett.pro.cvchat.domain.model.Message

@Composable
fun MessageBubble(message: Message) {
    val gradientBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.00f to MaterialTheme.colorScheme.surfaceContainerLow.copy(.7f),
            5.75f to MaterialTheme.colorScheme.surface,
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    val alignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart
    
    val bubblePaddingValues = if (message.isMine) {
        PaddingValues(start = 64.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
    } else {
        PaddingValues(start = 16.dp, end = 64.dp, top = 4.dp, bottom = 4.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bubblePaddingValues),
        contentAlignment = alignment
    ) {
        Row(
            modifier = Modifier
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = MaterialTheme.colorScheme.surfaceDim.copy(0.7f)
                )
                .clip(RoundedCornerShape(16.dp))
                .background(gradientBrush)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}