package com.example.spin36.feature.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.spin36.R
import com.example.spin36.app.MainActivity
import kotlinx.coroutines.flow.combine


class NotificacionHelper (private val context: Context){
    companion object{
        const val CANAL_ID = "canal_victoria"

        fun crearCanal (context: Context){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
               val canal = NotificationChannel(
                   CANAL_ID,
                   "Victorias",
                   NotificationManager.IMPORTANCE_DEFAULT
               )
                val manager = context.getSystemService(NotificationManager::class.java)
                manager?.createNotificationChannel(canal)
            }
        }
    }
    fun mostrarNotificacionVictoria (tiempoResolucionMS : Long){
        val tiempoSegundos = tiempoResolucionMS / 1000.0
        val tiempoTexto = String.format("%.2f", tiempoSegundos)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0 , intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.mipmap.logo_launcher)
            .setContentTitle("¡Victoria!")
            .setContentText("Tiempo de resolucion: $tiempoTexto s")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1,notificacion)
    }

}