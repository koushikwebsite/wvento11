package com.wvt.wvento.ui.fragments.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wvt.wvento.databinding.FragmentViewPagerBinding
import com.wvt.wvento.ui.fragments.onBoarding.screens.FirstScreen
import com.wvt.wvento.ui.fragments.onBoarding.screens.SecondScreen
import com.wvt.wvento.ui.fragments.onBoarding.screens.ThirdScreen

class ViewPagerFragment : Fragment() {

    private lateinit var binding: FragmentViewPagerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentViewPagerBinding.inflate(inflater, container, false)

        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager2.adapter = adapter
        binding.dots.setViewPager2(binding.viewPager2)

        return binding.root
    }

}