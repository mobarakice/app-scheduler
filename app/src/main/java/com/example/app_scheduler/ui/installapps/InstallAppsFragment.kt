package com.example.app_scheduler.ui.installapps

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_scheduler.data.model.AppInfo
import com.example.app_scheduler.databinding.FragmentInstallappsBinding
import com.example.app_scheduler.ui.utility.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InstallAppsFragment : Fragment() {

    private var _binding: FragmentInstallappsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: InstallAppsViewModel by viewModels()


    @SuppressLint("UnsafeRepeatOnLifecycleDetector")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInstallappsBinding.inflate(inflater, container, false)
       // context?.let { viewModel.getAllInstallApps(it) }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.appInfos.collect{
                    setupListAdapter(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.success.collect{
                Toast.makeText(context?.applicationContext,"Success!",Toast.LENGTH_LONG)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch{
            viewModel.isLoading.collect{
                if(it){
                    binding.progressBar.visibility = View.VISIBLE
                }else{
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    private fun setupListAdapter(items: List<AppInfo>) {
        val adapter = InstallAppsAdapter(items) {
            showPicker(it)
        }
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
    }
    private fun showPicker(item: AppInfo){
        activity?.let {
            val dialog = DatePickerFragment{ times ->
                Log.i("Test", times)
                viewModel.doSchedule(times, item)
            }
//            val arg = Bundle()
//            arg.putLong("DatePicker",LocalDateTime.now().atZone(ZoneOffset.UTC).minusDays(1).toInstant().toEpochMilli())
//            dialog.arguments = arg
            dialog.show(it.supportFragmentManager,"datePicker")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}