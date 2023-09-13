package com.example.app_scheduler.ui.addedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.app_scheduler.R
import com.example.app_scheduler.databinding.FragmentAddEditBinding
import com.example.app_scheduler.ui.installapps.InstallAppsViewModel
import com.example.app_scheduler.ui.utility.DatePickerFragment
import com.example.app_scheduler.ui.utility.TimePickerFragment
import com.example.app_scheduler.ui.utility.Utility.TAG_DATE_PICKER
import com.example.app_scheduler.ui.utility.Utility.TAG_TIME_PICKER
import com.example.app_scheduler.ui.utility.Utility.convert24hTo12hFormat
import com.example.app_scheduler.ui.utility.Utility.convertTimestampsTo12hTime
import com.example.app_scheduler.ui.utility.Utility.convertTimestampsTodMMMyyyyFormatString
import com.example.app_scheduler.ui.utility.Utility.dateStringToTimestamps
import com.example.app_scheduler.ui.utility.Utility.ddMMyyyyTodMMMMyyyy
import com.example.app_scheduler.ui.utility.Utility.getAppIcon
import com.example.app_scheduler.ui.utility.Utility.timeStringToTimestamps
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstallAppsViewModel by activityViewModels()
    private val args: AddEditFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            viewModel.clearAll()
            findNavController().popBackStack()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        viewModel.findScheduleById(args.scheduleId)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appInfo.collect {
                it?.let {
                    val pm = context?.packageManager
                    val icon = pm?.getAppIcon(it.packageName)
                    binding.appIcon.setImageDrawable(icon)
                    binding.appName.text = it.name
                    binding.appPackageName.text = it.packageName

                    it.time?.let { dt ->
                        binding.scheduleTime.text = dt.convertTimestampsTo12hTime()
                        binding.scheduleDate.text = dt.convertTimestampsTodMMMyyyyFormatString()
                        viewModel.timestamp = dt
                    }
                    binding.inputDescription.setText(it.description)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFindingSchedule.collect {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }

        binding.viewApp.setOnClickListener {
            findNavController().navigate(R.id.action_add_edit_schedule_to_nav_install_apps)
        }
        binding.viewTime.setOnClickListener {
            showTimePicker()
        }
        binding.viewDate.setOnClickListener {
            showDatePicker()
        }
        binding.save.setOnClickListener {
            viewModel.doSchedule(binding.inputDescription.text.toString())
            findNavController().popBackStack()
        }
        binding.cancel.setOnClickListener {
            viewModel.clearAll()
            findNavController().popBackStack()
        }

        requireActivity().addMenuProvider(menuProvider)

    }

    val menuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            if (menuItem.itemId == android.R.id.home) {
                viewModel.clearAll()
                findNavController().popBackStack()
            }
            return true
        }
    }

    private fun showTimePicker() {
        activity?.let {
            val dialog = TimePickerFragment { time ->
                Log.i("Test", time)
                viewModel.time = time
                binding.scheduleTime.text = time.convert24hTo12hFormat()
                showDatePicker()
            }
            var currentTime = viewModel.time.timeStringToTimestamps()
            if(currentTime == null){
                currentTime = viewModel.timestamp
            }
            val arg = Bundle()
            arg.putLong(TAG_TIME_PICKER, currentTime)
            dialog.arguments = arg
            dialog.show(it.supportFragmentManager, TAG_TIME_PICKER)
        }
    }

    private fun showDatePicker() {
        activity?.let {
            val dialog = DatePickerFragment { date ->
                Log.i("Test", date)
                viewModel.date = date
                binding.scheduleDate.text = date.ddMMyyyyTodMMMMyyyy()
            }

            var currentDate = viewModel.date.dateStringToTimestamps()
            if(currentDate == null){
                currentDate = viewModel.timestamp
            }
            val arg = Bundle()
            arg.putLong(TAG_DATE_PICKER, currentDate)
            dialog.arguments = arg
            dialog.show(it.supportFragmentManager, TAG_DATE_PICKER)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().removeMenuProvider(menuProvider)
        _binding = null
    }

}