package com.sky31.gongmultiplatform.util

enum class AppUpdateState {
    UP_TO_DATE,
    OPTIONAL_UPDATE,
    REQUIRED_UPDATE
}

fun getAppUpdateState(current: String, newest: String, least: String): AppUpdateState {
    val regex = Regex("""^(\d+(?:\.\d+)*)(?:-(.+))?$""")

    val g1 = regex.find(current)
    val g2 = regex.find(newest)
    val g3 = regex.find(least)

    val currentVersion = g1?.groupValues?.get(1)?.split(".")?.map { it.toInt() } ?: return AppUpdateState.REQUIRED_UPDATE
    val newestVersion = g2?.groupValues?.get(1)?.split(".")?.map { it.toInt() } ?: return AppUpdateState.REQUIRED_UPDATE
    val leastVersion = g3?.groupValues?.get(1)?.split(".")?.map { it.toInt() } ?: return AppUpdateState.REQUIRED_UPDATE

    for(i in 0..currentVersion.size - 1) {
        if(currentVersion[i] < leastVersion[i]) {
            return AppUpdateState.REQUIRED_UPDATE
        } else if(currentVersion[i] < newestVersion[i]) {
            return AppUpdateState.OPTIONAL_UPDATE
        }
    }

    return AppUpdateState.UP_TO_DATE
}