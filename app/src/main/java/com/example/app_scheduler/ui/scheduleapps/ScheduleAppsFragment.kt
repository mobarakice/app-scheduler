package com.example.app_scheduler.ui.scheduleapps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_scheduler.data.db.entity.Schedule
import com.example.app_scheduler.databinding.FragmentScheduleappsBinding
import com.example.app_scheduler.ui.installapps.InstallAppsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleAppsFragment : Fragment() {

    private var _binding: FragmentScheduleappsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleAppsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentScheduleappsBinding.inflate(inflater, container, false)
        setUpStateFlowObservers()
        setUpClickListeners()
        return binding.root
    }
    private fun setUpStateFlowObservers(){
        Log.i(TAG,"setUpStateFlowObservers()>>start")
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.apps.collect{
                setupListAdapter(it)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isEmpty.collect {
                emptyView(it)
            }
        }
        Log.i(TAG,"setUpStateFlowObservers()>>end")
    }

    private fun setUpClickListeners(){
        Log.i(TAG,"setUpClickListeners()>>start")
        binding.fab.setOnClickListener{
            Log.i(TAG,"setUpClickListeners()>>new schedule")
            val  action = ScheduleAppsFragmentDirections.actionScheduledAppsToAddEditSchedule("")
            findNavController().navigate(action)
        }
        Log.i(TAG,"setUpClickListeners()>>end")
    }

    private fun setupListAdapter(schedules: List<Schedule>) {
        Log.i(TAG,"setupListAdapter()>>start")
        val adapter = ScheduleAppsAdapter(schedules, object : ScheduleItemClickListener {
            override fun onUpdateClick(schedule: Schedule) {
                Log.i(TAG,"onUpdateClick>>schedule: $schedule")
                val  action = ScheduleAppsFragmentDirections.actionScheduledAppsToAddEditSchedule(schedule.id)
                findNavController().navigate(action)
            }

            override fun onCancelClick(schedule: Schedule) {
                Log.i(TAG,"onCancelClick>>schedule: $schedule")
                context?.let { viewModel.cancel(it, schedule) }

            }

        })
        val layoutManager = LinearLayoutManager(context)
        binding.list.layoutManager = layoutManager
        val divider = DividerItemDecoration(context,layoutManager.orientation)
        binding.list.addItemDecoration(divider)
        binding.list.adapter = adapter
        Log.i(TAG,"setupListAdapter()>>end")
    }
    private fun emptyView(isEmpty: Boolean){
        Log.i(TAG,"emptyView()>>isEmpty: $isEmpty")
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

    companion object {
        private const val TAG = "ScheduleAppsFragment"
    }
}