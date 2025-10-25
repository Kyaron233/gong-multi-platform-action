package com.sky31.gongmultiplatform.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context

abstract class BaseAlarmQueue {
    private val alarmList = mutableListOf<PendingIntent>()

    fun addAlarm(alarm: PendingIntent) {
        alarmList.add(alarm)
    }

    fun cancelAll() {
        val alarmManager = ContextProvider.context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for(pendingIntent in alarmList) {
            alarmManager.cancel(pendingIntent)
        }

        alarmList.clear()
    }
}