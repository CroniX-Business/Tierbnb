package com.ruki.tierbnb.components

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.ruki.tierbnb.R
import com.ruki.tierbnb.models.Car
import kotlin.random.Random

class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "notification_channel_id"

    fun showSimpleNotification(
        car: Car,
        firstDateSelected: String,
        lastDateSelected: String,
        price: String
    ) {
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle("Auto Rezervacija")
            .setContentText("Zatražena rezervacija ${car.type} ${car.name}")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Početak: ${firstDateSelected}\nKraj: ${lastDateSelected}\nUkupna cijena: ${price}€"))
            .setSmallIcon(R.drawable.tierbnb_logo)
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(Random.nextInt(), notification)
    }
}