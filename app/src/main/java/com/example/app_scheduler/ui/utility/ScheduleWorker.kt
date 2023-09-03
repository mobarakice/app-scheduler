package com.example.app_scheduler.ui.utility

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.app_scheduler.R
import com.example.app_scheduler.SchedulerApplication

class ScheduleWorker (private val appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        Log.i("ScheduleWorker","doWork")
        val appName = inputData.getString("appName")?:""
        val id = inputData.getString("appId")?:""
        val packageName = inputData.getString("PackageName")?:""
//        try {
//            CoroutineScope(Dispatchers.IO).launch{
//                dao.updateSchedule(Schedule(id,appName,packageName,Success(),0))
//            }
//        } catch (e: Exception) {
//        }
        if(SchedulerApplication.isAppForeground){
            lunchApp()
        }else{
            createNotification(packageName,appName)
        }
        return Result.success()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = "Click to open"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun lunchApp(){
        val handler = Handler(Looper.getMainLooper())
        val packageName = inputData.getString("PackageName")
        handler.postDelayed({
            Toast.makeText(appContext, "Open app", Toast.LENGTH_SHORT).show()
            try {
                val intent = appContext.packageManager.getLaunchIntentForPackage(packageName?:"")
                appContext.startActivity(intent)
            } catch (e: NameNotFoundException) {
                Log.i("ScheduleWorker","${e.message}")
            }
        }, 0)
    }


}