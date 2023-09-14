package com.example.app_scheduler.ui.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat.getColor
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.app_scheduler.data.db.entity.Schedule
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


object Utility {

    const val CHANNEL_ID: String = "CHANNEL_ID"
    private const val ddMMyyyyHHmm = "dd-MM-yyyy HH:mm"
    private const val dMMMMyyyyHHmm = "MMM d, yyyy h:mm a"
    private const val ddMMyyyy = "dd-MM-yyyy"
    private const val dMMMMyyyy = "MMM d, yyyy"
    private const val TIME_24H_FORMAT = "H:mm"
    private const val TIME_12H_FORMAT = "h:mm a"
    const val TAG_DATE_PICKER = "tagDatePicker"
    const val TAG_TIME_PICKER = "tagTimePicker"

    const val KEY_ID = "key-id"
    private const val TAG = "Utility"


    @SuppressLint("RestrictedApi")
    fun scheduleWorker(context: Context, time: Long, schedule: Schedule) {
        Log.i(TAG, "scheduleWorker>>schedule: $schedule")
        val timeInMillis = time - System.currentTimeMillis()
        val data = Data.Builder().put(KEY_ID, schedule.id).build()
        val constraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.NOT_REQUIRED).build()
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ScheduleWorker>()
                .setInitialDelay(timeInMillis, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .setInputData(data)
                .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(schedule.id, ExistingWorkPolicy.REPLACE, request)
        Log.i(TAG, "scheduleWorker>>scheduled")
    }

    fun cancelWorkById(context: Context, tag: String) {
        Log.i(TAG, "cancelWorkById>>tag: $tag")
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

    fun String.convert24hTo12hFormat(): String? {
        try {
            val time12HFormat = SimpleDateFormat(TIME_12H_FORMAT, Locale.US)
            val time24HFormat = SimpleDateFormat(TIME_24H_FORMAT, Locale.US)
            val time = time24HFormat.parse(this)
            return time12HFormat.format(time)
        } catch (e: Exception) {
            Log.i(TAG, "convert24hTo12hFormat>>${e.message}")
        }
        return null
    }

    fun Long.convertTimestampsTo12hTime(): String? {
        try {
            val time12HFormat = SimpleDateFormat(TIME_12H_FORMAT, Locale.US)
            return time12HFormat.format(Date(this))
        } catch (e: Exception) {
            Log.i(TAG, "convertTimestampsTo12hTime>>${e.message}")
        }
        return null
    }

    fun Long.convertTimestampsTodMMMyyyyFormatString(): String? {
        try {
            val sdf2 = SimpleDateFormat(dMMMMyyyy, Locale.US)
            return sdf2.format(Date(this))
        } catch (e: Exception) {
            Log.i(TAG, "convertTimestampsTodMMMyyyyFormatString>>${e.message}")
        }
        return null
    }

    fun Long.convertTimestampsToString(): String? {
        try {
            val dMMMMyyyyHHmm = SimpleDateFormat(dMMMMyyyyHHmm, Locale.US)
            return dMMMMyyyyHHmm.format(Date(this))
        } catch (e: Exception) {
            Log.i(TAG, "convertTimestampsToString>>${e.message}")
        }
        return null
    }

    fun String.convertStringToTimestamps(): Long? {
        try {
            val dateTime = SimpleDateFormat(ddMMyyyyHHmm, Locale.US)
            return dateTime.parse(this)?.time
        } catch (e: Exception) {
            Log.i(TAG, "convertStringToTimestamps>>${e.message}")
        }
        return null
    }

    fun String.ddMMyyyyTodMMMMyyyy(): String? {
        try {
            val sdf1 = SimpleDateFormat(ddMMyyyy, Locale.US)
            val sdf2 = SimpleDateFormat(dMMMMyyyy, Locale.US)
            sdf1.parse(this)?.let {
                return sdf2.format(it)
            }
        } catch (e: Exception) {
            Log.i(TAG, "ddMMyyyyTodMMMMyyyy>>${e.message}")
        }
        return null
    }

    fun String.dateStringToTimestamps(): Long? {
        try {
            val sdf1 = SimpleDateFormat(ddMMyyyy, Locale.US)
            return sdf1.parse(this)?.time
        } catch (e: Exception) {
            Log.i(TAG, "dateStringToTimestamps>>${e.message}")
        }
        return null
    }

    fun String.timeStringToTimestamps(): Long? {
        try {
            val time24HFormat = SimpleDateFormat(TIME_24H_FORMAT, Locale.US)
            return time24HFormat.parse(this)?.time
        } catch (e: Exception) {
            Log.i(TAG, "timeStringToTimestamps>>${e.message}")
        }
        return null
    }

    fun TextView.setColor(@ColorRes color: Int) {
        setTextColor(getColor(context, color))
    }


}
