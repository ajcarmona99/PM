package com.example.pm_sfp.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pm_sfp.media.SimpleAudioPlayer
import com.example.pm_sfp.media.SimpleAudioRecorder
import com.example.pm_sfp.storage.AppFiles

@Composable
fun AudioScreen(){
    val context = LocalContext.current
    val audioFile = remember { AppFiles.audioFile(context) }

    val (hasAudioPerm, requestAudioPerm) = rememberAudioPermission()

    var status by remember { mutableStateOf("Listo") }

    val player = remember { SimpleAudioPlayer() }
    val recoder = remember { SimpleAudioRecorder() }

    DisposableEffect(Unit) {
        onDispose {
            recoder.stop()
            player.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Audio - Grabar y reproducir")
        Text("Permiso microfono: ${if (hasAudioPerm) "OK" else "NO"}")
        Text("Archivo: ${audioFile.name} (${if (audioFile.exists()) "existe" else "no existe"})")
        Text("Estado : {$status}")

        if(!hasAudioPerm){
            Button(onClick = requestAudioPerm) { Text("Pedir permiso de microfono") }
        }else{
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    player.stop()
                    recoder.start(audioFile){status = it}
                    status = "Grabando ..."
                },
                    enabled = !recoder.isRecording()) {
                    Text("REC")
                }

                Button(
                    onClick = {
                        recoder.stop()
                        status = "Grabación guardada"
                    }, enabled = recoder.isRecording()
                ) { Text("STOP REC")}
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = {
                    status = "Preparando..."
                    player.prepareFromFile(
                        file = audioFile,
                        onCompleted = {status = "Terminado"},
                        onError = {status = it}
                    )
                }) {Text("Preparar") }
                Button(onClick = {
                    player.play { status = it }
                    if (status == "Preparado") status = "Reproduciendo..."
                }) {Text("Play") }
                Button(onClick = {player.pause(); status = "Pausado"}) {Text(("Pause")) }
                Button(onClick = {player.stop(); status = "Parado"}) {Text("Stop") }
            }
        }
    }

}