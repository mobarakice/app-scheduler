package com.example.app_scheduler.data.db.entity

import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.app_scheduler.data.model.Failed
import com.example.app_scheduler.data.model.ScheduleState
import com.example.app_scheduler.data.model.Scheduled
import com.example.app_scheduler.data.model.Success
import java.util.Date

@Entity(
    tableName = "schedule",
)
data class Schedule(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "app_name") var appName: String,
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "status") var status: ScheduleState,
    @ColumnInfo(name = "time") var time: Long?,
){
    @Ignore
    var icon: Drawable? = null

    fun getStates() = when(status){
        is Scheduled -> "Running"
        is Success -> "Executed"
        is Failed -> "Failed"
    }
}

