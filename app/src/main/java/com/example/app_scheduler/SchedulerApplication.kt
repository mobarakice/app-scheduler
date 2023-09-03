package com.example.app_scheduler

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SchedulerApplication : Application(), LifecycleEventObserver {


    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)


    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        isAppForeground = event == Lifecycle.Event.ON_RESUME
    }

    companion object {
        var isAppForeground = false
    }


}