package com.example.app_scheduler.ui.addedit

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_scheduler.data.db.ScheduleRepository
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.ui.utility.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    val handle: SavedStateHandle,
    @ApplicationContext context: Context
) : ViewModel() {

    fun getAppIcon(pm: PackageManager, packageName: String): Drawable? {
        try {
            return pm.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return null;
    }

    fun update(context: Context, schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO){
            repository.updateSchedule(schedule)
            Utility.scheduleWorker(context,schedule.time?:0, schedule)
        }
    }

    fun cancel(context: Context, schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO){
            repository.cancelSchedule(schedule)
            Utility.cancelWorkById(context, schedule.id)
        }
    }
}