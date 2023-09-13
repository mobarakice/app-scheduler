package com.example.app_scheduler.ui.installapps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_scheduler.data.model.AppInfo
import com.example.app_scheduler.databinding.InstallAppItemBinding


class InstallAppsAdapter(private val appInfos: List<AppInfo>,private val listener: OnItemClickListener) :
    RecyclerView.Adapter<InstallAppsAdapter.ViewHolder>() {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = appInfos[position]
        holder.bind(item,listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return appInfos.size
    }

    class ViewHolder private constructor(private val itemBinding: InstallAppItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(appInfo: AppInfo, listener: OnItemClickListener) {
            itemBinding.appName.text = appInfo.name
            itemBinding.icon.setImageDrawable(appInfo.icon)
            itemBinding.root.setOnClickListener{
                listener.onItemClick(appInfo)
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = InstallAppItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}