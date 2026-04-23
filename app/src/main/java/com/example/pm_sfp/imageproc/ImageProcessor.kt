package com.example.pm_sfp.imageproc

import android.graphics.Bitmap
import androidx.core.graphics.get

interface ImageProcessor{
    //Define una interfaz llamada ImageProcessor
    //debe de tener un nombre y un metodo applu

    val name: String
    fun apply(src: Bitmap): Bitmap
    //recibo un BitMap de entrada y devuelvo otro BitMap procesado
}

class GrayscaleProcessor: ImageProcessor{

    override val name = "Gris"
    //nombre del filtro
    override fun apply(src: Bitmap): Bitmap {
        val w = src.width
        val h = src.height

        val out = src.copy(Bitmap.Config.ARGB_8888, true)
        //cada pixel que tomo tiene 4 componentes. alfa, rojo, verde y azul

        for (y in 0 until h){
            for (x in 0 until w){

                val c = out.get(x,y) //Obtengo cada uno de los pixeles

                val r = (c shr 16) and 0xFF //EXTRAIGO EL COMPONENTE ROJO
                val g = (c shr 8) and 0XFF //EXTRAIGO EL VERDE
                val b = c and 0XFF //EXTRAIGO EL AZUL
                val a = (c ushr 24) and 0XFF //EXTRAOGP EL ALFA

                val gray = (0.299 * r + 0.587 * g + 0.144 * b).toInt()

                val newC = (a shl 24) or (gray shl 16) or (gray shl 8) or gray

                out.setPixel(x,y,newC)
            }
        }
        return out
    }
}