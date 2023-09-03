package com.example.app_scheduler.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.app_scheduler.data.db.dao.ScheduleDao
import com.example.app_scheduler.data.db.entity.Schedule

@Database(entities = [Schedule::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDao(): ScheduleDao
}