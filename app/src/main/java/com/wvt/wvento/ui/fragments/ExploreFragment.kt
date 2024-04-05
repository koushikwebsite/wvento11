package com.wvt.wvento.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wvt.wvento.R
import com.wvt.wvento.adapter.SearchAdapter
import com.wvt.wvento.databinding.FragmentExploreBinding
import com.wvt.wvento.viewModel.EventViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class ExploreFragment : Fragment() {

    private lateinit var navBar: BottomNavigationView
    private lateinit var binding: FragmentExploreBinding

    private lateinit var viewModel: EventViewModel
    private val myAdapter by lazy { SearchAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentExploreBinding.inflate(inflater, container, false)

        binding.cancel.setOnClickListener {
            findNavController().navigate(R.id.action_exploreFragment_to_searchFragment)
        }

        setUpRecyclerview()

        binding.search.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH) {

                val query = binding.search.text.toString()
                if(!TextUtils.isEmpty(query)) {
                    searchApiData(query)
                }
            }
            return@setOnEditorActionListener false
        }

        return binding.root
    }

    private fun showSoftKeyboard(view: View) {
        if (view.requestFocus()) {
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun searchApiData(searchQuery: String) {
        showShimmerEffect()
        viewModel.searchEvent(searchQuery).observe(viewLifecycleOwner) { response ->

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

    private fun setUpRecyclerview() {
        binding.rvSearch.adapter = myAdapter
        binding.rvSearch.layoutManager = LinearLayoutManager(activity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navBar = requireActivity().findViewById(R.id.bottomNavigationView)
    }

    private fun showShimmerEffect() {

        binding.searchProgress.visibility = View.VISIBLE
        binding.rvSearch.visibility = View.GONE

    }

    private fun hideShimmerEffect() {

        binding.searchProgress.visibility = View.GONE
        binding.rvSearch.visibility = View.VISIBLE

    }

    override fun onStart() {
        super.onStart()
        navBar.visibility = View.GONE

        showSoftKeyboard(binding.search)
    }

    override fun onStop() {
        super.onStop()
        navBar.visibility = View.VISIBLE
    }

}