package com.wvt.wvento.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wvt.wvento.R
import com.wvt.wvento.adapter.ExploreAdapter
import com.wvt.wvento.databinding.FragmentSearchBinding
import com.wvt.wvento.util.NetworkListener
import com.wvt.wvento.util.NetworkResult
import com.wvt.wvento.util.observeOnce
import com.wvt.wvento.viewModel.EventViewModel
import com.wvt.wvento.viewModel.PrefViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var viewModel: EventViewModel
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var networkListener: NetworkListener

    private val myAdapter by lazy { ExploreAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
    }

    override fun onResume() {
        super.onResume()
        if(viewModel.recyclerViewState != null){
            binding.rvExplore.layoutManager?.onRestoreInstanceState(viewModel.recyclerViewState)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding =  FragmentSearchBinding.inflate(inflater, container, false)

        binding.searchView.setOnClickListener {
            findNavController().navigate(R.id.action_searchFragment_to_exploreFragment)
        }

        setUpRecyclerview()

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status->
                Log.d("NetworkListener", status.toString())
                prefViewModel.networkStatus = status
                prefViewModel.showNetworkStatus()
                readDatabase()
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            requestApiData()
            binding.refreshLayout.isRefreshing = false
        }

        return binding.root
    }

    private fun setUpRecyclerview() {
        binding.rvExplore.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
        }
        showShimmerEffect()

    }

    private fun readDatabase() {

        lifecycleScope.launch {
            viewModel.exploreEvents.observeOnce(viewLifecycleOwner) { database ->
                if(database.isNotEmpty()){
                    Log.d("NewEventFragment","readDatabase called")
                    myAdapter.setData(database)
                    hideShimmerEffect()
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData() {

        showShimmerEffect()
        viewModel.getExplore()
        viewModel.exploreResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success<*> -> {
                    hideShimmerEffect()
                    response.data?.let { myAdapter.setData(it) }
                }
                is NetworkResult.Error<*> -> {
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
                is NetworkResult.Loading<*> -> {
                    showShimmerEffect()
                }
            }
        }
    }

    private fun showShimmerEffect() {

        binding.searchProgress.visibility = View.VISIBLE
        binding.rvExplore.visibility = View.GONE

    }

    private fun hideShimmerEffect() {

        binding.searchProgress.visibility = View.GONE
        binding.rvExplore.visibility = View.VISIBLE

    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.exploreEvents.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    myAdapter.setData(database)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.recyclerViewState = binding.rvExplore.layoutManager?.onSaveInstanceState()
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        exitTransition = MaterialFadeThrough()
//        enterTransition = MaterialFadeThrough()
//    }
}