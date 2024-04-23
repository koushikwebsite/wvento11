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
        binding.rvLtn.layoutManager = GridLayoutManager(this,2)
        binding.rvLtn.setHasFixedSize(true)
        (binding.rvLtn.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun generateList(): ArrayList<SelectModel> {

        val list = ArrayList<SelectModel>()
        list.add(SelectModel("al",R.drawable.al,0))
        list.add(SelectModel("ak",R.drawable.ak,1))
        list.add(SelectModel("az",R.drawable.az,2))
        list.add(SelectModel("ca",R.drawable.ca,3))
        list.add(SelectModel("co",R.drawable.co,4))
        list.add(SelectModel("de",R.drawable.de,5))
        list.add(SelectModel("fl",R.drawable.fl,6))
        list.add(SelectModel("ga",R.drawable.ga,7))
        list.add(SelectModel("hi",R.drawable.hi,8))
        list.add(SelectModel("id",R.drawable.id,9))
        list.add(SelectModel("il",R.drawable.il,10))
        list.add(SelectModel("ina",R.drawable.ina,11))
        list.add(SelectModel("ia",R.drawable.ia,12))
        list.add(SelectModel("ky",R.drawable.ky,13))
        list.add(SelectModel("la",R.drawable.la,14))
        list.add(SelectModel("me",R.drawable.me,15))
        list.add(SelectModel("md",R.drawable.md,16))
        list.add(SelectModel("ma",R.drawable.ma,17))
        list.add(SelectModel("mi",R.drawable.mi,18))
        list.add(SelectModel("mn",R.drawable.mn,19))
        list.add(SelectModel("ms",R.drawable.ms,20))
        list.add(SelectModel("mo",R.drawable.mo,21))
        list.add(SelectModel("mt",R.drawable.mt,22))
        list.add(SelectModel("ne",R.drawable.ne,23))
        list.add(SelectModel("nv",R.drawable.nv,24))
        list.add(SelectModel("nh",R.drawable.nh,25))
        list.add(SelectModel("nj",R.drawable.nj,26))
        list.add(SelectModel("nc",R.drawable.nc,27))
        list.add(SelectModel("nd",R.drawable.nd,28))
        list.add(SelectModel("or",R.drawable.or,29))
        list.add(SelectModel("ri",R.drawable.ri,30))
        list.add(SelectModel("sc",R.drawable.sc,31))
        list.add(SelectModel("sd",R.drawable.sd,32))
        list.add(SelectModel("tn",R.drawable.tn,33))
        list.add(SelectModel("tx",R.drawable.tx,34))

        return list

    }

    private fun onLocationFinished() {
        val sharedPref = getSharedPreferences("ScreenComplete", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("LocFinish", true)
        editor.apply()
    }
}

