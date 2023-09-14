package com.example.app_scheduler.ui.scheduleapps

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_scheduler.data.db.ScheduleRepository
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.ui.utility.Utility
import com.example.app_scheduler.ui.utility.Utility.getAppIcon
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ScheduleAppsViewModel @Inject constructor(
    private val repository: ScheduleRepository,
    @ApplicationContext context: Context,
) : ViewModel() {

    private val TAG = "ScheduleAppsViewModel"

    private var _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty

    private var _apps = MutableStateFlow(emptyList<Schedule>())
    val apps: StateFlow<List<Schedule>> = _apps

    init {
        observeAllSchedules(context)
    }

    private fun observeAllSchedules(context: Context){
        Log.i(TAG,"observeAllSchedules() start")
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            repository.getAllSchedule().map { items ->
                items.onEach {
                    it.icon = pm.getAppIcon(it.packageName)
                }
            }.collect {
                _apps.value = it
                _isEmpty.value = it.isEmpty()
            }
        }
        Log.i(TAG,"observeAllSchedules() end")
    }

    fun cancel(context: Context, schedule: Schedule) {
        Log.i(TAG,"cancel() schedule: $schedule")
        viewModelScope.launch(Dispatchers.IO) {
            repository.cancelSchedule(schedule)
            Utility.cancelWorkById(context, schedule.id)
        }
    }
}