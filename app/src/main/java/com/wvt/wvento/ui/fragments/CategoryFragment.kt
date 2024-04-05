package com.wvt.wvento.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.wvt.wvento.R
import com.wvt.wvento.adapter.CategoryAdapter
import com.wvt.wvento.databinding.FragmentCategoryBinding
import com.wvt.wvento.viewModel.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class CategoryFragment : Fragment() {

    private lateinit var navBar: BottomNavigationView
    private lateinit var binding: FragmentCategoryBinding

    private lateinit var viewModel: EventViewModel

    private val myAdapter by lazy { CategoryAdapter() }

    private val args by navArgs<CategoryFragmentArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)

        binding.Toolbar.setTitleTextColor(Color.WHITE)
        binding.Toolbar.title = args.category.ctgName

        binding.Toolbar.setNavigationOnClickListener {
            navigate()
        }

        setUpRecyclerview()

        requestApiData()

        return binding.root
    }

    private fun setUpRecyclerview() {
        binding.rvCategory.adapter = myAdapter
        binding.rvCategory.layoutManager = LinearLayoutManager(activity)
    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.visibility = View.VISIBLE

        binding.rvCategory.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.visibility = View.GONE

        binding.rvCategory.visibility = View.VISIBLE
    }

    private fun requestApiData() {
        showShimmerEffect()
        viewModel.readCategory(args.category.ctgName).observe(viewLifecycleOwner) { response ->

            if(response.isNotEmpty()){
                hideShimmerEffect()
                myAdapter.setData(response)
                binding.noResult.visibility = View.GONE

            } else {
                hideShimmerEffect()
                myAdapter.setData(response)
                binding.noResult.visibility = View.VISIBLE
            }
        }
    }

    private fun navigate() {
        findNavController().popBackStack()
    }

    override fun onStart() {
        super.onStart()
        navBar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        navBar.visibility = View.VISIBLE
    }

}