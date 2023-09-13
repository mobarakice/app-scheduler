package com.example.app_scheduler.ui.utility

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.app_scheduler.R
import com.example.app_scheduler.SchedulerApplication
import com.example.app_scheduler.data.db.ScheduleRepository
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.data.model.Success
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltWorker
class ScheduleWorker @AssistedInject constructor(@Assisted private val appContext: Context,
                                                 @Assisted params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        Log.i("ScheduleWorker","doWork")
        try {
            val id = inputData.getString(Utility.KEY_ID)
            CoroutineScope(Dispatchers.IO).launch{
               findScheduleById(id?:"")
            }
        } catch (e: Exception) {
        }
        return Result.success()
    }

    private suspend fun findScheduleById(id:String){
        if(!id.isNullOrEmpty()){
            val repo = SchedulerApplication.application.repository
            val schedule = repo.getScheduleById(id)
            schedule?.let {
                updateSchedule(it, repo)
                if(SchedulerApplication.isAppForeground){
                    launchApp(it)
                }else{
                    createNotification(it.packageName,it.appName)
                }
            }
        }
    }

    private suspend fun updateSchedule(schedule: Schedule, repository: ScheduleRepository){
        schedule.status = Success()
        repository.updateSchedule(schedule)
    }

    private fun createNotification(packageName: String, appName: String) {
        try {
            val intent = appContext.packageManager.getLaunchIntentForPackage(packageName?:"")
            val pendingIntent =
                PendingIntent.getActivity(appContext,
                    0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or  PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(appContext, "CHANNEL_ID").apply {
                setContentIntent(pendingIntent)
                setSmallIcon(R.drawable.notifications_24px)
                setContentTitle(appName)
                setContentText("Click to open")
                priority = NotificationCompat.PRIORITY_DEFAULT
            }
            createNotificationChannel(appName)
            with(NotificationManagerCompat.from(appContext)) {
                if (ActivityCompat.checkSelfPermission(
                        appContext,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                notify(0, builder.build())
            }
        } catch (e: Exception) {
        }


    }

    private fun createNotificationChannel(name: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = "Click to open"
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun launchApp(schedule: Schedule){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            Toast.makeText(appContext, "Open app", Toast.LENGTH_SHORT).show()
            try {
                val intent = appContext.packageManager.getLaunchIntentForPackage(schedule.packageName)
                appContext.startActivity(intent)
            } catch (e: NameNotFoundException) {
                Log.i("ScheduleWorker","${e.message}")
            }
        }, 0)
    }


}