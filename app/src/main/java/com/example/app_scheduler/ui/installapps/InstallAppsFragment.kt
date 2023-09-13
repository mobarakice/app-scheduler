package com.example.app_scheduler.ui.installapps

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app_scheduler.R
import com.example.app_scheduler.data.model.AppInfo
import com.example.app_scheduler.databinding.FragmentInstallappsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InstallAppsFragment : Fragment() {

    private var _binding: FragmentInstallappsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: InstallAppsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentInstallappsBinding.inflate(inflater, container, false)
        // context?.let { viewModel.getAllInstallApps(it) }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appInfos.collect {
                    setupListAdapter(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.success.collect {
                Toast.makeText(context?.applicationContext, "Success!", Toast.LENGTH_LONG)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect {
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupListAdapter(items: List<AppInfo>) {
        val adapter = InstallAppsAdapter(items) { item->
            viewModel._appInfo.value = item
            findNavController().popBackStack()
        }
        val layoutManager = LinearLayoutManager(context)
        binding.list.layoutManager = layoutManager
        val divider = DividerItemDecoration(context, layoutManager.orientation)
        binding.list.addItemDecoration(divider)
        binding.list.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}