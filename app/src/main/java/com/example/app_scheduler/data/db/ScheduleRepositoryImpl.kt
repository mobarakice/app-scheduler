package com.example.app_scheduler.data.db

import com.example.app_scheduler.data.db.entity.Schedule
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class ScheduleRepositoryImpl @Inject constructor (private val db: AppDatabase) : ScheduleRepository {

    override suspend fun insertSchedule(app: Schedule) = db.scheduleDao().insertSchedule(app)

    override suspend fun getScheduleById(id: String): Schedule? =
        db.scheduleDao().getScheduleById(id)

    override suspend fun updateSchedule(schedule: Schedule): Int =
        db.scheduleDao().updateSchedule(schedule)

    override fun getAllSchedule(): Flow<List<Schedule>> = db.scheduleDao().observeSchedule()
    override suspend fun cancelSchedule(schedule: Schedule): Int {
        return db.scheduleDao().cancelSchedule(schedule)
    }


}