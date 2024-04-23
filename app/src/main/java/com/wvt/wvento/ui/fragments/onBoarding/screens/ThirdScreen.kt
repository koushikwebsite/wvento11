package com.wvt.wvento.ui.fragments.onBoarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wvt.wvento.R
import com.wvt.wvento.databinding.FragmentThirdScreenBinding
import com.wvt.wvento.ui.activity.LoginActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ThirdScreen : Fragment() {

    private lateinit var binding: FragmentThirdScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentThirdScreenBinding.inflate(inflater, container, false)

        binding.next.setOnClickListener {
            navigating()
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
//        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ true)
//        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */ false)
//    }

}