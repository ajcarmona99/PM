package com.example.pm_sfp.storage

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppFiles {

    // ✅ Usamos cacheDir en todas las rutas para que CameraX pueda escribir sin problemas
    fun audioFile(context: Context): File =
        File(context.cacheDir, "grabacion.m4a")

    fun latestPhotoFile(context: Context): File =
        File(context.cacheDir, "ultima_foto.jpg")

    fun newPhotoFile(context: Context): File {
        val ts = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(context.cacheDir, "foto_$ts.jpg")
    }

    fun processedPngFile(context: Context): File =
        File(context.cacheDir, "foto_procesada.png")

    fun processedJpgFile(context: Context): File =
        File(context.cacheDir, "foto_procesada.jpg")
}