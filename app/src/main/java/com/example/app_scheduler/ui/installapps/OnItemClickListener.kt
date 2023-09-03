package com.example.app_scheduler.ui.installapps

import com.example.app_scheduler.data.model.AppInfo

fun interface OnItemClickListener {
    fun onItemClick(appInfo: AppInfo)
}