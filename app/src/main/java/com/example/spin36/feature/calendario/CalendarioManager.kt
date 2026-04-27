package com.example.spin36.feature.calendario

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import java.util.TimeZone

class CalendarioManager(private val context: Context) {

    // Devuelve true si el evento se insertó correctamente, false si no hay calendario o falla
    fun guardarVictoriaEnCalendario(
        nombreJugador: String,
        tipoApuesta: String,
        numeroGanador: Int,
        montoGanado: Int,
        fechaMillis: Long
    ): Boolean {
        val idCalendario = obtenerIdCalendarioPrincipal() ?: return false

        val evento = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID,   idCalendario)
            put(CalendarContract.Events.TITLE,         "¡Victoria en SPIN36! — $nombreJugador")
            put(CalendarContract.Events.DESCRIPTION,   "Apuesta: $tipoApuesta | Número: $numeroGanador | Ganado: $montoGanado monedas")
            put(CalendarContract.Events.DTSTART,       fechaMillis)
            put(CalendarContract.Events.DTEND,         fechaMillis + 30 * 60 * 1000) // duración: 30 minutos
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, evento)
        return uri != null
    }

    // Consulta el Content Provider del calendario y devuelve el ID del primer calendario disponible
    private fun obtenerIdCalendarioPrincipal(): Long? {
        val proyeccion = arrayOf(CalendarContract.Calendars._ID)
        val cursor = context.contentResolver.query(
            CalendarContract.Calendars.CONTENT_URI,
            proyeccion,
            null, null, null
        )
        return cursor?.use {
            if (it.moveToFirst()) it.getLong(0) else null
        }
    }
}