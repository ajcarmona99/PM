package com.example.pm_sfp.interfaces

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pm_sfp.storage.AppFiles
import java.io.File

@Composable //Esto lo hago siempre para decir que esto es una interfaz
fun CameraScreen(){
    //Contexto Android (para archivos, sistema, etc)
    val context = LocalContext.current

    //Lifecycle necesario para CameraX(dependencia que nosotros instalamos en las
    //primeras sesiones) - gestionar abrir/cerrar la camara de mi dispositivo
    val lifecycleOwner = LocalLifecycleOwner.current

    //Permiso de la camara
    val (hasCamPerm, requestCamPerm) = rememberCameraPermission()

    //Estado de la UI(texto que muestra dentro de la interfaz)
    var status by remember { mutableStateOf("Listo") }

    //Nombre de la ultima foto guardada
    var lastFileName by remember { mutableStateOf("Ninguna") }

    //Creo un objeto que have fotos(ImageCapture)
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    //Log inicial
    Log.d("CameraDebug", "Permiso cámara: $hasCamPerm")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //Informacion dentro de la pantalla
        Text("Cámara - Captura de foto")
        Text("Permiso cámara: ${if (hasCamPerm) "OK" else "NO"}")
        Text("Estado: $status")
        Text("Ultima foto: $lastFileName")

        //Si no hay permiso, mostramos boton y SALIMOS
        if (!hasCamPerm){
            Button(onClick = requestCamPerm) {
                Text("Pedir permiso de la cámara")
            }
            return@Column //Importante: no se siga ejecutando la camara
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),

            factory = { ctx ->

                Log.d("CameraDebug", "Inicializando cámara...")

                // 📺 Vista donde se verá la cámara
                val previewView = PreviewView(ctx)

                // 📷 Obtenemos el proveedor de cámara (async)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({

                    Log.d("CameraDebug", "CameraProvider obtenido")

                    // 📡 Accedemos al provider
                    val cameraProvider = cameraProviderFuture.get()

                    // 🎥 Caso de uso: PREVIEW (lo que vemos en pantalla)
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    // 📸 Caso de uso: CAPTURA DE FOTO
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    // 💾 Guardamos el objeto para usarlo en el botón
                    imageCapture = capture

                    try {
                        // 🔄 Limpiamos antes de volver a enlazar
                        cameraProvider.unbindAll()

                        // 🔗 Enlazamos cámara + preview + captura al lifecycle
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA, // cámara trasera
                            preview,
                            capture
                        )

                        status = "Preview OK"
                        Log.d("CameraDebug", "Cámara enlazada correctamente")

                    } catch (e: Exception) {
                        status = "Error cámara: ${e.message}"
                        Log.e("CameraDebug", "Error al hacer bind", e)
                    }

                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )


        Button(onClick = {
           val capture = imageCapture
           if (capture == null){
               status = "Image capture no listo"
               return@Button
           }

           //Crear el archivo donde se guarda la foto
           val file: File = AppFiles.latestPhotoFile(context)

           //Opciones de salida
           val options = ImageCapture.OutputFileOptions.Builder(file).build()

           status = "Capturando"

           //Hago la foto
           capture.takePicture(
               options,
               ContextCompat.getMainExecutor(context),
               //resultado de la foto
               object : ImageCapture.OnImageSavedCallback{
                   //Exito
                   override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                       status = "Foto guardada"
                       lastFileName = file.name
                   }

                   override fun onError(exception: ImageCaptureException) {
                       status = "Error capturando: ${exception.message}"
                   }
               }
           )
       }) { Text("Hacer foto") }
    }
}