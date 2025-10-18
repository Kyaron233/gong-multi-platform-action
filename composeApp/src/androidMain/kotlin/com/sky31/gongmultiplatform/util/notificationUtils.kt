package com.sky31.gongmultiplatform.util


import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.sky31.gongmultiplatform.worker.DailyScheduleWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

actual fun doCourseReminderWork() {
    val context = ContextProvider.context
    val wm = WorkManager.getInstance(context)

    // 移除之前设定的work
    wm.cancelUniqueWork("daily_courses_schedule_work")
    wm.cancelUniqueWork("immediate_courses_schedule_work")

    val immediateWork = OneTimeWorkRequestBuilder<DailyScheduleWorker>()
        .build()

    wm.enqueueUniqueWork(
        "immediate_courses_schedule_work",
        ExistingWorkPolicy.REPLACE,
        immediateWork
    )

    val dailyWork = PeriodicWorkRequestBuilder<DailyScheduleWorker>(1, TimeUnit.DAYS)
        .setInitialDelay(getDelayUntilNextMidnight(), TimeUnit.MILLISECONDS)
        .build()

    wm.enqueueUniquePeriodicWork(
        "daily_courses_schedule_work",
        ExistingPeriodicWorkPolicy.UPDATE,
        dailyWork
    )
}

fun getDelayUntilNextMidnight(): Long {
    val now = Calendar.getInstance()
    val nextMidnight = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return nextMidnight.timeInMillis - now.timeInMillis
}