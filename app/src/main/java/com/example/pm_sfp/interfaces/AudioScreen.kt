package com.example.pm_sfp.interfaces


import android.R.attr.onClick
import android.inputmethodservice.Keyboard
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.pm_sfp.media.SimpleAudioPlayer
import com.example.pm_sfp.storage.AppFiles
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp


@Composable
fun AudioScreen(){

    var context = LocalContext.current
    var audioFile = remember { AppFiles.audioFile(context) }
    var status by remember { mutableStateOf("Listo") }
    var player = remember { SimpleAudioPlayer() }

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    Column(horizontalAlignment = Arrangement.spacedBy(8.dp) as Alignment.Horizontal) {
        Button(onClick = {
            status = "Preparando..."
            player.prepareFromFile(
                file = audioFile,
                onCompleted = {status = "Terminado"},
                onError = {msg -> status = msg}
            )
        }) { Text("Preparar")}
        Button(onClick = {
            player.play { status = it }
            if (status == "Preparado") status = "Reproduciendo..."
        }) {Text("Play") }

        Button(
            onClick = {
                player.pause()
                status = "Pausado"
            }
        ) {Text("Pausado") }

        Button(onClick = {
            player.stop()
            status = "Parado"
        }) { Text("Stop") }
    }


}