package com.example.pm_sfp.storage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

object ImageStorage{

    fun loadBitmap(file: File): Bitmap?{
        if(!file.exists()) return null
        return BitmapFactory.decodeFile(file.absolutePath)
    }


}