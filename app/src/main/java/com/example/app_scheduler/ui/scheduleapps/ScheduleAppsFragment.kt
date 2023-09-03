package com.example.app_scheduler.ui.scheduleapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.databinding.FragmentScheduleappsBinding
import com.example.app_scheduler.ui.utility.DatePickerFragment
import com.example.app_scheduler.ui.utility.Utility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleAppsFragment : Fragment() {

    private var _binding: FragmentScheduleappsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ScheduleAppsViewModel by viewModels()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScheduleappsBinding.inflate(inflater, container, false)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apps.collect{
                if(it.isNotEmpty())
                setupListAdapter(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEmpty.collect {
                emptyView(it)
            }
        }
        return binding.root
    }

    private fun setupListAdapter(schedules: List<Schedule>) {
        val adapter = ScheduleAppsAdapter(schedules, object : ScheduleItemClickListener {
            override fun onUpdateClick(schedule: Schedule) {
                Log.i("Test","onUpdateClick")
                pickDateTime(schedule)
            }

            override fun onCancelClick(schedule: Schedule) {
                Log.i("Test","onCancelClick")
                context?.let { viewModel.cancel(it, schedule) }

            }

        })
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
    }

    private fun pickDateTime(schedule: Schedule){
        activity?.let { aContext->
            val arg = Bundle()
            arg.putLong(Utility.DATE_PICKER_KEY, schedule.time ?: System.currentTimeMillis())
            val fragment = DatePickerFragment {
                schedule.time = Utility.getTimeInMillis(it)
                viewModel.update(aContext, schedule)
            }
            fragment.arguments = arg
            fragment.show(aContext.supportFragmentManager, Utility.DATE_PICKER_KEY)
        }
    }

    private fun emptyView(isEmpty: Boolean){
        if(isEmpty){
            binding.textNoSchedule.visibility = View.VISIBLE
            binding.list.visibility = View.GONE
        }else{
            binding.textNoSchedule.visibility = View.GONE
            binding.list.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}