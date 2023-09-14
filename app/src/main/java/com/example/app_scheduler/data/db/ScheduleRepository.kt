package com.example.app_scheduler.data.db

import com.example.app_scheduler.data.db.entity.Schedule
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    suspend fun insertSchedule(app: Schedule)
    suspend fun getScheduleById(id: String): Schedule?
    suspend fun updateSchedule(schedule: Schedule): Int
    fun getAllSchedule(): Flow<List<Schedule>>

    suspend fun cancelSchedule(schedule: Schedule): Int
}