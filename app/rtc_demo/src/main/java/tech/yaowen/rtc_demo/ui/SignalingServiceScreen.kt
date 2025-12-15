package tech.yaowen.rtc_demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SignalingServiceScreen(
    onJoinRoom: (String) -> Unit
) {
    var roomText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = roomText,
                onValueChange = { roomText = it },
                modifier = Modifier.weight(1f),
                label = { Text("房间号") },
                singleLine = true
            )
            Button(
                onClick = { 
                    if (roomText.isNotEmpty()) {
                        onJoinRoom(roomText)
                    }
                }
            ) {
                Text("加入")
            }
        }
    }
}
