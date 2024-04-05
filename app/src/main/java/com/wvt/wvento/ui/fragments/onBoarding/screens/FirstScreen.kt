package com.wvt.wvento.ui.fragments.onBoarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentFirstScreenBinding
import com.wvt.wvento.ui.activity.LoginActivity

class FirstScreen : Fragment() {

    private lateinit var binding: FragmentFirstScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentFirstScreenBinding.inflate(inflater, container, false)

        val viewPager =  activity?.findViewById<ViewPager2>(R.id.viewPager2)

        binding.next.setOnClickListener {
            viewPager?.currentItem = 1
        }

        binding.skip.setOnClickListener {
            navigating()
        }

        return binding.root
    }

    private fun navigating() {
        startActivity( Intent(activity, LoginActivity::class.java))
        requireActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
        onBoardingFinished()
        activity?.finish()
    }

    private fun onBoardingFinished() {
        val sharedPref = requireActivity().getSharedPreferences("ScreenComplete", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("IntroFinish", true)
        editor.apply()
    }

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
//        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
//    }


}