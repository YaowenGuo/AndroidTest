package tech.yaowen.rtc_demo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RTCDataChannelScreen(
    sponsorReceiveText: String,
    responderReceiveText: String,
    onSponsorTextChange: (String) -> Unit,
    onResponderTextChange: (String) -> Unit,
    onSponsorSend: () -> Unit,
    onResponderSend: () -> Unit
) {
    var sponsorText by remember { mutableStateOf("") }
    var responderText by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // 发起端
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "发起端",
                fontSize = 20.sp,
                color = Color(0xFFFF0000)
            )
            
            Text(
                text = sponsorReceiveText.ifEmpty { "接收到的内容" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(8.dp),
                fontSize = 20.sp,
                color = Color(0xFFFF0000)
            )
            
            OutlinedTextField(
                value = sponsorText,
                onValueChange = { 
                    sponsorText = it
                    onSponsorTextChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("输入要发送数据，按回车发送") },
                minLines = 1,
                maxLines = 5,
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        if (sponsorText.isNotEmpty()) {
                            onSponsorSend()
                            sponsorText = ""
                        }
                    }
                )
            )
        }
        
        // 响应端
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "响应端",
                fontSize = 20.sp,
                color = Color(0xFFFF0000)
            )
            
            Text(
                text = responderReceiveText.ifEmpty { "接收到的内容" },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(8.dp),
                fontSize = 20.sp,
                color = Color(0xFFFF0000)
            )
            
            OutlinedTextField(
                value = responderText,
                onValueChange = { 
                    responderText = it
                    onResponderTextChange(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("输入要发送数据，按回车发送") },
                minLines = 1,
                maxLines = 5,
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = {
                        if (responderText.isNotEmpty()) {
                            onResponderSend()
                            responderText = ""
                        }
                    }
                )
            )
        }
    }
}
