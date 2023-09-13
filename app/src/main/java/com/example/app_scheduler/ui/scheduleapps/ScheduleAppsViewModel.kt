package com.example.app_scheduler.ui.scheduleapps

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
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
    val repository: ScheduleRepository,
    val handle: SavedStateHandle,
    @ApplicationContext context: Context,
) : ViewModel() {

    private var _isEmpty = MutableStateFlow(false)
    val isEmpty: StateFlow<Boolean> = _isEmpty

    private var _apps = MutableStateFlow(emptyList<Schedule>())
    val apps: StateFlow<List<Schedule>> = _apps

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val pm = context.packageManager
            repository.observeSchedule().map { items ->
                items.onEach {
                    it.icon = pm.getAppIcon(it.packageName)
                }
            }.collect {
                _apps.value = it
                _isEmpty.value = it.isEmpty()
            }
        }
    }

    fun cancel(context: Context, schedule: Schedule) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.cancelSchedule(schedule)
            Utility.cancelWorkById(context, schedule.id)
        }
    }
}