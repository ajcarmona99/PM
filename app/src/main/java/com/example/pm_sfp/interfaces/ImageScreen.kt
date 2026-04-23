package com.example.pm_sfp.interfaces

import android.graphics.Bitmap
import android.media.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pm_sfp.imageproc.GrayscaleProcessor
import com.example.pm_sfp.storage.AppFiles
import com.example.pm_sfp.storage.ImageStorage

@Composable
fun ImageScreen(){
    val context = LocalContext.current

    val inputFile = AppFiles.latestPhotoFile(context)

    var fileExits by remember { mutableStateOf(inputFile.exists()) }

    var status by remember { mutableStateOf("Listo") }

    var original by remember { mutableStateOf<Bitmap?>(null) }

    var processed by remember { mutableStateOf<Bitmap?>(null) }

    val gray = remember { GrayscaleProcessor() }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Imagen - Filtro gris")
        Text("Entrada: ${inputFile.name} (${if (fileExits) "existe" else "no existe"})")
        Text("Estado:${status}")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                original = ImageStorage.loadBitmap(inputFile)
                status = "Cargando"
                processed = null
            }) {Text("Cargar fotp") }

            Button(onClick = {
                val src = original
                status = "Procesando(gris)..."
                original = null
                //processed = gray.apply(src)
                status = "Procesada:Gris"
            }) {Text("Gris") }
        }

        Spacer(Modifier.height(8.dp))

        Text("Vista previa:")

        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            original?.let {
                Column(Modifier.weight(1f)) {
                    Text(("Original"))
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            processed?.let {
                Column(Modifier.weight(1f)) {
                    Text(("Original"))
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }


}