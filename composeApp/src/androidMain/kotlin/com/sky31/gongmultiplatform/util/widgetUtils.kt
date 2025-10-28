package com.sky31.gongmultiplatform.util

import androidx.glance.appwidget.GlanceAppWidgetManager
import com.sky31.gongmultiplatform.MainActivity
import com.sky31.gongmultiplatform.widget.CoursesAppWidget

actual suspend fun updateAppWidget() {
    val context = MainActivity.getInstance()

    if(context == null) {
        throw Exception("No MainActivity instance!")
    } else {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(CoursesAppWidget::class.java)
        glanceIds.forEach {
            CoursesAppWidget().update(context, it)
        }
    }
}