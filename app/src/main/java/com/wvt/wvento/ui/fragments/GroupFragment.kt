package com.wvt.wvento.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.wvt.wvento.R
import com.wvt.wvento.adapter.EventAdapter
import com.wvt.wvento.adapter.SearchCtgAdapter
import com.wvt.wvento.databinding.FragmentGroupBinding
import com.wvt.wvento.models.CtgModel
import com.wvt.wvento.util.NetworkListener
import com.wvt.wvento.util.NetworkResult
import com.wvt.wvento.util.observeOnce
import com.wvt.wvento.viewModel.EventViewModel
import com.wvt.wvento.viewModel.PrefViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GroupFragment : Fragment() {

    private lateinit var binding: FragmentGroupBinding

    private lateinit var auth: FirebaseAuth

    private var selectedLocation: String? = null
    private var selectedLocPos: Int = 0

    private lateinit var viewModel: EventViewModel
    private lateinit var prefViewModel: PrefViewModel
    private lateinit var networkListener: NetworkListener

    private val myAdapter by lazy { EventAdapter() }

    private val ctgList = generateList()

    private val ctgAdapter by lazy { SearchCtgAdapter(ctgList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[EventViewModel::class.java]

        prefViewModel = ViewModelProvider(requireActivity())[PrefViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupBinding.inflate(inflater, container, false)

        setUpRecyclerview()

        prefViewModel.readBackOnline.observe(viewLifecycleOwner) {
            prefViewModel.backOnline = it
        }

        lifecycleScope.launchWhenStarted {

            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status->
                prefViewModel.networkStatus = status
                prefViewModel.showNetworkStatus()
                readDatabase()
            }
        }

        binding.refreshLayout.setOnRefreshListener {
            requestApiData()
            binding.refreshLayout.isRefreshing = false
        }

        binding.evtNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_GroupFragment_to_notificationFragment)
        }

        binding.chgLocation.setOnClickListener {
            showLocationDialog()
        }

        binding.nestedScrollView.setOnScrollChangeListener { v: NestedScrollView, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY > oldScrollY) {
                    binding.addEvent.hide()
                }
                else {
                    binding.addEvent.show()
                }
            }
        }

        binding.addEvent.setOnClickListener {
            findNavController().navigate(R.id.action_GroupFragment_to_newEventActivity)
        }

        fetchUserData()

        return binding.root
    }

    private fun readDatabase() {

        viewModel.localEvents.observeOnce(viewLifecycleOwner) { database ->
            if(database.isNotEmpty()) {

                myAdapter.setData(database.first().event)
                hideShimmerEffect()

            } else {

                requestApiData()
            }
        }
    }

    private fun showLocationDialog() {
        val singleItems = arrayOf(
            "al","ak","az","ca","co","de","fl","ga","hi","id","il","ina","ia","ky","la","me","md","ma","mi",
            "mn","ms","mo","mt","ne","nv","nh","nj","nc","nd","or","ri","sc","sd","tn","tx"
        )
        var checkedItem = 0

        var selected: String? = selectedLocation

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Change Location")
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, selectedLocPos) { _, which ->
                selected = singleItems[which]
                checkedItem = which
            }
            .setPositiveButton("select") { dialog, _ ->

                prefViewModel.savedToDataStore(selected.toString(),checkedItem)

                lifecycleScope.launch {
                    delay(1000)
                    requestApiData()
                }

                Toast.makeText(requireContext(),selected, Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
            .show()
    }

    private fun requestApiData() {

        observeData()
        showShimmerEffect()

        viewModel.getLocal(selectedLocation.toString())

        viewModel.localResponse.observe(viewLifecycleOwner) { response ->
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

    private fun observeData() {
        prefViewModel.readLocationStore.observe(viewLifecycleOwner) { value ->
            selectedLocation = value.selectedLocationType
            selectedLocPos = value.selectedLocationPos
        }
    }

    private fun loadDataFromCache() {
        lifecycleScope.launch {
            viewModel.localEvents.observe(viewLifecycleOwner) { database ->
                if (database.isNotEmpty()) {
                    myAdapter.setData(database.first().event)
                }
            }
        }
    }

    private fun generateList(): ArrayList<CtgModel> {

        val list = ArrayList<CtgModel>()
        list.add(CtgModel("StandUp Comedy","StandUp\nComedy",R.drawable.ic_comedy,"Expressive, Concise, and Powerful"))
        list.add(CtgModel("National Days","National\nDays",R.drawable.ic_national,""))
        list.add(CtgModel("College Fests","College\nFests",R.drawable.ic_college,""))
        list.add(CtgModel("Music & Dance","Music &\nDance",R.drawable.ic_music,""))
        list.add(CtgModel("Party Meetings","Party\nMeetings",R.drawable.ic_party,""))
        list.add(CtgModel("Sports Tournament","Sports",R.drawable.ic_sports,""))
        list.add(CtgModel("Technical Event","Technical\nEvents",R.drawable.ic_technical,""))
        list.add(CtgModel("Festival Days","Festivals",R.drawable.ic_festival,""))
        list.add(CtgModel("Yoga & Meditation","Yoga &\nWellness",R.drawable.yoga,""))
        list.add(CtgModel("Cultural Days","Cultural\nEvents",R.drawable.ic_cultural,""))
        list.add(CtgModel("Party Nights","Party\nNights",R.drawable.ic_night,""))
        list.add(CtgModel("Fan Clubs","Fan\nClubs",R.drawable.ic_club,""))
        return list

    }

    private fun setUpRecyclerview() {
        binding.rvCtg.apply {
            adapter = ctgAdapter
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            setHasFixedSize(true)
        }

        binding.rvLtnEvents.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.rvLtnEvents.setHasFixedSize(true)
        }

        showShimmerEffect()

    }

    private fun showShimmerEffect() {
        binding.shimmerFrameLayout.shimmerFrameLayout.startShimmer()
        binding.shimmerFrameLayout.shimmerFrameLayout.visibility = View.VISIBLE

        binding.rvContainer.visibility = View.GONE
    }

    private fun hideShimmerEffect() {
        binding.shimmerFrameLayout.shimmerFrameLayout.stopShimmer()
        binding.shimmerFrameLayout.shimmerFrameLayout.visibility = View.GONE

        binding.rvContainer.visibility = View.VISIBLE
    }


    private fun fetchUserData() {
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.let {
            binding.userName.text = user.displayName
        }
    }
}
