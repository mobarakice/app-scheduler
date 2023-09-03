package com.example.app_scheduler.data.model

sealed class ScheduleState(open val status: Int)
data class Scheduled(override val status: Int = 0) : ScheduleState(status)
data class Success(override val status: Int = 1) : ScheduleState(status)
data class Failed(override val status: Int = 2) : ScheduleState(status)
