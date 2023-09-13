package com.example.app_scheduler.ui.scheduleapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.databinding.ScheduleAppItemBinding
import com.example.app_scheduler.ui.utility.Utility.convertTimestampsToString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ScheduleAppsAdapter(
    private val schedules: List<Schedule>,
    private val listener: ScheduleItemClickListener
) :
    RecyclerView.Adapter<ScheduleAppsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = schedules[position]
        holder.bind(item, listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    class ViewHolder private constructor(private val itemBinding: ScheduleAppItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(schedule: Schedule, listener: ScheduleItemClickListener) {
            itemBinding.icon.setImageDrawable(schedule.icon)
            itemBinding.appName.text = schedule.appName
            itemBinding.appPackageName.text = schedule.packageName
            if(schedule.description.isNullOrEmpty()){
                itemBinding.description.visibility = View.GONE
            }else{
                itemBinding.description.text = schedule.description
                itemBinding.description.visibility = View.VISIBLE
            }
            itemBinding.appName.text = schedule.appName
            schedule.time?.let {
                itemBinding.scheduleTime.text = it.convertTimestampsToString()
            }

            itemBinding.edit.setOnClickListener {
                listener.onUpdateClick(schedule)
            }

            itemBinding.cancel.setOnClickListener {
                listener.onCancelClick(schedule)
            }

        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ScheduleAppItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}