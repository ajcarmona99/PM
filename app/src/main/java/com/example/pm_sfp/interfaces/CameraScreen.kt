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
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.pm_sfp.storage.AppFiles
import java.io.File

@Composable
fun CameraScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val (hasCamPerm, requestCamPerm) = rememberCameraPermission()

    var status by remember { mutableStateOf("Listo") }
    var lastFileName by remember { mutableStateOf("Ninguna") }

    // ✅ Ref para ImageCapture, accedido por .value para evitar problemas con hilos de CameraX
    val imageCaptureRef = remember { mutableStateOf<ImageCapture?>(null) }

    Log.d("CameraDebug", "Permiso cámara: $hasCamPerm")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cámara - Captura de foto")
        Text("Permiso cámara: ${if (hasCamPerm) "OK" else "NO"}")
        Text("Estado: $status")
        Text("Ultima foto: $lastFileName")

        if (!hasCamPerm) {
            Button(onClick = requestCamPerm) {
                Text("Pedir permiso de la cámara")
            }
            return@Column
        }

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            factory = { ctx ->
                Log.d("CameraDebug", "Inicializando cámara...")

                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    Log.d("CameraDebug", "CameraProvider obtenido")

                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    // ✅ Asignamos al ref directamente
                    imageCaptureRef.value = capture

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
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
            val capture = imageCaptureRef.value
            if (capture == null) {
                status = "Cámara no lista, espera un momento"
                Log.w("CameraDebug", "imageCaptureRef es null al pulsar el botón")
                return@Button
            }

            val file: File = AppFiles.latestPhotoFile(context)

            // ✅ Garantizamos que el directorio existe antes de escribir
            file.parentFile?.mkdirs()

            Log.d("CameraDebug", "Intentando guardar en: ${file.absolutePath}")

            val options = ImageCapture.OutputFileOptions.Builder(file).build()

            status = "Capturando..."

            capture.takePicture(
                options,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        status = "Foto guardada ✓"
                        lastFileName = file.name
                        Log.d("CameraDebug", "Foto guardada en: ${file.absolutePath}, tamaño: ${file.length()} bytes")
                    }

                    override fun onError(exception: ImageCaptureException) {
                        status = "Error: ${exception.message}"
                        Log.e("CameraDebug", "Error al capturar foto", exception)
                    }
                }
            )
        }) { Text("Hacer foto") }
    }
}