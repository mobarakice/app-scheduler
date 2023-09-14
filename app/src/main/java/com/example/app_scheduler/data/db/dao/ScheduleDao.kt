package com.example.app_scheduler.data.db.dao

import androidx.room.*
import com.example.app_scheduler.data.db.entity.Schedule
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule)

    @Query("SELECT * FROM Schedule ORDER BY time DESC")
    fun observeSchedule(): Flow<List<Schedule>>


    @Query("SELECT * FROM Schedule WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: String): Schedule?

    @Update
    suspend fun updateSchedule(schedule: Schedule): Int

    @Delete
    suspend fun cancelSchedule(schedule: Schedule): Int

}