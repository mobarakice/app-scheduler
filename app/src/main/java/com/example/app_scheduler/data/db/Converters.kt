package com.example.app_scheduler.data.db

import androidx.room.TypeConverter
import com.example.app_scheduler.data.model.Failed
import com.example.app_scheduler.data.model.ScheduleState
import com.example.app_scheduler.data.model.Scheduled
import com.example.app_scheduler.data.model.Success
import java.util.Date

class Converters {
  @TypeConverter
  fun fromTimestamp(value: Long?): Date? {
    return value?.let { Date(it) }
  }

  @TypeConverter
  fun dateToTimestamp(date: Date?): Long? {
    return date?.time?.toLong()
  }

  @TypeConverter
  fun fromIntToState(value: Int?): ScheduleState? {
    return value?.let {
      when(it){
        0 -> Scheduled(it)
        1 -> Success(it)
        else
          -> Failed(it)
      }
    }
  }

  @TypeConverter
  fun dateToTimestamp(state: ScheduleState?): Int? {
    return state?.status
  }
}