package com.example.app_scheduler.ui.installapps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_scheduler.data.model.AppInfo
import com.example.app_scheduler.databinding.FragmentInstallappsBinding
import com.example.app_scheduler.ui.addedit.AddEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InstallAppsFragment : Fragment() {

    private var _binding: FragmentInstallappsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AddEditViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInstallappsBinding.inflate(inflater, container, false)
        setUpStateFlowObservers()
        return binding.root
    }

    private fun setUpStateFlowObservers(){
        Log.i(TAG,"setUpStateFlowObservers()>>start")
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appInfos.collect {
                    setupListAdapter(it)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect {
                Log.i(TAG,"isLoading: $it")
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        Log.i(TAG,"setUpStateFlowObservers()>>end")
    }

    private fun setupListAdapter(items: List<AppInfo>) {
        Log.i(TAG,"setupListAdapter()>>start")
        val adapter = InstallAppsAdapter(items) { item->
            Log.i(TAG,"setupListAdapter()>>item clicked>> $item")
            viewModel._appInfo.value = item
            findNavController().popBackStack()
        }
        val layoutManager = LinearLayoutManager(context)
        binding.list.layoutManager = layoutManager
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        binding.list.addItemDecoration(divider)
        binding.list.adapter = adapter
        Log.i(TAG,"setupListAdapter()>>end")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "InstallAppsFragment"
    }
}