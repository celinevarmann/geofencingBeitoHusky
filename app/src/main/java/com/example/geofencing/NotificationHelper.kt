package com.example.geofencing

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import java.util.Random


class NotificationHelper(base: Context) : ContextWrapper(base) {

    private val TAG = "NotificationHelper"
    private val PERMISSION_REQUEST_CODE = 1003

    init {
        createChannels()
    }

    var CHANNEL_NAME = "High priority channel"
    var CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME

    private fun createChannels() {
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = "This is the description of the channel."
        notificationChannel.lightColor = Color.RED
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }

    fun sendHighPriorityNotification(title : String, body : String, activityname : Class<*>){
        val intent = Intent(this, activityname)
        val pendingintent = PendingIntent.getActivity(this, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().setSummaryText("Summary").setBigContentTitle(title).bigText(body))
            .setContentIntent(pendingintent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(Random().nextInt(), notification)
    }
}