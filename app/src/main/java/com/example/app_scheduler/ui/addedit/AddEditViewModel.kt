package com.example.app_scheduler.ui.addedit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_scheduler.data.db.ScheduleRepository
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.data.model.AppInfo
import com.example.app_scheduler.data.model.Scheduled
import com.example.app_scheduler.ui.utility.Utility
import com.example.app_scheduler.ui.utility.Utility.convertStringToTimestamps
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel
@Inject constructor(
    private val repository: ScheduleRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    private val TAG = "AddEditViewModel"
    private var _appInfos: MutableStateFlow<List<AppInfo>> = MutableStateFlow(emptyList())
    val appInfos: StateFlow<List<AppInfo>> = _appInfos
    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private var _isFindingSchedule = MutableStateFlow(false)
    val isFindingSchedule: StateFlow<Boolean> = _isFindingSchedule
    private var _success = MutableStateFlow(false)
    var _appInfo: MutableStateFlow<AppInfo?> = MutableStateFlow(null)
    val appInfo: StateFlow<AppInfo?> = _appInfo
    var time: String = ""
    var date: String = ""
    var timestamp: Long = System.currentTimeMillis()
    private var uuid = ""


    init {
        Log.i(TAG,"init called")
        viewModelScope.launch {
            _isLoading.value = true
            getAllInstallApps(context)
            _isLoading.value = false
        }
    }

    // searching main activities labeled to be launchers of the apps
    private suspend fun getAllInstallApps(context: Context) = withContext(Dispatchers.IO) {
        Log.i(TAG,"getAllInstallApps called")
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)

        _appInfos.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(
                mainIntent,
                PackageManager.ResolveInfoFlags.of(0L)
            )
        } else {
            pm.queryIntentActivities(mainIntent, 0)
        }.map {
            AppInfo(
                it.activityInfo.loadLabel(pm).toString(),
                it.activityInfo.packageName,
                it.activityInfo.loadIcon(pm)
            )
        }
    }

    fun doSchedule(context: Context, times: String) {
        Log.i(TAG,"doSchedule called")
        viewModelScope.launch(Dispatchers.IO) {
            saveSchedule(context, times, appInfo.value)
            _success.value = true
        }
    }

    fun clearAll() {
        Log.i(TAG,"clearAll called")
        _appInfo.value = null
        time = ""
        date = ""
        _isLoading.value = false
        _isFindingSchedule.value = false
        _success.value = false
    }

    private suspend fun saveSchedule(context: Context, description: String, item: AppInfo?) {
        Log.i(TAG,"saveSchedule called")
        uuid = uuid.ifEmpty { UUID.randomUUID().toString() }
        item?.let {
            Log.i(TAG,"saveSchedule>>appInfo: $it")
            val dateTime = "$date $time".convertStringToTimestamps()
            val schedule = Schedule(
                id = uuid,
                appName = it.name,
                packageName = it.packageName,
                status = Scheduled(),
                description = description,
                time = dateTime
            )
            Log.i(TAG,"saveSchedule>>schedule: $schedule")
            repository.insertSchedule(schedule)
            dateTime?.let { timestamps ->
                Utility.scheduleWorker(context, timestamps, schedule)
            }
            clearAll()
        }
    }

    fun findScheduleById(scheduleId: String) {
        Log.i(TAG,"findScheduleById called")
        uuid = scheduleId
        if (scheduleId.isNotEmpty()) {
            _isFindingSchedule.value = true
            viewModelScope.launch {
                findSchedule(scheduleId)
                _isFindingSchedule.value = false
            }
        }
    }

    private suspend fun findSchedule(scheduleId: String) {
        Log.i(TAG,"findSchedule>>scheduleId: $scheduleId")
        val schedule = repository.getScheduleById(scheduleId)
        schedule?.let {
            Log.i(TAG,"findSchedule>> schedule: $it")
            _appInfo.value = it.getAppInfo()
        }
    }
}