package com.example.spin36.feature.calendario

import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import java.util.TimeZone

class CalendarioManager(private val context: Context) {

    fun guardarVictoriaEnCalendario(
        nombreJugador: String,
        tipoApuesta: String,
        numeroGanador: Int,
        montoGanado: Int,
        fechaMillis: Long
    ): Boolean {
        // Intento 1: inserción silenciosa si hay calendarios configurados en el dispositivo
        val idCalendario = obtenerIdCalendarioPrincipal()
        if (idCalendario != null) {
            val evento = ContentValues().apply {
                put(CalendarContract.Events.CALENDAR_ID,    idCalendario)
                put(CalendarContract.Events.TITLE,          "¡Victoria en SPIN36! — $nombreJugador")
                put(CalendarContract.Events.DESCRIPTION,    "Apuesta: $tipoApuesta | Número: $numeroGanador | Ganado: $montoGanado monedas")
                put(CalendarContract.Events.DTSTART,        fechaMillis)
                put(CalendarContract.Events.DTEND,          fechaMillis + 30 * 60 * 1000)
                put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            }
            try {
                val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, evento)
                if (uri != null) {
                    Log.d(TAG, "Evento guardado silenciosamente: $uri")
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Insert silencioso falló: ${e.message}")
            }
        }

        // Intento 2 (fallback para emulador / sin cuenta Google configurada):
        // abre la app de Calendario con el evento pre-rellenado
        return try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE,       "¡Victoria en SPIN36! — $nombreJugador")
                putExtra(CalendarContract.Events.DESCRIPTION, "Apuesta: $tipoApuesta | Número: $numeroGanador | Ganado: $montoGanado monedas")
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, fechaMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME,   fechaMillis + 30 * 60 * 1000)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            Log.d(TAG, "Evento abierto en app de Calendario via Intent")
            true
        } catch (e: ActivityNotFoundException) {
            Log.e(TAG, "No hay app de calendario instalada", e)
            false
        }
    }

    private fun obtenerIdCalendarioPrincipal(): Long? {
        return try {
            val cursor = context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                arrayOf(CalendarContract.Calendars._ID),
                "${CalendarContract.Calendars.VISIBLE} = 1 AND " +
                    "${CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL} >= " +
                    "${CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR}",
                null,
                null
            )
            cursor?.use { if (it.moveToFirst()) it.getLong(0) else null }
        } catch (e: Exception) {
            Log.w(TAG, "Error buscando calendario: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "CalendarioManager"
    }
}
