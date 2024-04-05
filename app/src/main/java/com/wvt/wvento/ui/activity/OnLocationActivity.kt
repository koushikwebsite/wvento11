package com.wvt.wvento.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.wvt.wvento.R
import com.wvt.wvento.adapter.SelectionAdapter
import com.wvt.wvento.databinding.ActivityOnLocationBinding
import com.wvt.wvento.models.SelectModel
import com.wvt.wvento.viewModel.EventViewModel
import com.wvt.wvento.viewModel.PrefViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class OnLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnLocationBinding
    private lateinit var mainViewModel: PrefViewModel

    private lateinit var viewModel: EventViewModel

    private val ltnList = generateList()

    private var chosenLocation = ""
    private var chosenPosition = 0

    private val myAdapter by lazy { SelectionAdapter(ltnList) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mainViewModel = ViewModelProvider(this@OnLocationActivity)[PrefViewModel::class.java]

        viewModel = ViewModelProvider(this@OnLocationActivity)[EventViewModel::class.java]

        val window: Window = this.window
        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)

        setUpRecyclerview()

        binding.selectLtnBtn.setOnClickListener {

            if (myAdapter.getSelected() != null) {
                chosenLocation =  myAdapter.getSelected()!!.location
                chosenPosition = myAdapter.getSelected()!!.position

                viewModel.getExplore()

                mainViewModel.savedToDataStore(chosenLocation,chosenPosition)
                startActivity(Intent(this, HomeActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
                onLocationFinished()
                finish()

            } else {

                Toast.makeText(this,"You have to select any location",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setUpRecyclerview() {
        binding.rvLtn.adapter = myAdapter
        binding.rvLtn.layoutManager = GridLayoutManager(this,3)
        binding.rvLtn.setHasFixedSize(true)
        (binding.rvLtn.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun generateList(): ArrayList<SelectModel> {

        val list = ArrayList<SelectModel>()
        list.add(SelectModel("Andhra Pradesh",R.drawable.ic_andhra_pradesh,0))
        list.add(SelectModel("Bihar",R.drawable.ic_bihar,1))
        list.add(SelectModel("Chhattisgarh",R.drawable.ic_chhattisgarh,2))
        list.add(SelectModel("Haryana",R.drawable.ic_haryana,3))
        list.add(SelectModel("Himachal Pradesh",R.drawable.ic_himachal_pradesh,4))
        list.add(SelectModel("Jharkhand",R.drawable.ic_jharkhand,5))
        list.add(SelectModel("Karnataka",R.drawable.ic_karnataka,6))
        list.add(SelectModel("Madhya Pradesh",R.drawable.ic_madhya_pradesh,7))
        list.add(SelectModel("Maharashtra",R.drawable.ic_maharashtra,8))
        list.add(SelectModel("Meghalaya",R.drawable.ic_meghalaya,9))
        list.add(SelectModel("Rajasthan",R.drawable.ic_rajasthan,10))
        list.add(SelectModel("Tamil Nadu",R.drawable.ic_tamil_nadu,11))
        list.add(SelectModel("Telangana",R.drawable.ic_telangana,12))
        list.add(SelectModel("Uttar Pradesh",R.drawable.ic_uttar_pradesh,13))
        list.add(SelectModel("Uttarakhand",R.drawable.ic_uttarakhand,14))
        return list

    }

    private fun onLocationFinished() {
        val sharedPref = getSharedPreferences("ScreenComplete", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LocFinish", true)
        editor.apply()
    }
}

