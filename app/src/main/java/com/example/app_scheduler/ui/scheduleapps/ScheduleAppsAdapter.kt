package com.example.app_scheduler.ui.scheduleapps

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.databinding.ScheduleAppItemBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class ScheduleAppsAdapter(private val schedules: List<Schedule>, private val listener: ScheduleItemClickListener) :
        RecyclerView.Adapter<ScheduleAppsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = schedules[position]
        holder.bind(item,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    class ViewHolder private constructor(private val itemBinding: ScheduleAppItemBinding) :
            RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(schedule: Schedule, listener: ScheduleItemClickListener){
            itemBinding.icon.setImageDrawable(schedule.icon)
            itemBinding.appName.text = schedule.appName
            itemBinding.time.text = LocalDateTime.ofInstant(Instant.ofEpochMilli(schedule.time?:0),
                ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")).toString()
            itemBinding.status.text = "Status: ${schedule.getStates()}"
            itemBinding.update.setOnClickListener{
                listener.onUpdateClick(schedule)
            }

            itemBinding.cancel.setOnClickListener{
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

/**
 * Callback for calculating the diff between two non-null items in a list.
 *
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
internal class ScheduleAppsDiffCallback : DiffUtil.ItemCallback<Schedule>() {
    override fun areItemsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Schedule, newItem: Schedule): Boolean {
        return oldItem == newItem
    }
}