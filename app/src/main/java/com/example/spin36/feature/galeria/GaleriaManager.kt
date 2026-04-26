package com.example.spin36.feature.galeria

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.res.ResourcesCompat
import com.example.spin36.R
import java.io.OutputStream

class GaleriaManager(private val context: Context) {

    fun crearBitmapVictoria(
        nombreJugador: String,
        numeroGanador: Int,
        fecha: String,
        tipoApuesta: String,
        cantidadApostada: Int,
        montoGanado: Int
    ): Bitmap {
        val ancho      = 800
        val alto       = 620
        val tipografia = ResourcesCompat.getFont(context, R.font.mileast) ?: Typeface.DEFAULT
        val bmp        = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val pintaFondo = Paint().apply { color = Color.parseColor("#0F5C3A") }
        canvas.drawRect(0f, 0f, ancho.toFloat(), alto.toFloat(), pintaFondo)

        val pintaBorde = Paint().apply {
            color       = Color.parseColor("#C9A227")
            style       = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawRect(16f, 16f, (ancho - 16).toFloat(), (alto - 16).toFloat(), pintaBorde)

        fun texto(size: Float, color: Int) = Paint().apply {
            textSize   = size
            this.color = color
            textAlign  = Paint.Align.CENTER
            typeface   = tipografia
        }

        val dorado = Color.parseColor("#C9A227")
        val blanco = Color.parseColor("#F5F1E8")
        val cx     = ancho / 2f

        canvas.drawText("SPIN 36",                          cx,  90f, texto(90f, dorado))
        canvas.drawText("¡VICTORIA!",                       cx, 175f, texto(54f, blanco))
        canvas.drawText(nombreJugador,                      cx, 245f, texto(40f, blanco))
        canvas.drawText("Número ganador: $numeroGanador",   cx, 305f, texto(34f, dorado))
        canvas.drawText("Apuesta: $tipoApuesta",            cx, 365f, texto(30f, blanco))
        canvas.drawText("Cantidad apostada: $cantidadApostada monedas", cx, 415f, texto(30f, blanco))
        canvas.drawText("Monto ganado: $montoGanado monedas",           cx, 465f, texto(30f, dorado))
        canvas.drawText(fecha,                              cx, 535f, texto(22f, blanco))

        return bmp
    }

    fun guardarBitmapEnUri(bitmap: Bitmap, uri: Uri): Boolean {
        val stream: OutputStream = context.contentResolver.openOutputStream(uri)
            ?: return false
        return stream.use {
            bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, it)
        }
    }
}
