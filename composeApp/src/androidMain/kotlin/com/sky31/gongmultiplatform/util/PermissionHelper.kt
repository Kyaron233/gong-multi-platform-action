package com.sky31.gongmultiplatform.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

class PermissionHelper(
    private val activity: ComponentActivity
) {
    private val launcher = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        callback?.invoke(granted)
    }

    private var callback: ((Boolean) -> Unit)? = null

    fun askPostNotificationPermission(onResult: (Boolean) -> Unit) {
        val hasPermission = hasPostNotificationPermission()

        if(!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            callback = {granted ->
                onResult(granted)

                if(granted) {
                    val channelId = "course_channel"
                    val channelName = "课程提醒"
                    val channelDesc = "每天上课前的提醒通知"

                    val importance = NotificationManager.IMPORTANCE_HIGH
                    val channel = NotificationChannel(channelId, channelName, importance).apply {
                        description = channelDesc
                    }

                    val manager = activity.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    manager.createNotificationChannel(channel)
                }
            }

            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}