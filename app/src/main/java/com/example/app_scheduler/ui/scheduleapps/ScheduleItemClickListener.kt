package com.example.app_scheduler.ui.scheduleapps

import com.example.app_scheduler.data.db.entity.Schedule

interface ScheduleItemClickListener {
    fun onUpdateClick(schedule: Schedule)
    fun onCancelClick(schedule: Schedule)
}