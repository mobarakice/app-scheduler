package com.example.app_scheduler.ui.installapps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.app_scheduler.data.db.ScheduleRepository
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.data.model.AppInfo
import com.example.app_scheduler.data.model.Scheduled
import com.example.app_scheduler.ui.utility.Utility
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
class InstallAppsViewModel @SuppressLint("StaticFieldLeak")
@Inject constructor(
    val repository: ScheduleRepository,
    val handle: SavedStateHandle,
    @ApplicationContext private val context: Context
): ViewModel() {


    private var _appInfos: MutableStateFlow<List<AppInfo>> = MutableStateFlow(emptyList())
    val appInfos: StateFlow<List<AppInfo>> = _appInfos
    private var _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private var _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    init {
        viewModelScope.launch {
            _isLoading.value = true
            getAllInstallApps(context)
            _isLoading.value = false
        }
    }

    // searching main activities labeled to be launchers of the apps
    suspend fun getAllInstallApps(context: Context) = withContext(Dispatchers.IO){
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
            AppInfo(it.activityInfo.loadLabel(pm).toString(),
                it.activityInfo.packageName,
                it.activityInfo.loadIcon(pm))
        }
    }

    fun printInfo(resolveInfo : ResolveInfo){
        Log.i("Test","${resolveInfo.activityInfo.name} ${resolveInfo.activityInfo.packageName}")
    }

    fun doSchedule(times: String, item: AppInfo) {
        viewModelScope.launch(Dispatchers.IO){
            val uuid = UUID.randomUUID().toString()
            val time = Utility.getTimeInMillis(times)
            val schedule = Schedule(uuid,item.name,item.packageName, Scheduled(),time)
            repository.insertSchedule(schedule)
            Utility.scheduleWorker(context,time,schedule)
            _success.value = true
        }
    }
}