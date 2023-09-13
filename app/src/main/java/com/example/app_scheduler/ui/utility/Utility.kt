package com.example.app_scheduler.ui.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.app_scheduler.data.db.entity.Schedule
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit


object Utility {

    const val DATE_PATTERN = "d-M-yyyy HH:mm"
    const val DATE_PICKER_KEY = "DatePicker"
    const val TIME_PICKER_KEY = "TimePicker"

    @SuppressLint("RestrictedApi")
    fun scheduleWorker(context: Context, time: Long, schedule: Schedule) {
        val timeInMillis = time - System.currentTimeMillis()
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ScheduleWorker>()
                .setInitialDelay(timeInMillis, TimeUnit.MILLISECONDS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                ).setInputData(Data.Builder().put("PackageName", schedule.packageName).build())
                .build()
        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(schedule.id, ExistingWorkPolicy.REPLACE, request)
    }

    fun getTimeInMillis(time: String): Long {
        try {
            val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
            return LocalDateTime.parse(time, formatter).toInstant(ZoneOffset.UTC)
                .toEpochMilli()

//            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MMM-dd'T'HH:mm"))
//                .toInstant(ZoneOffset.UTC)
//                .toEpochMilli()
        } catch (e: Exception) {
            Log.i("Test", "${e.message}")
        }
        return 0
    }

    fun cancelWorkById(context: Context, tag: String) {
        WorkManager.getInstance(context).cancelAllWorkByTag(tag)
    }

    fun PackageManager.getAppIcon(packageName: String): Drawable? {
        try {
            return getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null;
    }

}
