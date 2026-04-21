package com.example.spin36.feature.galeria

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.provider.MediaStore

class GaleriaManager(private val context: Context) {

    fun guardarVictoria(
        nombreJugador: String,
        numeroGanador: Int,
        ganancia: Int,
        fecha: String
    ): Boolean {
        val bitmap = crearImagenVictoria(nombreJugador, numeroGanador, ganancia, fecha)
        return guardarEnGaleria(bitmap, "spin36_victoria_${System.currentTimeMillis()}.png")
    }
    //creamos el bitmap
    private fun crearImagenVictoria(
        nombreJugador: String,
        numeroGanador: Int,
        ganancia: Int,
        fecha: String
    ): Bitmap {
        val ancho = 800
        val alto  = 500
        val bmp    = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val pintaFondo = Paint().apply { color = Color.parseColor("#0F5C3A") }
        canvas.drawRect(0f, 0f, ancho.toFloat(), alto.toFloat(), pintaFondo)

        val pintaBorde = Paint().apply {
            color       = Color.parseColor("#C9A227")
            style       = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawRect(16f, 16f, (ancho - 16).toFloat(), (alto - 16).toFloat(), pintaBorde)


        fun texto(size: Float, color: Int, negrita: Boolean = false) = Paint().apply {
            textSize  = size
            this.color = color
            textAlign = Paint.Align.CENTER
            isFakeBoldText = negrita
        }

        val dorado  = Color.parseColor("#C9A227")
        val blanco  = Color.parseColor("#F5F1E8")
        val cx      = ancho / 2f

        canvas.drawText("SPIN 36", cx, 100f, texto(90f, dorado, negrita = true))
        canvas.drawText("¡VICTORIA!", cx, 190f, texto(54f, blanco, negrita = true))
        canvas.drawText(nombreJugador, cx, 265f, texto(40f, blanco))
        canvas.drawText("Número ganador: $numeroGanador", cx, 330f, texto(34f, dorado))
        canvas.drawText("Ganancia: $ganancia monedas", cx, 385f, texto(30f, blanco))
        canvas.drawText(fecha, cx, 460f, texto(22f, blanco))

        return bmp
    }

    //lo guardamos en el dispositivo con MediaStore
    private fun guardarEnGaleria(bitmap: Bitmap, nombre: String): Boolean {
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, nombre)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Spin36")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores
        ) ?: throw IllegalStateException("MediaStore no pudo crear la entrada de imagen")

        context.contentResolver.openOutputStream(uri)?.use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        } ?: throw IllegalStateException("No se pudo abrir el stream de escritura")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            valores.clear()
            valores.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, valores, null, null)
        }

        return true
    }
}
