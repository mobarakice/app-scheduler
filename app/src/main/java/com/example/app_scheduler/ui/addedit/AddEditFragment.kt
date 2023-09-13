package com.example.app_scheduler.ui.addedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.app_scheduler.R
import com.example.app_scheduler.databinding.FragmentAddEditBinding
import com.example.app_scheduler.ui.installapps.InstallAppsViewModel
import com.example.app_scheduler.ui.utility.DatePickerFragment
import com.example.app_scheduler.ui.utility.TimePickerFragment
import com.example.app_scheduler.ui.utility.Utility.getAppIcon
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddEditFragment : Fragment() {

    private var _binding: FragmentAddEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InstallAppsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.appInfo.collect{
                it?.let {
                    val pm = context?.packageManager
                    val icon = pm?.getAppIcon(it.packageName)
                    binding.appIcon.setImageDrawable(icon)
                    binding.appName.text = it.name
                    binding.appPackageName.text = it.packageName
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

    }

    private fun showTimePicker() {
        activity?.let {
            val dialog = TimePickerFragment { time ->
                Log.i("Test", time)
                viewModel.time = time
                binding.scheduleTime.text = time
                showDatePicker()
            }
//            val arg = Bundle()
//            arg.putLong("DatePicker",LocalDateTime.now().atZone(ZoneOffset.UTC).minusDays(1).toInstant().toEpochMilli())
//            dialog.arguments = arg
            dialog.show(it.supportFragmentManager, "datePicker")
        }
    }

    private fun showDatePicker() {
        activity?.let {
            val dialog = DatePickerFragment { date ->
                Log.i("Test", date)
                viewModel.date = date
                binding.scheduleDate.text = date
            }
//            val arg = Bundle()
//            arg.putLong("DatePicker",LocalDateTime.now().atZone(ZoneOffset.UTC).minusDays(1).toInstant().toEpochMilli())
//            dialog.arguments = arg
            dialog.show(it.supportFragmentManager, "datePicker")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}